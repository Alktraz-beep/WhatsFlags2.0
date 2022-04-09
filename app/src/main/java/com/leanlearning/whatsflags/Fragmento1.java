package com.leanlearning.whatsflags;

import static com.leanlearning.whatsflags.MainActivity.PERMISSIONS_REQUEST_READ_CONTACTS;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.leanlearning.whatsflags.FragmentoChat.ArrayAdapterContacto;
import com.leanlearning.whatsflags.FragmentoChat.Contacto;
import com.leanlearning.whatsflags.FragmentoChat.FragmentoChatListener;
import com.leanlearning.whatsflags.database.DBHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class Fragmento1 extends Fragment implements FragmentoChatListener, AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener {

    private List<String> myWhatsappContactsNames;
    private List<String> myWhatsappContactsPhones;
    private ListView lvContactos;
    private ArrayAdapterContacto adapter;
    private boolean permissionsGranted=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_fragmento1, container, false);
        lvContactos=view.findViewById(R.id.lvContactos);
        lvContactos.setOnItemClickListener(this);
        lvContactos.setOnItemLongClickListener(this);

        MainActivity mainActivity=((MainActivity) getActivity());
        mainActivity.setFragmentoChatListener(this);
        if(mainActivity.permissionGranted){
            mainActivity.actualizarContactos();
        }
        lvContactos.setDivider(null);

        return view;
    }



    /**METODOS DEL LISTENER**/

    /*Asigno el adapter al lvContactos*/
    @Override
    public void updateContactosList(ArrayAdapterContacto adapter) {
        this.adapter=adapter;
        lvContactos.setAdapter(adapter);
    }
    //cuando se de click a un contacto
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if(adapter.getItem(i).isAnalizado()){
            Intent intent=new Intent(((MainActivity)getActivity()),AnalisisWhatsflags.class);
            intent.putExtra("analisis.analizado",true);
            intent.putExtra("analisis.nombre",adapter.getItem(i).getNombre());//se inicia nueva activity y se manda el nombre
            startActivity(intent);
        }else{
            //aqui se diferencia
            Intent intent=new Intent(((MainActivity)getActivity()),AnalisisWhatsflags.class);
            intent.putExtra("analisis.analizado",false);//se inicia una nueva activity y se avisa que no tiene analisis despliega solamente como hacerlo
            intent.putExtra("analisis.nombre",adapter.getItem(i).getNombre());
            startActivity(intent);
        }
    }
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        Contacto contacto=adapter.getItem(i);
        String nombre=contacto.getNombre();
        if(contacto.isAnalizado())
            new AlertDialog.Builder(getContext()).setTitle("Eliminar chat").setMessage("Quieres eliminar tu WhatsFlags con "+nombre+"?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            adapter.remove(contacto);
                            lvContactos.setAdapter(adapter);
                            //eliminar en DB
                            delete(nombre);
                        }
                    })
                    .setNegativeButton(android.R.string.no,null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        return true;
    }
    //delete a la base de datos
    public void delete(String nombre){
        DBHelper adminSQLiteOpenHelper=new DBHelper(getContext(),"administrador",null,1);
        SQLiteDatabase sqLiteDatabase=adminSQLiteOpenHelper.getReadableDatabase();
        sqLiteDatabase.execSQL("delete from t_contactos where nombre='"+nombre+"'");
        sqLiteDatabase.close();
    }
    /**PRINT**/
    public  void print(String s){System.out.println(s);}


}