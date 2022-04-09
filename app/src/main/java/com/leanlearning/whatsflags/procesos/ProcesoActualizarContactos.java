package com.leanlearning.whatsflags.procesos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.leanlearning.whatsflags.FragmentoChat.Contacto;
import com.leanlearning.whatsflags.database.DBHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//este proceso actualiza los contactos de la base de datos
public class ProcesoActualizarContactos extends Thread{
    DBHelper dbHelper;
    ArrayList<Contacto> arrayListContactosReales=new ArrayList<>();
    ArrayList<Contacto> arrayListContactosEnBase=new ArrayList<>();
    //*CONTACTOS ACTUALIZADOS*
    ArrayList<Contacto> arrayListContactosActualizados=new ArrayList<>();
    @Override
    public void run() {
        super.run();
        for(int i=0;i<arrayListContactosReales.size();i++){//se recorre la base de datos
                insert(arrayListContactosReales.get(i).getNombre(),arrayListContactosReales.get(i).getNum());//se reinsertan y si no existen se inserta sino no se inserta
        }
        //elimina a los que no estan en los actuales
        /*for(int i=0;i<arrayListContactosEnBase.size();i++){
            if(!arrayListContactosReales.contains(arrayListContactosEnBase.get(i))){//si no esta en la actual eliminar
                delete(arrayListContactosEnBase.get(i).getNombre());
                print("se elimimo "+arrayListContactosEnBase.get(i).getNombre());
            }
        }*/

        /*se leen nuevamente de la base*/
        SQLiteDatabase sqLiteDatabase=dbHelper.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("select * from t_contactos",null);//veo si es que existe
        ArrayList<Contacto> arrayListContactosActualizadosA=new ArrayList<>();//en este se cuardan analizados
        ArrayList<Contacto> arrayListContactosActualizadosS=new ArrayList<>();//este son sin analizar
        if(cursor.moveToFirst()){
            do{
                    if(cursor.getInt(3)>0){
                        arrayListContactosActualizadosA.add(new Contacto(cursor.getString(1),true,cursor.getInt(41),cursor.getInt(42)));//se obtienen contactos de la base
                    }else{
                        arrayListContactosActualizadosS.add(new Contacto(cursor.getString(1),"",false));//se obtienen contactos de la base
                    }
            }while(cursor.moveToNext());
            cursor.close();
            sqLiteDatabase.close();
        }else{
            //print("sin contactos");
        }
        //ordenamiento
        Collections.sort(arrayListContactosActualizadosA,new Comparator<Contacto>() {
            @Override
            public int compare(Contacto contacto, Contacto t1) {
                return contacto.getNombre().compareTo(t1.getNombre());
            }
        });
        Collections.sort(arrayListContactosActualizadosS,new Comparator<Contacto>() {
            @Override
            public int compare(Contacto contacto, Contacto t1) {
                return contacto.getNombre().compareTo(t1.getNombre());
            }
        });
        //primero se añaden analizados y luego sin analizar
        for(Contacto contacto: arrayListContactosActualizadosA){
            arrayListContactosActualizados.add(contacto);
        }
        for(Contacto contacto: arrayListContactosActualizadosS){
            arrayListContactosActualizados.add(contacto);
        }
    }

    public void setValues(DBHelper dbHelper,ArrayList<Contacto> cr,ArrayList<Contacto> cb){
        this.dbHelper=dbHelper;
        this.arrayListContactosReales=cr;
        this.arrayListContactosEnBase=cb;
    }
    //inserta un nuevo contacto
    public  void insert(String nombre,String numero){
        SQLiteDatabase sqLiteDatabase=dbHelper.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("select * from t_contactos where nombre='"+nombre+"'",null);//veo si es que existe
        if(cursor.moveToFirst()){
            //System.out.println("Contacto existente");
        }else{
            ContentValues register=new ContentValues();
            register.put("nombre",nombre);
            register.put("numero",numero);
            register.put("analizado",0);
            sqLiteDatabase.insert("t_contactos",null,register);
        }
        sqLiteDatabase.close();
    }
    //elimina contactos
    public void delete(String nombre){
        SQLiteDatabase sqLiteDatabase=dbHelper.getReadableDatabase();
        sqLiteDatabase.execSQL("delete from t_contactos where nombre='"+nombre+"'");
        sqLiteDatabase.close();
        sqLiteDatabase.close();
    }
    public ArrayList<Contacto> getArrayListContactosActualizados() {
        return this.arrayListContactosActualizados;
    }
    public void print(String s){
        System.out.println(s);
    }
}
