package com.leanlearning.whatsflags.procesos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.leanlearning.whatsflags.AnalisisWhatsflags;
import com.leanlearning.whatsflags.database.DBHelper;
import com.leanlearning.whatsflags.fragmentosAnalisis.Mensaje;

import java.util.ArrayList;

//este proceso se encarga dde mostrar lo de la base de datos en el front end
public class ProcesoFrontendIndividual extends  Thread{
    AnalisisWhatsflags analisisWhatsflags;
    String nombre_c;
    boolean tipo;//false es FLAGS y true es ANALISIS
    /**VARIABLES OBTENIDAS DE LA BASE**/
    String lapso="";
    String usuario_nom1;
    String usuario_nom2;
    long tiempoRespuestaU1=0,tiempoRespuestaU2=0;
    int veces_inicio1=0,veces_inicio2=0;
    int cantidadMensajesU1=0,cantidadMensajesU2=0;
    String emoji_fav_u1="",emoji_fav_u2="";
    int frec_fav_u1=0,frec_fav_u2=0;
    String frecLinks="";
    int numLinks=0;
    int eliminados_u1=0,eliminados_u2=0;
    int dia_mas_m=0,dia_men_m=0,cantidad_mas=0,cantidad_menos=0;
    long hora_masM=0,hora_menosM =0;
    String palabras_mas_usadas="";
    int llamadas_perdidas=0,video_perdidas=0,multimedia_u1=0,multimedia_u2=0;
    String groseriasFrec="";
    float porGroserias=0.0f;
    int mensajesMadrugada=0,mensajes_al_ano =0,maxLapso =0,redflags =0,greenflags =0;
    float horas_inU1=0.0f;
    String mensajes2021="",mensajes2022="";
    String greencad="",redcad="";
    //***FLAGS
    String[] banderas_green={"","","","","","","","","",""};
    // [0]GOOD energy,[1]mensaje borrado,[2]Reciprocidad,[3]Tiempo respuesta,[4]frecuencia de conversacion,[5] mensajes en madrugada,[6]groserías,[7]video/llamadas perdidas,[8]archivos multimedia,[9]links enviados;
    String[] banderas_red={"","","","","","","","",""};
    // [0]mensaje borrado,[1]Reciprocidad,[2]inicio de conversacion,[3]lapsos sin hablar,[4]frecuencia de conversacion,[5] mensajes en madrugada,[6]groserías,[7]video/llamadas perdidas,[8]links enviados;
    //***ANALISIS
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void run() {
        super.run();
        if(tipo){
            mostrarAnalisis();
        }else{
            mostrarFlags();
        }

    }
    public void setValues(AnalisisWhatsflags analisisWhatsflags, String nombrechat,boolean tipo){
        this.analisisWhatsflags=analisisWhatsflags;
        this.nombre_c=nombrechat;
        this.tipo=tipo;
    }


    public void mostrarFlags(){
        //se obtiene el registro del nombre
        getRegistro();
        //se construye banderas red y green
        String[] aux=redcad.split("EOF");
        for(int i=0;i<aux.length;i++){
            banderas_red[i]=aux[i].replaceAll("[0-9]:","");
        }
        aux=greencad.split("EOF");
        for(int i=0;i<aux.length;i++){
            banderas_green[i]=aux[i].replaceAll("[0-9]:","");
        }
        //se crea el mensaje con las flags
        Mensaje mFlags=new Mensaje(analisisWhatsflags,analisisWhatsflags);
        mFlags.crearMensajeFlags_I(redflags,greenflags,banderas_red,banderas_green);
        analisisWhatsflags.getFlags().addChild(mFlags.getRelativ_l());
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void mostrarAnalisis(){
        //se obtiene el registro del nombre
        getRegistro();
        //LINKS es una variable a desencriptar
        String frecAux[]=frecLinks.split(",");
        ArrayList<Integer> frecuenciaLinks=new ArrayList<>();
        for(String freclink:frecAux){
            String[] flink=freclink.split(":");
            frecuenciaLinks.add(Integer.parseInt(flink[1]));
        }
        //groseriasAux tambien se desencripta para mostrar en la vista
        String[] goroseriasAux=groseriasFrec.split(",");
        ArrayList<String> groserias_unicas=new ArrayList<>();
        ArrayList<Integer> groserias_frec=new ArrayList<>();
        if(!groseriasFrec.equals("")){
            for(String groseriafrec:goroseriasAux){
                String[] grosfrec=groseriafrec.split(":");
                groserias_unicas.add(grosfrec[0]);
                groserias_frec.add(Integer.parseInt(grosfrec[1]));
            }
        }
        //se construye primero
        //LAPSO
        Mensaje mLapso=new Mensaje(analisisWhatsflags);
        mLapso.crearMensajeLapso_I(lapso);
        analisisWhatsflags.getAnalisis().addChild(mLapso.getRelativ_l());
        //TIEMPO RES
        Mensaje tiempo=new Mensaje(analisisWhatsflags);
        tiempo.crearMensajeTiempo_I(usuario_nom1,usuario_nom2,tiempoRespuestaU1,tiempoRespuestaU2);
        analisisWhatsflags.getAnalisis().addChild(tiempo.getRelativ_l());
        //INICON
        Mensaje inicon=new Mensaje(analisisWhatsflags);
        inicon.crearMensajeINICON_I(usuario_nom1,usuario_nom2,veces_inicio1,veces_inicio2);
        analisisWhatsflags.getAnalisis().addChild(inicon.getRelativ_l());
        //MTOTALES
        Mensaje mtotales=new Mensaje(analisisWhatsflags,analisisWhatsflags);
        mtotales.crearMensajeTotales_I(usuario_nom1,usuario_nom2,cantidadMensajesU1,cantidadMensajesU2);
        analisisWhatsflags.getAnalisis().addChild(mtotales.getRelativ_l());
        //Emojis
        Mensaje mEmojis=new Mensaje(analisisWhatsflags);
        mEmojis.crearMensajeEmois_I(usuario_nom1,usuario_nom2,emoji_fav_u1,emoji_fav_u2,frec_fav_u1,frec_fav_u2);
        analisisWhatsflags.getAnalisis().addChild(mEmojis.getRelativ_l());
        //Links
        Mensaje mLinks=new Mensaje(analisisWhatsflags);
        mLinks.crearMensajeLinks_I(frecuenciaLinks);
        analisisWhatsflags.getAnalisis().addChild(mLinks.getRelativ_l());
        //ELIMINADOS
        Mensaje mEliminados=new Mensaje(analisisWhatsflags,analisisWhatsflags);
        mEliminados.crearMensajeEliminados_I(usuario_nom1,usuario_nom2,eliminados_u1,eliminados_u2);
        analisisWhatsflags.getAnalisis().addChild(mEliminados.getRelativ_l());
        //GROSERIAS
        Mensaje mGroserias=new Mensaje(analisisWhatsflags,analisisWhatsflags);
        mGroserias.crearMensajeGroseria_I(groserias_unicas,groserias_frec,porGroserias);
        analisisWhatsflags.getAnalisis().addChild(mGroserias.getRelativ_l());
        //DIA MAS MENOS M
        Mensaje mDiasMasMenos=new Mensaje(analisisWhatsflags);
        mDiasMasMenos.crearMensajeDiasMasMenos(dia_mas_m,dia_men_m,hora_masM,hora_menosM,cantidad_mas,cantidad_menos,usuario_nom2);
        analisisWhatsflags.getAnalisis().addChild(mDiasMasMenos.getRelativ_l());
        //VOCABULARIO
        Mensaje mVocabulario=new Mensaje(analisisWhatsflags,analisisWhatsflags);
        mVocabulario.crearMensajeVocabulario(palabras_mas_usadas,usuario_nom2);
        analisisWhatsflags.getAnalisis().addChild(mVocabulario.getRelativ_l());

        //PERDIDAS
        Mensaje mPerdidas=new Mensaje(analisisWhatsflags);
        mPerdidas.crearMensajePerdidas(llamadas_perdidas,video_perdidas);
        analisisWhatsflags.getAnalisis().addChild(mPerdidas.getRelativ_l());
        //MULTIMEDIA
        Mensaje mMulti=new Mensaje(analisisWhatsflags);
        mMulti.crearMensajeMultimedia(multimedia_u1,multimedia_u2,usuario_nom1,usuario_nom2);
        analisisWhatsflags.getAnalisis().addChild(mMulti.getRelativ_l());
        //TIEMPO INVER
        Mensaje mInvert=new Mensaje(analisisWhatsflags);
        mInvert.crearMensajeTiempoInvertido(horas_inU1,usuario_nom1,usuario_nom2);
        analisisWhatsflags.getAnalisis().addChild(mInvert.getRelativ_l());
        //BOTON compartir
        Mensaje mCompartir=new Mensaje(analisisWhatsflags);
        mCompartir.crearMensajeCompartir();
        analisisWhatsflags.getAnalisis().addChild(mCompartir.getRelativ_l());
    }
    public void getRegistro(){
        //se lee la base de datos
        DBHelper adminSQLiteOpenHelper=new DBHelper(analisisWhatsflags,null,null,1);
        SQLiteDatabase sqLiteDatabase=adminSQLiteOpenHelper.getReadableDatabase();

        Cursor cursor=sqLiteDatabase.rawQuery("select * from t_contactos where nombre=\""+nombre_c+"\"",null);//veo si es que existe ese registro
        if(cursor.moveToFirst()){
            usuario_nom1=cursor.getString(5);
            usuario_nom2=cursor.getString(6);
            lapso=cursor.getString(7);
            tiempoRespuestaU1=cursor.getLong(8);
            tiempoRespuestaU2=cursor.getLong(9);
            veces_inicio1=cursor.getInt(10);
            veces_inicio2=cursor.getInt(11);
            cantidadMensajesU1=cursor.getInt(12);
            cantidadMensajesU2=cursor.getInt(13);
            emoji_fav_u1=cursor.getString(14);
            emoji_fav_u2=cursor.getString(15);
            frec_fav_u1=cursor.getInt(16);
            frec_fav_u2=cursor.getInt(17);
            frecLinks=cursor.getString(18);
            numLinks=cursor.getInt(19);
            eliminados_u1=cursor.getInt(20);
            eliminados_u2=cursor.getInt(21);
            dia_mas_m=cursor.getInt(22);
            dia_men_m=cursor.getInt(23);
            cantidad_mas=cursor.getInt(24);
            cantidad_menos=cursor.getInt(25);
            hora_masM=cursor.getLong(26);
            hora_menosM =cursor.getLong(27);
            palabras_mas_usadas=cursor.getString(28);
            llamadas_perdidas=cursor.getInt(29);
            video_perdidas=cursor.getInt(30);
            multimedia_u1=cursor.getInt(31);
            multimedia_u2=cursor.getInt(32);
            groseriasFrec=cursor.getString(33);
            porGroserias=cursor.getFloat(34);
            mensajesMadrugada=cursor.getInt(35);
            mensajes_al_ano =cursor.getInt(36);
            maxLapso =cursor.getInt(37);
            horas_inU1=cursor.getFloat(38);
            mensajes2021=cursor.getString(39);
            mensajes2022=cursor.getString(40);
            redflags =cursor.getInt(41);
            greenflags =cursor.getInt(42);
            redcad=cursor.getString(43);
            greencad=cursor.getString(44);
        }else{
            Toast.makeText(analisisWhatsflags, "Error al cargar chat", Toast.LENGTH_SHORT).show();
        }
        sqLiteDatabase.close();
    }
    public void print(String s){
        System.out.println(s);
    }
}
