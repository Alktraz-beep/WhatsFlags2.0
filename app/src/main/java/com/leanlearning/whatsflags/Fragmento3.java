package com.leanlearning.whatsflags;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leanlearning.whatsflags.FragmentoChat.Contacto;
import com.leanlearning.whatsflags.database.DBHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Fragmento3 extends Fragment {
    TextView titulo;
    LinearLayout contenedor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_fragmento3, container, false);
        titulo=view.findViewById(R.id.tv_titulo_mytop);
        contenedor=view.findViewById(R.id.contenedor_mytop);
        obtenerMyTop();
        return view;
    }
    public void obtenerMyTop(){
        MainActivity main=(MainActivity)getActivity();
        DBHelper adminSQLiteOpenHelper=new DBHelper(main,null,null,1);
        SQLiteDatabase sqLiteDatabase=adminSQLiteOpenHelper.getReadableDatabase();

        Cursor cursor=sqLiteDatabase.rawQuery("select * from t_contactos where analizado!='"+0+"'",null);
        String mensaje="";
        ArrayList<PersonaMytop> personas=new ArrayList<>();
        if(cursor.moveToFirst()){
            mensaje="Top de "+cursor.getString(5);
            //recabacion de datos
            personas.add(new PersonaMytop(cursor.getInt(13),cursor.getInt(42),cursor.getInt(41),cursor.getString(6)));
            while(cursor.moveToNext()){
                personas.add(new PersonaMytop(cursor.getInt(13),cursor.getInt(42),cursor.getInt(41),cursor.getString(6)));
            }
        }else{
            print("no hay contactos");
        }
        if(personas.size()>=3){
            for(PersonaMytop personaMytop:personas){
                //print("my top "+personaMytop.getNombre());
                float puntuacion=(float)((float) personaMytop.getMensajes()/1000.0f)+((float)personaMytop.getGreenflags()-(float)personaMytop.getRedflags());
                personaMytop.setPuntuacion((int) puntuacion);

                //print(personaMytop.getNombre()+" -"+puntuacion);
            }
            //se ordenan de mayor a menor
            Collections.sort(personas, new Comparator<PersonaMytop>() {
                @Override
                public int compare(PersonaMytop personaMytop, PersonaMytop personaMytop2) {
                    return ((Integer)personaMytop2.getPuntuacion()).compareTo(personaMytop.getPuntuacion());
                }
            });
            //FRONTEND
            titulo.setText(mensaje);
            LayoutInflater inflater=LayoutInflater.from(main);
            //obtener medidas de width
            int width=main.getWindowManager().getDefaultDisplay().getWidth();
            width= (int) ( width*.80);

            for(int i=0;i<personas.size();i++){
                LinearLayout persona_puntuacion=(LinearLayout) inflater.inflate(R.layout.persona_puntuacion,null,false);
                ((TextView)persona_puntuacion.findViewById(R.id.tv_posicion)).setText((i+1)+"");
                ((TextView)persona_puntuacion.findViewById(R.id.tv_nombre_mytop)).setText(personas.get(i).getNombre());
                ((TextView)persona_puntuacion.findViewById(R.id.tv_nombre_mytop)).getLayoutParams().width=(int) (width*.45);
                ((TextView)persona_puntuacion.findViewById(R.id.tv_puntuacion)).setText(personas.get(i).getPuntuacion()+" puntos");
                if(i==0){
                    ((TextView)persona_puntuacion.findViewById(R.id.iv_medalla)).setText("\uD83E\uDD47");
                }
                if(i==2){
                    ((TextView)persona_puntuacion.findViewById(R.id.iv_medalla)).setText("\uD83E\uDD48");
                }
                if(i==3){
                    ((TextView)persona_puntuacion.findViewById(R.id.iv_medalla)).setText("\uD83E\uDD49");
                }
                if(i>3){

                }
                contenedor.addView(persona_puntuacion);
                //print(persona.getNombre()+" * "+persona.getPuntuacion());
            }

        }else{
            //poner mensaje de que no hay suficientes contactos
            //Esta sección se habilita una vez que analices tres o más chats.
            titulo.setText("Esta sección se habilita una vez que analices tres o más chats.");
        }
    }
    public  void print(String s){
        System.out.println(s);
    }

    public class PersonaMytop{

        public int getMensajes() {
            return mensajes;
        }

        public void setMensajes(int mensajes) {
            this.mensajes = mensajes;
        }

        public int getGreenflags() {
            return greenflags;
        }

        public void setGreenflags(int greenflags) {
            this.greenflags = greenflags;
        }

        public int getRedflags() {
            return redflags;
        }

        public void setRedflags(int redflags) {
            this.redflags = redflags;
        }

        int mensajes;

        public int getPuntuacion() {
            return puntuacion;
        }

        public void setPuntuacion(int puntuacion) {
            this.puntuacion = puntuacion;
        }

        int puntuacion;
        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }
        String nombre;
        public PersonaMytop(int mensajes, int greenflags, int redflags,String nombre) {
            this.mensajes = mensajes;
            this.greenflags = greenflags;
            this.redflags = redflags;
            this.nombre=nombre;
        }

        int greenflags;
        int redflags;
    }
}