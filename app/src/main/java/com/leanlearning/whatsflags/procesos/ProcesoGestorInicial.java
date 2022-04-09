package com.leanlearning.whatsflags.procesos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.leanlearning.whatsflags.FragmentoChat.Contacto;
import com.leanlearning.whatsflags.database.DBHelper;

import java.util.ArrayList;

///ES TE PROCESO SUBE A LA BASE DE DATOS CADA CONTACTO CON TELEFONO
public class ProcesoGestorInicial extends Thread{
    ArrayList<Contacto> arrayListContactos;
    DBHelper adminSQLiteOpenHelper;
    @Override
    public void run() {
        super.run();
        /*AQUI SE REALIZA LA  INSERCION DE CADA DATO EN LA BASE DE DATOS*/
        for(int i=0;i<arrayListContactos.size();i++){
            insert(arrayListContactos.get(i).getNombre(),arrayListContactos.get(i).getNum());
        }

    }
    public void setValues(ArrayList<Contacto> arrayListContactos,DBHelper db){
        this.arrayListContactos=arrayListContactos;
        this.adminSQLiteOpenHelper=db;
    }

    public  void insert(String nombre,String numero){
        SQLiteDatabase sqLiteDatabase=adminSQLiteOpenHelper.getReadableDatabase();

        Cursor cursor=sqLiteDatabase.rawQuery("select * from t_contactos where nombre='"+nombre+"'",null);//veo si es que existe
        if(cursor.moveToFirst()){
            System.out.println("Contacto existente");
        }else{
            ContentValues register=new ContentValues();
            register.put("nombre",nombre);
            register.put("numero",numero);
            register.put("analizado",0);
            sqLiteDatabase.insert("t_contactos",null,register);
            sqLiteDatabase.close();
        }
    }
}
