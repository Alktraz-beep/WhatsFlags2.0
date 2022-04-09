package com.leanlearning.whatsflags;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leanlearning.whatsflags.database.DBHelper;

import java.util.ArrayList;

public class Fragmento2 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_fragmento2, container, false);

        obtenerMyFLags();
        return view;
    }

    public void obtenerMyFLags(){
        MainActivity main=(MainActivity)getActivity();
        DBHelper adminSQLiteOpenHelper=new DBHelper(main,null,null,1);
        SQLiteDatabase sqLiteDatabase=adminSQLiteOpenHelper.getReadableDatabase();

        Cursor cursor=sqLiteDatabase.rawQuery("select * from t_contactos where analizado!='"+0+"'",null);
        String nombre="";
        ArrayList<PersonaMyFlag> personas=new ArrayList<>();
        if(cursor.moveToFirst()){
            nombre=cursor.getString(5);
            //recabacion de datos
                personas.add(new PersonaMyFlag(cursor.getInt(42),cursor.getInt(41),cursor.getInt(12),cursor.getInt(13),cursor.getLong(8),cursor.getLong(9),cursor.getInt(16),cursor.getInt(17),cursor.getString(14),cursor.getString(15),cursor.getInt(29),cursor.getInt(30),cursor.getInt(10000),cursor.getInt(10),cursor.getInt(11)));
            while(cursor.moveToNext()){
                //personas.add(new PersonaMyFlag());
            }
        }else{
            print("no hay contactos");
        }

        if(personas.size()>=3){

        }else{
            //mensaje de cuando no hay mas de 3
            String mensaje="Analiza al menos 3 chats para sumar todas tus banderas";
        }
    }
    public  void print(String s){
        System.out.println(s);
    }

    public class PersonaMyFlag{
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

        public int getmR() {
            return mR;
        }

        public void setmR(int mR) {
            this.mR = mR;
        }

        public int getmE() {
            return mE;
        }

        public void setmE(int mE) {
            this.mE = mE;
        }

        public long gettRU1() {
            return tRU1;
        }

        public void settRU1(long tRU1) {
            this.tRU1 = tRU1;
        }

        public long gettRU2() {
            return tRU2;
        }

        public void settRU2(long tRU2) {
            this.tRU2 = tRU2;
        }

        public int geteE() {
            return eE;
        }

        public void seteE(int eE) {
            this.eE = eE;
        }

        public int geteR() {
            return eR;
        }

        public void seteR(int eR) {
            this.eR = eR;
        }

        public String getEmojiE() {
            return emojiE;
        }

        public void setEmojiE(String emojiE) {
            this.emojiE = emojiE;
        }

        public String getEmojiR() {
            return emojiR;
        }

        public void setEmojiR(String emojiR) {
            this.emojiR = emojiR;
        }

        public int getLlamadasPerdidas() {
            return llamadasPerdidas;
        }

        public void setLlamadasPerdidas(int llamadasPerdidas) {
            this.llamadasPerdidas = llamadasPerdidas;
        }

        public int getVideoPerdidas() {
            return videoPerdidas;
        }

        public void setVideoPerdidas(int videoPerdidas) {
            this.videoPerdidas = videoPerdidas;
        }

        public int getmBorrados() {
            return mBorrados;
        }

        public void setmBorrados(int mBorrados) {
            this.mBorrados = mBorrados;
        }

        public int getIniconU1() {
            return iniconU1;
        }

        public void setIniconU1(int iniconU1) {
            this.iniconU1 = iniconU1;
        }

        public int getIniconU2() {
            return iniconU2;
        }

        public void setIniconU2(int iniconU2) {
            this.iniconU2 = iniconU2;
        }

        int greenflags;
        int redflags;
        int mR;//mensajes enviados
        int mE;//mensajes recibidos
        long tRU1;//tiempo respuesta usuario 1
        long tRU2;//tiempo respuesta usuario 2
        int eE;//Emojis enviados
        int eR;//emojis recibidos
        String emojiE;//emoji enviado
        String emojiR;//emoji Recibido
        int llamadasPerdidas;
        int videoPerdidas;
        int mBorrados;
        int iniconU1;

        public PersonaMyFlag(int greenflags, int redflags, int mR, int mE, long tRU1, long tRU2, int eE, int eR, String emojiE, String emojiR, int llamadasPerdidas, int videoPerdidas, int mBorrados, int iniconU1, int iniconU2) {
            this.greenflags = greenflags;
            this.redflags = redflags;
            this.mR = mR;
            this.mE = mE;
            this.tRU1 = tRU1;
            this.tRU2 = tRU2;
            this.eE = eE;
            this.eR = eR;
            this.emojiE = emojiE;
            this.emojiR = emojiR;
            this.llamadasPerdidas = llamadasPerdidas;
            this.videoPerdidas = videoPerdidas;
            this.mBorrados = mBorrados;
            this.iniconU1 = iniconU1;
            this.iniconU2 = iniconU2;
        }

        int iniconU2;


    }
}