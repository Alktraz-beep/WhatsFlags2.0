package com.leanlearning.whatsflags;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.leanlearning.whatsflags.FragmentoChat.ArrayAdapterContacto;
import com.leanlearning.whatsflags.FragmentoChat.Contacto;
import com.leanlearning.whatsflags.FragmentoChat.FragmentoChatListener;
import com.leanlearning.whatsflags.database.DBHelper;
import com.leanlearning.whatsflags.procesos.ProcesoActualizarContactos;
import com.leanlearning.whatsflags.procesos.ProcesoGestorInicial;
import com.leanlearning.whatsflags.procesos.VPAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 41;
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;//permiso para leer contactos
    public boolean permissionGranted=false;


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ProgressDialog progressDialog;
    private ArrayList<String> myWhatsappContactsNames;
    private ArrayList<String> myWhatsappContactsPhones;
    private FragmentoChatListener fragmentoChatListener;


    ArrayList<Contacto> contactos;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#0E7166"));

        setContentView(R.layout.activity_main);
        tabLayout=findViewById(R.id.tabLayout);
        viewPager=findViewById(R.id.viewerpager);


        tabLayout.setupWithViewPager(viewPager);

        VPAdapter vpAdapter=new VPAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new Fragmento1(),"CHATS");
        vpAdapter.addFragment(new Fragmento2(),"MY FLAGS");
        vpAdapter.addFragment(new Fragmento3(),"MY TOP");

        viewPager.setAdapter(vpAdapter);
        viewPager.setOffscreenPageLimit(2);

        /*pidiendo permisos */
        checkPermissionsApp();
        /*Se inicia el buscador*/
        initSearchWidgets();
    }

    /**
     * ----------------------------FUNCIONES PRIMARIAS----------------------------
     **/
    //funcion que obtiene contactos con whatsapp nombres en variable myWhatsappNames y telefonos en myWhatsappPhones
    public void getContactos() {
        String[] projection    = new String[] {
                ContactsContract.RawContacts._ID, ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY,  ContactsContract.CommonDataKinds.Phone.CONTACT_ID,      ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor c = getContentResolver().query(  ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,   ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?",
                new String[] { "com.whatsapp" }, null);
        myWhatsappContactsNames = new ArrayList<String>();
        myWhatsappContactsPhones= new ArrayList<>();
        int contactNameColumn = c.getColumnIndex(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY);
        int indexNumber = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        while (c.moveToNext()) {
            //print(c.getString(contactNameColumn)+"-"+c.getString(indexNumber));
            myWhatsappContactsNames.add(c.getString(contactNameColumn));
            myWhatsappContactsPhones.add(c.getString(indexNumber));
        }
    }
    //activa la funcionalidad de busqueda
    public void initSearchWidgets() {
        androidx.appcompat.widget.SearchView searchView=findViewById(R.id.buscador);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Contacto> filteredContacto = new ArrayList<>();
                for (Contacto contacto : contactos) {
                    if (contacto.getNombre().toLowerCase().contains(newText.toLowerCase())) {
                        filteredContacto.add(contacto);
                    }
                }
                ArrayAdapterContacto adapter=new ArrayAdapterContacto(getApplicationContext(),R.layout.item_contacto_layout,filteredContacto);
                sendAdapterToFragmentListener(adapter);
                return false;
            }
        });
    }
    //actualiza los contactos cada vez que se vuelve a entrar
    public void actualizarContactos(){
        if(permissionGranted ){

            //primero se obtienen contactos actuales
            getContactos();
            //se obtienen contactos de la base

            ArrayList<Contacto> arrayListContactosReales=new ArrayList<>();
            ArrayList<Contacto> arrayListContactosEnBase=new ArrayList<>();
            for(int i=0;i<myWhatsappContactsNames.size();i++){
                arrayListContactosReales.add(new Contacto(myWhatsappContactsNames.get(i),myWhatsappContactsPhones.get(i),false));//se obtienen contactos actuales
            }
            //solo imprime
            DBHelper adminSQLiteOpenHelper=new DBHelper(this,"administrador",null,1);
            SQLiteDatabase sqLiteDatabase=adminSQLiteOpenHelper.getReadableDatabase();

            Cursor cursor=sqLiteDatabase.rawQuery("select * from t_contactos",null);//veo si es que existe
            if(cursor.moveToFirst()){
                do{
                    /*if(cursor.getString(1).equals("")){//si esta analizado

                    }*/
                    arrayListContactosEnBase.add(new Contacto(cursor.getString(1),"",false));//se obtienen contactos de la base

                }while(cursor.moveToNext());
                cursor.close();
                sqLiteDatabase.close();
            }else{
                print("no hay contactos");
            }
            //se actualizan
            if(arrayListContactosEnBase.size()>0){
                ProcesoActualizarContactos pA=new ProcesoActualizarContactos();
                pA.setValues(adminSQLiteOpenHelper,arrayListContactosReales,arrayListContactosEnBase);
                pA.run();
                try {
                    pA.join();
                }catch (Exception e){e.printStackTrace();}
                contactos= pA.getArrayListContactosActualizados();
                ArrayAdapterContacto adapter=new ArrayAdapterContacto(this,R.layout.item_contacto_layout,contactos);
                sendAdapterToFragmentListener(adapter);
            }

        }
    }
    //crea un dialog para esperar
    public void crearDialogEspera(){
        progressDialog=new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.espera_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }


    public void print(String s){
        System.out.println(s);
    }
    /**------------------------------------METODOS INTERFAZ--------------------------------------------------------------**/
    public void setFragmentoChatListener(FragmentoChatListener fragmentoChatListener){ this.fragmentoChatListener=fragmentoChatListener; }
    public void sendAdapterToFragmentListener(ArrayAdapterContacto adapter){
        this.fragmentoChatListener.updateContactosList(adapter);
    }


    /*********----------------------------PERMISOS--------------------------------------------------------------**********/

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_STORAGE_PERMISSION_REQUEST_CODE || requestCode == READ_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de cámara otorgado", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Debes otorgar permiso de cámara!", Toast.LENGTH_SHORT).show();
            }

            if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de almacenamiento otorgado", Toast.LENGTH_SHORT).show();
                //la primera vez despues de dar permisos
                //pantalla espera
                crearDialogEspera();

                getContactos();
                ArrayList<Contacto> arrayListContactos=new ArrayList<>();
                for(int i=0;i<myWhatsappContactsNames.size();i++){
                    arrayListContactos.add(new Contacto(myWhatsappContactsNames.get(i),myWhatsappContactsPhones.get(i),false));
                }
                /**Primero ordena la lista**/
                Collections.sort(arrayListContactos, new Comparator<Contacto>() {
                    @Override
                    public int compare(Contacto contacto, Contacto t1) {
                        return contacto.getNombre().compareTo(t1.getNombre());
                    }
                });
                //**SE MANDA A LA VISTA*/
                contactos=arrayListContactos;
                ArrayAdapterContacto adapter=new ArrayAdapterContacto(this,R.layout.item_contacto_layout,arrayListContactos);
                sendAdapterToFragmentListener(adapter);

                progressDialog.dismiss();

                /**PROCESO GESTOR INICIAL PARA CREAR BASE DE DATOS E INSERTAR CADA CONTACTO**/
                DBHelper dbHelper=new DBHelper(this,null,null,1);
                SQLiteDatabase db=dbHelper.getWritableDatabase();
                if(db!=null){
                    print("DB CREADA");
                }else{
                    print("ERROR AL CREAR DB");
                }
                ProcesoGestorInicial pg=new ProcesoGestorInicial();
                pg.setValues(arrayListContactos,dbHelper);
                pg.run();
            } else {
                Toast.makeText(this, "Debes otorgar permiso de Almacenamiento", Toast.LENGTH_SHORT).show();
            }
        }

    }

    //funcion para pedir permisos
    private boolean checkPermissionsApp(){
        int pC1= ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int pC2=ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

        if(pC1!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_CONTACTS}, READ_STORAGE_PERMISSION_REQUEST_CODE);
        }else{
            permissionGranted=true;
        }

        return permissionGranted;
    }

}