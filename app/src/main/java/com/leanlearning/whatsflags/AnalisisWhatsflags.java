package com.leanlearning.whatsflags;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.leanlearning.whatsflags.fragmentosAnalisis.FragmentoAnalisis1;
import com.leanlearning.whatsflags.fragmentosAnalisis.FragmentoAnalisis2;
import com.leanlearning.whatsflags.fragmentosAnalisis.Mensaje;
import com.leanlearning.whatsflags.fragmentosAnalisis.fragmentoanalisis1.FragmentoAnalisis2Listener;
import com.leanlearning.whatsflags.fragmentosAnalisis.fragmentoanalisis1.FragmentoAnalisis1Listener;
import com.leanlearning.whatsflags.procesos.ProcesoLectura;
import com.leanlearning.whatsflags.procesos.VPAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class AnalisisWhatsflags extends AppCompatActivity {
    ImageView imageView;
    public ProgressDialog progressDialog;
    /**PARA MANEJO CUANDO NO HAY ANALISIS**/
    public boolean analizado=false;
    public boolean intent=false;
    /**PARA MANEJO DE FLAGS Y ANALISIS**/
    private TabLayout tabLayout;
    private ViewPager viewPager;
    FragmentoAnalisis1Listener flags;
    FragmentoAnalisis2Listener analisis;
    /**Para el nombre**/

    private TextView tv_nombre;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analisis_whatsflags);
        getWindow().setStatusBarColor(Color.parseColor("#0E7166"));
        tabLayout=findViewById(R.id.tabLayout_analisis);
        viewPager=findViewById(R.id.viewerpager_analisis);
        tv_nombre=findViewById(R.id.nombre_analisis_tv);
        /**INICIA LAS PAGINAS DE FLAGS Y ANALISIS**/
        tabLayout.setupWithViewPager(viewPager);
        VPAdapter vpAdapter=new VPAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new FragmentoAnalisis1(),"FLAGS");
        vpAdapter.addFragment(new FragmentoAnalisis2(),"ANÁLISIS");
        viewPager.setAdapter(vpAdapter);
        viewPager.setOffscreenPageLimit(2);
        //imageView=findViewById(R.id.sticker);

        /*Bitmap myBitmap = BitmapFactory.decodeFile(ponerSticker().getAbsolutePath());
        imageView.setImageBitmap(myBitmap);*/

        /**DEACUERDO A LA FORMA DE INGRESAR SE MUESTRAN COSAS DIFERENTES**/
        if(getIntent().getAction()== Intent.ACTION_SEND_MULTIPLE || getIntent().getAction()== Intent.ACTION_SEND){
            intent=true;
            Bundle bundle=getIntent().getExtras();//obtenemos l oque mando whatsapp

            ProcesoLectura pL=new ProcesoLectura();
            pL.setValues(this,bundle,getIntent());
            pL.start();

            try {
                pL.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            //si intent es normal se recurre a la base de datos y se muestra no se llama a ningun hilo
            intent=false;//no es un intent como el de exportar
            tv_nombre.setText(getIntent().getExtras().getString("analisis.nombre"));//pone el nombre en el tv
            if(getIntent().getExtras().getBoolean("analisis.analizado")){
                //si esta analizado
                analizado=true;

            }else{
                //si no esta analizado se hace unas instrucciones de como analizar el chat
                analizado=false;
            }
        }

    }
    public File ponerSticker(){
        String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/WhatsApp/Media/WhatsApp Stickers";
        System.out.println(path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        ArrayList<String> nombres=new ArrayList<>();
        ArrayList<Long> fechas=new ArrayList<>();
        ArrayList<String> fileNames=new ArrayList<>();
        for (int i = 0; i < files.length; i++)
        {
            nombres.add(files[i].getName());
            fechas.add(files[i].lastModified());
            //Log.d("Files", "FileName:" + files[i].getName()+"  -"+  new Date(files[i].lastModified()));
        }
        for(int i=0;i<4;i++){
            //System.out.println(nombres.get(fechas.indexOf(Collections.max(fechas))));
            fileNames.add(nombres.get(fechas.indexOf(Collections.max(fechas))));
            nombres.remove(fechas.indexOf(Collections.max(fechas)));
            fechas.remove(fechas.indexOf(Collections.max(fechas)));
        }

        return  new File(path+"/"+fileNames.get(0));
    }
    //CREA DIALOGO DE ESPERA Y LO MUESTRA
    public void crearDialogEspera(){
        progressDialog=new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.espera_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
    //*BOTON DE REGRESA
    public void regresar(View view){
        Intent main=new Intent(this,MainActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(main,0);
    }
    public void ponerNombre(String s){
        this.tv_nombre.setText(s);//sirve para poner nombre del chat solo se usa e los procesos
    }
    /****METODOS DE INTERFACES***/
    //PONE UN LISTENER PARA FRAGMENTOANALISIS1 (FLAGS)++
    public void setFragmentoAnalisis1Listener(FragmentoAnalisis1Listener flags){
        this.flags=flags;
    }
    public FragmentoAnalisis1Listener getFlags(){
        return this.flags;
    }
    public void setFragmentoAnalisis2Listener(FragmentoAnalisis2Listener analisis){
        this.analisis=analisis;
    }
    public FragmentoAnalisis2Listener getAnalisis(){
        return this.analisis;
    }


}