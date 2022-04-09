package com.leanlearning.whatsflags.procesos;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.leanlearning.whatsflags.AnalisisWhatsflags;
import com.leanlearning.whatsflags.MainActivity;
import com.leanlearning.whatsflags.database.DBHelper;
import com.leanlearning.whatsflags.fragmentosAnalisis.Mensaje;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcesoIndividual extends  Thread{
    AnalisisWhatsflags analisisWhatsflags;
    ArrayList<String> datos=new ArrayList<>();//contiene linea sin \n
    ArrayList<String> lines=new ArrayList<>();//contiene linea con \n
    ArrayList<String> nombres=new ArrayList<>();//nombres de lineas
    String android_usuario;
    String usuario_nom2;
    String usuario_nom1;
    ArrayList<String> mensajesU1=new ArrayList<>(),mensajesU2=new ArrayList<>(); //contiene los mensajes por usuario
    String mensajesU1_cad="",mensajesU2_cad="";
    String[] emojis_positivos={"😃","😄","😁","😆","😀","☺️","😊","🙂","😇","😌","😍","🥰","😘","😗","😝","😛","😋","😚","😜","🤪","😙","😏","😎","🤩","🤗","🥴","🤑","🤠"};
    String[] emojis_negativos={"🤨","🧐","😒","😞","😔","😟","😕","😫","😖","😣","☹️","🙁","😩","😢","🥺","😭","😤","😠","😡","🤬","🥵","🥶","😰","😥","😓","🤔","😑","🙄","🤒","😷","🤕","🤢","🤮","🤧","😵‍"};
    int cant_emojisU1=0;
    int cant_emojisU2=0;
    ArrayList<String> dominios=new ArrayList<>();
    ArrayList<String> m_eliminados=new ArrayList<>();
    ArrayList<String> mensajes_fecha=new ArrayList<>();//guarda las fechas y las horas con mas mensajes
    ArrayList<String> mensajes_fecha_menos_frec=new ArrayList<>();//guarda las fechas y las horas con menos mensajes
    ArrayList<String> multimedia=new ArrayList<>();
    ArrayList<String> cad=new ArrayList<>();
    ArrayList<String> groserias=new ArrayList<>();
    ArrayList<String> fechas=new ArrayList<>();
    ArrayList<String> fechas_unicas=new ArrayList<>();
    String formato="";//es el formato de la fecha
    String fecha_frec;
    String fecha_menos_frec;
    String[] palabras_usadas;
    int[] frec_pal_usadas;
    int[] mensajesMes2021={0,0,0,0,0,0,0,0,0,0,0,0};
    int[] mensajesMes2022={0,0,0,0,0,0,0,0,0,0,0,0};
    /*Variables de salida*/
    String lapso="";
    private long tiempoRespuestaU2,tiempoRespuestaU1;//del bloque de tiempo respuesta
    private int veces_inicio1,veces_inicio2;//bloque quien inicia conversacion
    private int cantidadMensajesU1=0,cantidadMensajesU2=0;//bloque cantidad de mensajes
    private String emoji_fav_u1="",emoji_fav_u2="";//bloque emojis
    private int frec_fav_u1=0,frec_fav_u2=0;//bloque emojis
    private ArrayList<Integer> frecuenciaLinks=new ArrayList<>();//bloque links 0:tiktok,1:facebook,2:instagram,3:whatsApp,4:messenger,5:zoom,6:snapchat,7:telegram,8:capcut,9:meet,10:whatsapp bussines,11: picsart,12:youtube,13:twitter,14:spotify,15:pinterest,16:kuaishou,17:netflix,18:shein,19:inshot,20:por defectp
    private int numLinks=0;//bloque links
    private int eliminados_u1=0, eliminados_u2=0;//Bloque eliminados
    private int dia_mas_m=0,dia_men_m=0,cantidad_mas=0,cantidad_menos=0;//Bloque dia con mas y menos mensajes
    private long hora_masM=0,hora_menosM=0;//Bloque dia con mas y menos mensajes
    private String palabras_mas_usadas="";//BLoque palabras mas usadas word-frec separadas por *
    private int llamadas_perdidas=0,video_perdidas=0;//bloque llamadas perdidas
    private int multimedia_u1=0,multimedia_u2=0;//bloque multimedia
    private ArrayList<String> groserias_unicas=new ArrayList<>();//Bloque groserias
    private ArrayList<Integer> groserias_frec=new ArrayList<>();//bloque groserias la frecuencia groseria:frec,groseria1:frec1,...
    private float porGroserias=0.0f;//BLOQUE GROSERIAS
    private int mensajesMadrugada=0;//Bloque lapso sin hablar
    private int mensajes_al_ano=0;//Bloque banderas por frecuencia de conversacion
    long maxLapso=0;//Bloque lapso sin hablar
    float horas_inU1=0.0f;//BLOQUE TIEMPO INVERTIDO
    String mensajes2021="",mensajes2022="";//BLOQUE MENSAJES POR MES index:numero,index:numero
    private int redflags=0, greenflags=0;
    /*ESTO ES LO QUE SE MUESTRA EN LOS LAYOUTS NO SIEMPRE HAY MENSAJE*/
    private String[] banderas_green={"","","","","","","","","",""};//cant*msj
    String banderas_green_cad="";//index:cant*msj,...
    //cada indice son mensajes de banderas verdes en el siguiente orden
    // [0]GOOD energy,[1]mensaje borrado,[2]Reciprocidad,[3]Tiempo respuesta,[4]frecuencia de conversacion,[5] mensajes en madrugada,[6]groserías,[7]video/llamadas perdidas,[8]archivos multimedia,[9]links enviados
    private String[] banderas_red={"","","","","","","","",""};//cant*msj
    String banderas_red_cad="";//index:cant*msj,...
    //cada indice son mensajes de banderas rojas en el siguiente orden
    // [0]mensaje borrado,[1]Reciprocidad,[2]inicio de conversacion,[3]lapsos sin hablar,[4]frecuencia de conversacion,[5] mensajes en madrugada,[6]groserías,[7]video/llamadas perdidas,[8]links enviados

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        //barra de espera
        analisisWhatsflags.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                analisisWhatsflags.crearDialogEspera();
            }
        });
        long startTime = System.currentTimeMillis();

        super.run();
        /**ASIGNAR NOMBRES**/
        asignarNombres();
        /**BLOQUE LAPSO**/
        bloqueLapso();
        /***BLOQUE TIEMPO DE RESPUESTA***/
        bloqueTiempoRespuesta();
        /**BLOQUE QUIEN COMIENZA CONVERZACION**/
        quienComienza();
        /**BLOQUE MENSAJES TOTALES**/
        mensajesTotales();
        /**BLOQUE EMOJIS**/
        emojis();
        /**BLOQUE LINKS**/
        links();
        /**BLOQUE M ELIMINADOS**/
        mensajesEliminados();
        /**BLOQUE VOCABULARIO**/
        palabrasMasUsadas();
        /**BLOQUE GROSERIAS**/
        groserias();
        /****BLOQUE DIAS CON MAS MENSAJES**/
        diasConMasMensajes();
        /**BLOQUE LLAMADAS PERDIDAS**/
        llamadasPerdidas();
        /**BLOQUE MULTIMEDIA**/
        multimedia();
        /**MENSAJES EN MADRUGADA**/
        mensajesMadrugada();
        /**FRECUENCIA DE CONVERSACION AL AÑO**/
        frecDeConversacionAlAno();
        /**LAPSO SIN HABLAR**/
        lapsoSinHablar();
        /**TIEMPO INVERTIDO**/
        obtenerTiempoInvertido();
        /**BLOQUE MENSAJES POR MES**/
        obtenerMensajesPorMes();
        /**FORMATO PARA GROSERIAS ,FRECUENCIA DE LINKS Y MENSAJESBANDERAS**/
        String frecLinks="";
        for(int i=0;i<frecuenciaLinks.size();i++){
            frecLinks+=i+":"+frecuenciaLinks.get(i)+",";
        }
        String groseriasFrec="";
        for(int i=0;i<groserias_unicas.size();i++){
            groseriasFrec+=groserias_unicas.get(i)+":"+groserias_frec.get(i)+",";
        }
        for(int i=0;i<banderas_green.length;i++){
            banderas_green_cad+=i+":"+banderas_green[i]+"EOF";
        }
        for(int i=0;i<banderas_red.length;i++){
            banderas_red_cad+=i+":"+banderas_red[i]+"EOF";
        }
        /**MOSTRAR EN FRONTEND**/
        generarVista();
        /**UPDATE EN SUBIDA A BASE DE DATOS**/
        String fecha_analisis=Calendar.getInstance().getTime().toString();
        update(usuario_nom2,1,fecha_analisis,usuario_nom1,usuario_nom2,lapso,tiempoRespuestaU1,tiempoRespuestaU2,veces_inicio1,veces_inicio2,cantidadMensajesU1,cantidadMensajesU2,emoji_fav_u1,emoji_fav_u2,frec_fav_u1,frec_fav_u2,frecLinks,numLinks,eliminados_u1,eliminados_u2,dia_mas_m,dia_men_m,cantidad_mas,cantidad_menos,hora_masM,hora_menosM,palabras_mas_usadas,llamadas_perdidas,video_perdidas,multimedia_u1,multimedia_u2,groseriasFrec,porGroserias,mensajesMadrugada,mensajes_al_ano,maxLapso,horas_inU1,mensajes2021,mensajes2022,redflags,greenflags,banderas_red_cad,banderas_green_cad);


        long end = System.currentTimeMillis()-startTime;
        print(end/1000+" s totales");

        analisisWhatsflags.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                analisisWhatsflags.progressDialog.dismiss();
            }
        });
    }

    public void setValues(ArrayList<String> datos, ArrayList<String> lines, ArrayList<String> nombres, String nombreChat, ArrayList<String> links, ArrayList<String> eliminados, int llam_per, int vi_perd, ArrayList<String > multi, AnalisisWhatsflags analisisWhatsflags){
        this.lines=lines;
        this.nombres=nombres;
        this.datos=datos;
        this.android_usuario=nombreChat;
        this.dominios=links;
        this.m_eliminados=eliminados;
        this.llamadas_perdidas=llam_per;
        this.video_perdidas=vi_perd;
        this.multimedia=multi;
        this.analisisWhatsflags=analisisWhatsflags;
    }
    /*******-----------------------BLOQUES PRINCIPALES----------------------------------*********/
    public void asignarNombres(){
        if(android_usuario.equals(nombres.get(0))){
            usuario_nom1=nombres.get(1);
            usuario_nom2=nombres.get(0);
        }else if(android_usuario.equals(nombres.get(1))){
            usuario_nom1=nombres.get(0);
            usuario_nom2=nombres.get(1);
        }
    }
    //LAPSO
    public void bloqueLapso(){
        String fechaInicio= datos.get(0).split(" ")[0];
        String fechaFin= datos.get(datos.size()-1).split(" ")[0];
        lapso=fechaInicio+" - "+fechaFin;
    }
    //TIEMPO RESPUESTA
    public void bloqueTiempoRespuesta(){
        String regex="(\\d{1,2})(/|-)(\\d{1,2})(/|-)(\\d{2,4})(,?\\s)";
        Pattern pat=Pattern.compile(regex);
        Matcher mat;
        for(int i=0;i<datos.size();i++){
            mat= pat.matcher(datos.get(i));
            if(mat.find()){
                fechas.add(mat.group());
            }
        }
        ArrayList<Integer> frec=new ArrayList<>();
        for(int i=0;i<fechas.size();i++){
            frec.add(Collections.frequency(fechas,fechas.get(i)));
        }
        fecha_frec=fechas.get(frec.indexOf(Collections.max(frec)));
        fecha_menos_frec=fechas.get(frec.indexOf(Collections.min(frec)));
        //aqui se usa mensajes_fecha pero se volvio global
        String regex2="(\\d{1,2})(/|-)(\\d{1,2})(/|-)(\\d{2,4})(,?\\s)(\\d{1,2})(:|\\.)(\\d{2})(\\s)?([apAP]\\.?)(\\s)([mM]\\.?)(\\]?\\s-?\\s?\\s?)([^\\s]+)";
        Pattern pat2=Pattern.compile(regex2);
        Matcher mat2;
        int contador=0;
        ArrayList<ArrayArrays> datos4=new ArrayList<>();
        for(int i=0;i<datos.size();i++){
            if(datos.get(i).contains(fecha_frec)){
                mat2=pat2.matcher(datos.get(i));
                if(mat2.find()){
                    mensajes_fecha.add(mat2.group());
                    String[] datos=mat2.group().split(" ");
                    datos4.add(new ArrayArrays(contador,datos));
                    contador++;
                }
                /**ESTO SE SACA PARA EL DIA CON MENOS MENSAJES SOLO LA AÑADE A UN ARRAY PARA USARLA DESPUES**/
            }else if(datos.get(i).contains(fecha_menos_frec)){
                mat2=pat2.matcher(datos.get(i));
                if(mat2.find()){
                    mensajes_fecha_menos_frec.add(mat2.group());

                }
            }
        }

        String usuario11=usuario_nom1.split(" ")[0];
        String usuario22=usuario_nom2.split(" ")[0];
        //System.out.println(usuario11);
        //System.out.println(usuario22);
        //imprimir(mensajes_fecha);
        int n1=0, n2=1;
        String cadena1;
        String cadena2;
        String usuario111="";
        String usuario222="";
        String hora__ext = "(,?\\s)(\\d{1,2})(:|\\.)(\\d{2})(\\s)?([apAP]\\.?)(\\s)([mM]\\.?)";
        Pattern pat3=Pattern.compile(hora__ext);
        Matcher mat3;
        String[] users;
        SimpleDateFormat format=new SimpleDateFormat("HH:mm");
        ArrayList<Long> lp2=new ArrayList<>();

        while(n1<datos4.size()-1){
            cadena1=datos4.get(n1).getDatos()[4];
            cadena2=datos4.get(n2).getDatos()[4];

            users=agregarPuntos(cadena1,cadena2,usuario11,usuario22);

            if(datos4.get(n1).getDatos()[4].equals(users[0])){
                if(datos4.get(n2).getDatos()[4].equals(users[1])){
                    Date d1=null,d2=null;
                    long ht;
                    try {
                        mat3 = pat3.matcher(mensajes_fecha.get(n1));
                        if(mat3.find()) d1=format.parse(mat3.group());//print(mat3.group());
                        mat3 = pat3.matcher(mensajes_fecha.get(n2));
                        if(mat3.find()) d2=format.parse(mat3.group());//print(mat3.group());

                        if(d2.getTime()>d1.getTime()) ht=d2.getTime()-d1.getTime();
                        else if(d2.getTime()==d1.getTime()) ht=0;
                        else d2.setTime(d2.getTime()+12*60*60000);ht=d2.getTime()-d1.getTime();
                        //print(d1.getTime()+"-"+d2.getTime()+"="+ht+"");
                        lp2.add(ht);
                        usuario111=users[0];
                        usuario222=users[1];
                    }catch (Exception e){ e.printStackTrace(); }
                }
            }
            n1++;
            n2++;

        }
        long suma_tiempossr_u2=0;
        for(int i=0;i<lp2.size();i++){
            suma_tiempossr_u2+=lp2.get(i);
        }
        //print(lp2.size()+"");
        if(lp2.size()==0){
            //hay 0 minutos
            tiempoRespuestaU2=0;
        }else{
            tiempoRespuestaU2=((int)suma_tiempossr_u2/lp2.size())/1000;
            //print(tiempoRespuestaU2+"");
        }

        n1=0;n2=1;
        String u2c_s1;
        String u2c_s2;
        String u2hora__ext = "(,?\\s)(\\d{1,2})(:|\\.)(\\d{2})(\\s)?([apAP]\\.?)(\\s)([mM]\\.?)";
        Pattern pat4=Pattern.compile(u2hora__ext);
        Matcher mat4;
        ArrayList<Long> u2lpp2=new ArrayList<>();

        while(n1<datos4.size()-1){
            if(datos4.get(n1).getDatos()[4].equals(usuario222)){
                if(datos4.get(n2).getDatos()[4].equals(usuario111)){
                    Date u2h_1=null,u2h_2=null;
                    long u2ht=0;
                    try {
                        mat4 = pat4.matcher(mensajes_fecha.get(n1));
                        if(mat4.find()) u2h_1=format.parse(mat4.group());//print(mat4.group());
                        mat4 = pat4.matcher(mensajes_fecha.get(n2));
                        if(mat4.find()) u2h_2=format.parse(mat4.group());//print(mat4.group());

                        if(u2h_2.getTime()>u2h_1.getTime()) u2ht=u2h_2.getTime()-u2h_1.getTime();
                        else if(u2h_1.getTime()==u2h_2.getTime()) u2ht=0;
                        else u2h_2.setTime(u2h_2.getTime()+12*60*60000);u2ht=u2h_2.getTime()-u2h_1.getTime();
                        u2lpp2.add(u2ht);
                    }catch (Exception e){ e.printStackTrace(); }
                }
            }
            n1++;n2++;
        }
        long suma_tiempossr_u1=0;
        for(int i=0;i<u2lpp2.size();i++){
            suma_tiempossr_u1+=u2lpp2.get(i);
        }
        //print(u2lpp2.size()+"");
        /**BANDERAS**/
        if(u2lpp2.size()==0){
            //hay 0 minutos
            tiempoRespuestaU1=0;
        }else{
            tiempoRespuestaU1=((int)suma_tiempossr_u1/u2lpp2.size())/1000;
            //print(tiempoRespuestaU1+"");
        }
        if(tiempoRespuestaU1<tiempoRespuestaU2){
            greenflags+=3;
            banderas_green[3]=3+"*¡Felicidades! tuviste el menor tiempo de respuesta el día que más platicaron "+usuario_nom2+" y tú";
        }
    }
    //INICIA LA CONVERSACION
    public void quienComienza(){
        String fechas_extr= "(\\d{1,2})(/|-)(\\d{1,2})(/|-)(\\d{2,4})(,?\\s)(\\d{1,2})";
        Pattern pat=Pattern.compile(fechas_extr);
        Matcher mat;
        ArrayList<String> b5li=new ArrayList<>(),b5lista_fu=new ArrayList<>();
        ArrayList<String> cadena_inicia1=new ArrayList<>(),cadena_inicia2=new ArrayList<>();
        for(int i=0;i<lines.size();i++){
            mat=pat.matcher(lines.get(i));
            if(mat.find()) b5li.add(mat.group());
        }

        String fechas_extr2= "(\\d{1,2})(/|-)(\\d{1,2})(/|-)(\\d{2,4})";
        pat=Pattern.compile(fechas_extr2);
        for(int i=0;i<b5li.size();i++){
            mat=pat.matcher(b5li.get(i));
            if(mat.find()) if(!b5lista_fu.contains(mat.group())) b5lista_fu.add(mat.group());
        }
        for(int i=0;i<b5lista_fu.size();i++){
            int index;
            if((index=containsIn(datos,b5lista_fu.get(i)))!=-1){
                if(datos.get(index).contains(usuario_nom1)){
                    cadena_inicia1.add(datos.get(index));
                }else if(datos.get(index).contains(usuario_nom2)){
                    cadena_inicia2.add(datos.get(index));
                }
            }
        }
        veces_inicio1=cadena_inicia1.size();
        veces_inicio2=cadena_inicia2.size();

        if((veces_inicio1/(veces_inicio2+veces_inicio1))>0.6){
            redflags+=1;
            banderas_red[2]="Inicias la conversación casi siempre, "+usuario_nom2+" tiene 0 iniciativa.";
        }
    }
    //RECIPROCIDAD
    public void mensajesTotales(){
        for(int i=0;i<datos.size();i++){
            if(datos.get(i).contains(usuario_nom1)){ mensajesU1.add(datos.get(i)); mensajesU1_cad+=datos.get(i);}
            else if(datos.get(i).contains(usuario_nom2)){ mensajesU2.add(datos.get(i));mensajesU2_cad+=datos.get(i);}
        }
        cantidadMensajesU1=mensajesU1.size();
        cantidadMensajesU2=mensajesU2.size();
        if((cantidadMensajesU1/(cantidadMensajesU1+cantidadMensajesU2)*100)>=60.0 || (cantidadMensajesU2/(cantidadMensajesU1+cantidadMensajesU2)*100)>=60.0){
            redflags++;
            banderas_red[1]="¡Date cuenta! No muestran el mismo nivel de interés en la conversación.";
        }

        if((cantidadMensajesU1/(cantidadMensajesU1+cantidadMensajesU2)*100)<60.0 && (cantidadMensajesU1/(cantidadMensajesU1+cantidadMensajesU2)*100)<60.0){
            greenflags++;
            banderas_green[2]="¡Felicidades! "+usuario_nom2+" y tú muestran el mismo nivel de interés en la conversación, mandan aproximadamente el mismo número de mensajes.";
        }
    }
    //BADGOOD ENERGY
    public void emojis(){
        ArrayList<String> emojisU1=new ArrayList<>();
        ArrayList<String> emojisU2=new ArrayList<>();

        for(int i=0;i<mensajesU1.size();i++){
            if(EmojiManager.containsEmoji(mensajesU1.get(i))) emojisU1.addAll(EmojiParser.extractEmojis(mensajesU1.get(i).replaceAll(usuario_nom1,"")));
        }
        for(int i=0;i<mensajesU2.size();i++){
            if(EmojiManager.containsEmoji(mensajesU2.get(i))) emojisU2.addAll(EmojiParser.extractEmojis(mensajesU2.get(i).replaceAll(usuario_nom2,"")));
        }
        ArrayList<Integer> frecEU1=new ArrayList<>();
        int indexEmojiU1=0,indexEmojiU2=0;
        ArrayList<Integer> frecEU2=new ArrayList<Integer>();
        int frecEmojiFav1=0,frecEmojiFav2=0;
        String emojiFav1="",emojiFav2="";
        ArrayList<String> emojisTotales=new ArrayList<>();

        if(emojisU1.size()>0){
            for(int i=0;i<emojisU1.size();i++){
                frecEU1.add(Collections.frequency(emojisU1,emojisU1.get(i)));
            }
            frecEmojiFav1=Collections.max(frecEU1);
            indexEmojiU1=frecEU1.indexOf(frecEmojiFav1);
            emojiFav1=emojisU1.get(indexEmojiU1);
            emojisTotales.addAll(emojisU1);
            frec_fav_u1=frecEmojiFav1;
            emoji_fav_u1=emojiFav1;
        }
        if(emojisU2.size()>0){
            for(int i=0;i<emojisU2.size();i++){
                frecEU2.add(Collections.frequency(emojisU2,emojisU2.get(i)));
            }
            frecEmojiFav2=Collections.max(frecEU2);
            indexEmojiU2=frecEU2.indexOf(frecEmojiFav2);
            emojiFav2=emojisU2.get(indexEmojiU2);
            emojisTotales.addAll(emojisU2);
            frec_fav_u2=frecEmojiFav2;
            emoji_fav_u2=emojiFav2;
        }
        //se sacan cantidad de emojis por miembro
        cant_emojisU1=emojisU1.size();
        cant_emojisU2=emojisU2.size();
        /**BANDERAS EMOJIS**/
        if(emojisTotales.size()>0){
            int frecuenciaPositivos=0,frecuenciaNegativos=0;
            for(int i=0;i<emojis_positivos.length;i++){
                if(emojisTotales.contains(emojis_positivos[i])){frecuenciaPositivos+=Collections.frequency(emojisTotales,emojis_positivos[i]);}
            }
            for(int i=0;i<emojis_negativos.length;i++){
                if(emojisTotales.contains(emojis_negativos[i])){frecuenciaNegativos+=Collections.frequency(emojisTotales,emojis_negativos[i]);}
            }
            if(frecuenciaPositivos>frecuenciaNegativos){
                greenflags++;
                banderas_green[0]="Tu conversación tiene "+frecuenciaPositivos+" emojis de Good Energy";
            }
            //mensajes
            /*if(emojis_negativos.length!=0 && emojis_positivos.length!=0){

            }*/
        }else{
            //banderas_green[0]="Tu conversación con "+usuario_nom2+" es 0% Good Energy";
        }
    }
    //LINKS ENVIADOS
    public void links(){
        for(int i=0;i<21;i++)
            frecuenciaLinks.add(0);
        for(int i=0;i<dominios.size();i++){
            //***0
            if(dominios.get(i).contains("tiktok")){
                frecuenciaLinks.set(0,frecuenciaLinks.get(0)+1);
            }
            //***1
            else if(dominios.get(i).contains("facebook")){
                frecuenciaLinks.set(1,frecuenciaLinks.get(1)+1);
            }
            //***2
            else if(dominios.get(i).contains("instagram")){
                frecuenciaLinks.set(2,frecuenciaLinks.get(2)+1);
            }
            //***3
            else if(dominios.get(i).contains("whatsapp.com")){
                frecuenciaLinks.set(3,frecuenciaLinks.get(3)+1);
            }
            //***4
            else if(dominios.get(i).contains("messenger") || dominios.get(i).contains("m.me")){
                frecuenciaLinks.set(4,frecuenciaLinks.get(4)+1);
            }
            //***5
            else if(dominios.get(i).contains("zoom")){
                frecuenciaLinks.set(5,frecuenciaLinks.get(5)+1);
            }
            //***6
            else if(dominios.get(i).contains("snapchat")){
                frecuenciaLinks.set(6,frecuenciaLinks.get(6)+1);
            }
            //***7
            else if(dominios.get(i).contains("telegram") || dominios.get(i).contains("t.me")){
                frecuenciaLinks.set(7,frecuenciaLinks.get(7)+1);
            }
            //***8
            else if(dominios.get(i).contains("capcut")){
                frecuenciaLinks.set(8,frecuenciaLinks.get(8)+1);
            }
            //***9
            else if(dominios.get(i).contains("meet")){
                frecuenciaLinks.set(9,frecuenciaLinks.get(9)+1);
            }
            //***10
            else if(dominios.get(i).contains("bussines")){
                frecuenciaLinks.set(10,frecuenciaLinks.get(10)+1);
            }
            //***11
            else if(dominios.get(i).contains("picsart")){
                frecuenciaLinks.set(11,frecuenciaLinks.get(11)+1);
            }
            //***12
            else if(dominios.get(i).contains("youtube") || dominios.get(i).contains("youtu.be")){
                frecuenciaLinks.set(12,frecuenciaLinks.get(12)+1);
            }
            //***13
            else if(dominios.get(i).contains("twitter")){
                frecuenciaLinks.set(13,frecuenciaLinks.get(13)+1);
            }
            //***14
            else if(dominios.get(i).contains("spotify")){
                frecuenciaLinks.set(14,frecuenciaLinks.get(14)+1);
            }
            //***15
            else if(dominios.get(i).contains("pinterest") || dominios.get(i).contains("pint.it")){
                frecuenciaLinks.set(15,frecuenciaLinks.get(15)+1);
            }
            //***16
            else if(dominios.get(i).contains("kuaishou") || dominios.get(i).contains("kwaii") || dominios.get(i).contains("s.kw.ai")){
                frecuenciaLinks.set(16,frecuenciaLinks.get(16)+1);
            }
            //***17
            else if(dominios.get(i).contains("netflix")){
                frecuenciaLinks.set(17,frecuenciaLinks.get(17)+1);
            }
            //***18
            else if(dominios.get(i).contains("shein")){
                frecuenciaLinks.set(18,frecuenciaLinks.get(18)+1);
            }
            //***19
            else if(dominios.get(i).contains("inshot")){
                frecuenciaLinks.set(19,frecuenciaLinks.get(19)+1);
            }
            //***20
            else{
                frecuenciaLinks.set(20,frecuenciaLinks.get(20)+1);
            }
        }
        /**BANDERAS**/
        for(Integer integer:frecuenciaLinks){
            numLinks+=integer;
        }
        //numLinks=frecuenciaLinks.size();
        if(numLinks>15){
            greenflags++;
            banderas_green[9]="Enviarse links es lo de ustedes, se han enviado "+numLinks+" links en el chat.";
        }else{
            redflags++;
            banderas_red[8]="No les encanta enviarse links con información,¿Cierto? Solo tienen "+numLinks+" links enviados.";
        }

    }
    //MENSAJES BORRADOS
    public void mensajesEliminados(){
        for(int i=0;i<m_eliminados.size();i++){
            if(m_eliminados.get(i).contains(usuario_nom2)) eliminados_u2++;
            if(m_eliminados.get(i).contains(usuario_nom1)) eliminados_u1++;
        }
        /**BANDERAS DE LINKS**/
        if(eliminados_u2>=10) {
            redflags++;
            banderas_red[0]="¡Precaución! "+usuario_nom2+" se dedica a borrar mensajes, igual y te oculta algo.";
        }
        else if(eliminados_u2==0) {
            greenflags++;
            banderas_green[1]="Son 100% transparentes, no borran mensajes.";
        }
    }
    //GROSERIAS
    public void groserias(){
        groserias.add(" wey");groserias.add(" capullo");groserias.add(" boludo");groserias.add(" boluda");groserias.add(" zonzo");groserias.add(" zonza");
        groserias.add(" tonto");groserias.add(" tonta");groserias.add(" joder");groserias.add(" gilipollas");groserias.add(" bruto");groserias.add(" bruta");groserias.add("coño");groserias.add("conchesumare");
        groserias.add(" babosa");groserias.add(" burro");groserias.add(" burra");groserias.add(" ojete");groserias.add(" idiota");groserias.add("mamón");groserias.add(" mamona");groserias.add(" cabrona");groserias.add("cabrón");
        groserias.add(" puñetas");groserias.add(" culo");groserias.add(" huevos");groserias.add(" pito");groserias.add(" estupida");groserias.add("estupido");groserias.add(" chale");groserias.add(" puta");groserias.add("mierda");
        groserias.add(" verga");groserias.add(" pendejo");groserias.add(" culero");groserias.add(" puto");groserias.add("puta madre");groserias.add("pinche pendejo");groserias.add("pinche pendeja");groserias.add("vale verga");groserias.add("lame huevos");
        groserias.add(" pendeja");groserias.add("zorra");groserias.add("pinches mamadas");groserias.add("pinche perro");groserias.add("no mames");groserias.add("valiendo madre");groserias.add("vale madres");groserias.add("no chingues");groserias.add("no jodas");
        groserias.add("a huevo");groserias.add("al chile");groserias.add("no friegues");groserias.add("hijo de puta");groserias.add("hija de puta");groserias.add("hija de perra");groserias.add("hijo de perra");groserias.add("chinga tu madre");
        groserias.add("hijo de la chingada");groserias.add("hija de la chingada");groserias.add("vete a la verga");

        for(int i=0;i<datos.size();i++){
            obtenerGroserias(datos.get(i));
        }
        int suma=0;
        for(int i=0;i<groserias_unicas.size();i++){
            suma+=groserias_frec.get(i);
        }
        /**BANDERAS**/
        int total=cad.size();
        if(total>0){
            porGroserias=(((float)suma/(float)total)*100.0f);
            if(porGroserias<=2.0f){
                greenflags++;
                banderas_green[6]="¿Puros y castos? "+usuario_nom2+" y tú lo son, nunca dicen groserías.";
            }else{
                redflags++;
                banderas_red[6]="Todo mal, "+usuario_nom2+" y tú dicen muchísimas groserías.";
            }
        }else{
            //no hay groserias
            greenflags++;
            banderas_green[6]="¿Puros y castos? "+usuario_nom2+" y tú lo son, nunca dicen groserías.";
        }
    }
    //FIAS MAS MENSAJES
    public void diasConMasMensajes(){
        String fechaRegex="(\\d{1,2})(/|-)(\\d{1,2})(/|-)(\\d{2,4})(,?\\s)(\\d{1,2})(:|\\.)(\\d{2})(\\s)?([apAP]\\.?)(\\s)([mM]\\.?)(\\]?\\s-?\\s?\\s?)([^\\s]+)";
        Pattern pat=Pattern.compile(fechaRegex);
        Matcher mat;
        mat=pat.matcher(mensajes_fecha.get(0));
        /**SE MUESTREA EL FORMATO PARA CONVERTIR A FECHA**/
        String muestra="";
        if(mat.find())
            muestra=mat.group(1)+mat.group(2)+mat.group(3)+mat.group(4)+mat.group(5);
        formato=muestra.replaceAll("([0-9]+)(/)([0-9]+)(/)(\\d{4})","dd/MM/yyyy");
        formato=formato.replaceAll("([0-9]+)(-)([0-9]+)(-)(\\d{4})","dd-MM-yyyy");
        formato=formato.replaceAll("([0-9]+)(/)([0-9]+)(/)(\\d{2})","dd/MM/yy");
        formato=formato.replaceAll("([0-9]+)(-)([0-9]+)(-)(\\d{2})","dd-MM-yy");

        Date fecha,fecha_menos;
        SimpleDateFormat format=new SimpleDateFormat(formato);
        Date hora,hora_menos;
        SimpleDateFormat formatHour=new SimpleDateFormat("HH");

        try{
                /*CON MAS MENSAJES*/
                mat= pat.matcher(mensajes_fecha.get(0));
                if(mat.find()){
                    fecha=format.parse(mat.group(1)+mat.group(2)+mat.group(3)+mat.group(4)+mat.group(5));
                    dia_mas_m=fecha.getDay();
                    hora =formatHour.parse(mat.group(7));
                    hora_masM=hora.getHours();
                }
                /*CON MENOS MENSAJES*/
                if(fecha_frec!=fecha_menos_frec){
                    mat= pat.matcher(mensajes_fecha_menos_frec.get(0));
                    if(mat.find()){
                        fecha_menos=format.parse(mat.group(1)+mat.group(2)+mat.group(3)+mat.group(4)+mat.group(5));
                        dia_men_m=fecha_menos.getDay();
                        hora_menos =formatHour.parse(mat.group(7));
                        hora_menosM=hora_menos.getHours();
                    }
                    cantidad_mas=mensajes_fecha.size();
                    cantidad_menos=mensajes_fecha_menos_frec.size();
                } else{//si son el mismo dia
                    dia_men_m=dia_mas_m;
                    hora_menosM=hora_masM;
                    cantidad_mas=mensajes_fecha.size();
                    cantidad_menos=mensajes_fecha.size();
                }
        }catch (Exception e){e.printStackTrace();}

    }
    //VOCABULARIO
    public void palabrasMasUsadas(){
        //long startTime = System.currentTimeMillis();
        //Obtencion de palabras mas frecuentes por medio de dos procesos
        try{
            //debajo de 50 un solo proceso entre
            // 51 y 1000 2 procesos
            //arriba de 1000 15 procesos
            int TAMANO=mensajesU1.size();
            if(TAMANO<=50){
                //print("50 mensajes o menos");
                ProcesoLimpiadoMitad1 pL=new ProcesoLimpiadoMitad1();
                pL.setValues(0,TAMANO,mensajesU1,analisisWhatsflags);
                pL.run();
                pL.join();
                palabras_usadas=pL.getVocabulario();
                frec_pal_usadas=pL.getFrecuencia_vocabulario();
                //OBTENCION DE PALABRAS Y GUARDAR EN VARIABLE GLOBAL
                for(int i=0;i<palabras_usadas.length;i++){
                    if (!palabras_usadas[i].equals("")){
                        palabras_mas_usadas+=palabras_usadas[i]+"-"+frec_pal_usadas[i]+"*";
                    }
                }
                cad=pL.getCad();
            }else if(TAMANO>=51 && TAMANO<=1000){
                //print("entre 51 y 1000");
                procesarVocabulario(2,TAMANO);
            }else{
                //print("mas de 1000");
                procesarVocabulario(15,TAMANO);
            }

        }catch(Exception e){e.printStackTrace();}

        /*long endTime = System.currentTimeMillis() - startTime;
        print(endTime/1000+" s de  VOCABULARIO");*/
    }
    //VIDEO LLAMADAS PERDIDAS
    public void llamadasPerdidas(){
        /**BANDERAS**/
        if((llamadas_perdidas+video_perdidas)==0){
            greenflags++;
            banderas_green[7]="¡Que buena amistad tienen! Siempre respondes las llamadas de "+usuario_nom2+".";
        }else if((llamadas_perdidas+video_perdidas)>=1 && (llamadas_perdidas+video_perdidas)<=5){//entre 1 y 5
            redflags++;
            banderas_red[7]="No has estado atento a las llamadas de "+usuario_nom2+"\n¡Contesta la siguiente!";
        }else if((llamadas_perdidas+video_perdidas)>=6 && (llamadas_perdidas+video_perdidas)<=10){//entre 10 y 6
            redflags+=2;
            banderas_red[7]=2+"*Casi nunca le respondes a "+usuario_nom2+" deberías prestar mas atención a su chat.";
        }else{//mas de 11
            redflags+=3;
            banderas_red[7]=3+"*No estaria nada mal que le respondieras a "+usuario_nom2+" o seguramente dejará de hablarte.";
        }
    }
    //CANTIDAD DE ARCHIVOS
    public void multimedia(){
        for(int i=0;i<multimedia.size();i++){
            if(multimedia.get(i).contains(usuario_nom1)) multimedia_u1++;
            if(multimedia.get(i).contains(usuario_nom2)) multimedia_u2++;
        }
        /**BANDERAS MULTIMEDIA**/
        if(multimedia_u1>multimedia_u2){
            greenflags+=3;
            banderas_green[8]=3+"*¡Eres MultimediaLover! Enviaste "+multimedia_u1+" archivos multimedia.";
        }


    }
    //HABLAR DE MADRUGADA
    public void mensajesMadrugada(){
        String regex="(,?\\s)+([2-6])+(:|\\.)+(\\d{2})+(\\s)?([aA]\\.?)(\\s)([mM]\\.?)";
        Pattern pat=Pattern.compile(regex);
        Matcher mat;
        for(int i=0;i<mensajesU2.size();i++){
            mat=pat.matcher(mensajesU2.get(i));
            if(mat.find())
                mensajesMadrugada++;
        }
        /**BANDERAS**/
        if(mensajesMadrugada>=5 && mensajesMadrugada<10){
            redflags++;
            banderas_red[5]="No sueles hacerlo pero de vez en cuando mandas mensajes en horas de dormir.";
        }else if(mensajesMadrugada>=10 && mensajesMadrugada<20){
            redflags+=2;
            banderas_red[5]=2+"*A tus amigos no les encanta recibir mensajes de madrugada, situación contraria a ti, déjalos dormir plis.";
        }else if(mensajesMadrugada>=20){
            redflags+=3;
            banderas_red[5]=3+"*Te encanta enviar muchos mensajes a tus amigos mientras duermen, tú deberías dormir en las madrugadas.";
        }else if(mensajesMadrugada==0){
            greenflags++;
            banderas_green[5]=usuario_nom1+" sabes que las personas deben dormir, nunca mandas mensajes en la madrugada.";
        }
    }
    //LAPSOS SIN HABLAR
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void lapsoSinHablar(){
        //se convierte a fechas unicas
        ArrayList<Long> lapsos=new ArrayList<>();
        ArrayList<LocalDate> fechas_date=convertirADate(fechas_unicas);
        maxLapso=0;
        //se verifica que no sean conversaciones de un solo dia
        if(fechas_date.size()>2){
           for(int i=0;i<fechas_date.size()-1;i++){
               int n=i+1;
               long lapso= Duration.between(fechas_date.get(i).atStartOfDay(),fechas_date.get(n).atStartOfDay()).toDays();
               lapsos.add(lapso);
           }
           maxLapso=Collections.max(lapsos);
        }
        /**BANDERAS**/
        if(maxLapso>=30 && maxLapso<60){
            redflags++;
            banderas_red[3]="¡Qué triste! En un mes "+usuario_nom2+" y tú no han platicado";
        }else if(maxLapso>=60 && maxLapso<90){
            redflags+=2;
            banderas_red[3]=2+"*Casi 2 meses han pasado desde la última vez que hablaste con "+usuario_nom2+" … no olvides su chat.";
        }else if(maxLapso>90){
            redflags+=3;
            banderas_red[3]=3+"*¿Todo bien? Llevas más de 90 días sin hablar con  "+usuario_nom2+", seguro te extraña.";
        }
    }
    //FRECUENCIA CONVERSACION AL AÑO
    public void frecDeConversacionAlAno(){
        for(int i=0;i<fechas.size();i++){
            if(!fechas_unicas.contains(fechas.get(i)))
                fechas_unicas.add(fechas.get(i));
        }
        mensajes_al_ano=fechas_unicas.size();
        /**BANDERAS**/
        float frec=(float) (mensajes_al_ano/365.0f)*100.0f;
        if(frec==100.0f){
            greenflags+=5;
            banderas_green[4]=5+"*¡Wow, patican diario!Seguramente "+usuario_nom2+" es una persona super importante para ti.";
        }else if(frec>=90.0f && frec<99.0f){
            greenflags+=4;
            banderas_green[4]=4+"*Parece que un día para ti no estaría completo si no le mandas un whats a "+usuario_nom2+".";
        }else if(frec>=80.0f && frec<89.0f){
            greenflags+=3;
            banderas_green[4]=3+"*"+usuario_nom2+" es de tus chats preferidos en WhatsApp, platican casi siempre.";
        }else if(frec>=70.0f && frec<79.0f){
            greenflags+=2;
            banderas_green[4]=2+"*Sin duda tienes presente a "+usuario_nom2+" en tu mente, aunque debería platicar más ¡Las nuevas noticias nunca faltan!.";
        }else if(frec>=50.0f && frec<69.0f){
            greenflags++;
            banderas_green[4]="Sin duda tienes presente a "+usuario_nom2+" en tu mente, aunque debería platicar más ¡Las nuevas noticias nunca faltan!.";
        }else if(frec>=30.0f && frec<39.0f){
            redflags++;
            banderas_red[4]="No te olvides de la existencia de "+usuario_nom2+", platican muy poco ☹";
        }else if(frec>=20.0f && frec<29.0f){
            redflags+=2;
            banderas_red[4]="2*No te olvides de la existencia de "+usuario_nom2+", platican muy poco ☹";
        }else if(frec<=19.0f){
            redflags+=3;
            banderas_red[4]="3*Seguramente "+usuario_nom2+" está súper triste, nunca hablan por WhatsApp.";
        }else{
            //sin banderas NEUTRO
        }

    }
    //TIEMPO INVERT
    public  void  obtenerTiempoInvertido(){
        ProcesoPalabrasIndividuales pP1=new ProcesoPalabrasIndividuales();
        ProcesoPalabrasIndividuales pP2=new ProcesoPalabrasIndividuales();
        pP1.setValues(mensajesU1,analisisWhatsflags);
        pP2.setValues(mensajesU2,analisisWhatsflags);
        pP1.run();
        pP2.run();
        float func1=((5.0f)*((float)veces_inicio1))/3600.0f;
        float func4=((float) cant_emojisU2 + (float)cant_emojisU1)*(2.0f)/(3600.0f);
        float func5= (((float)numLinks)*(10.0f))/(3600.0f);
        float func6=((5.0f)*((float)eliminados_u1))/3600.0f;
        float func7=(((float)multimedia_u1+(float)multimedia_u2)*(10.0f))/3600.0f;
        try {
            pP1.join();
            pP2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        float func2=((float) pP1.getNum_palabras()/91.0f)/(60.0f);
        float func3=((float) pP2.getNum_palabras()/270.0f)/(60.0f);

        horas_inU1=func1+func2+func3+func4+func5+func6+func7;
        print(""+horas_inU1);
    }
    //MENSAJES POR MES
    @RequiresApi(api = Build.VERSION_CODES.O)
    public  void obtenerMensajesPorMes(){
        ArrayList<LocalDate> fechasRepetidas=convertirADate(fechas);
        for(LocalDate fecha:fechasRepetidas){
            if(fecha.getYear()==2021){
                mensajesMes2021[fecha.getMonthValue()-1]+=1;
            }
            if(fecha.getYear()==2022){
                mensajesMes2022[fecha.getMonthValue()-1]+=1;
            }
        }
        //para DB
        print("2021");
        int i=0;
        for(Integer meses:mensajesMes2021){
            mensajes2021+=i+":"+meses+",";
            i++;
        }
        print("2022");
        i=0;
        for(Integer meses:mensajesMes2022){
            mensajes2022+=i+":"+meses+",";
            i++;
        }
    }
    /*******-----------------------FUNC SECUNDARIAS----------------------------------*********/
    public void imprimir(List<String > lista){
        for(int i=0;i<lista.size();i++){
            print(lista.get(i)+" **"+i);
        }
    }
    public void imprimir2(String[] lista){
        for(int i=0;i<lista.length;i++){
            print(lista[i]+" **"+i);
        }
    }
    public void imprimir3( List<Integer > lista){
        for(int i=0;i<lista.size();i++){
            print(lista.get(i)+" **"+i);
        }
    }
    public void imprimir4(List<Long > lista){
        for(int i=0;i<lista.size();i++){
            print(lista.get(i)+" **"+i);
        }
    }
    public void print(String s){
            System.out.println(s);
    }
    //agrega : a los nombres de un sola palabra
    public String[] agregarPuntos(String cadena1,String cadena2,String usuario11,String usuario22){
        String user1="",user2="";
        if (cadena1.charAt(cadena1.length()-1)==':' && cadena2.charAt(cadena2.length()-1)==':' && !cadena1.equals(cadena2)){
            user1=usuario11;
            user2=usuario22;
            user1+=":";
            user2+=":";
            //System.out.println("caso 1 "+cadena1+" - "+cadena2);
        }else if (cadena1.charAt(cadena1.length()-1)==':' && cadena2.charAt(cadena2.length()-1)!=':' && !cadena1.equals(cadena2)){
            user1=usuario11;
            user2=usuario22;
            user1+=":";
            //System.out.println("caso 2");
        }else if (cadena1.charAt(cadena1.length()-1)!=':' && cadena2.charAt(cadena2.length()-1)==':' && !cadena1.equals(cadena2)){
            user1=usuario11;
            user2=usuario22;
            user2+=":";
            //System.out.println("caso 3");
        }else if(cadena1.charAt(cadena1.length()-1)!=':' && cadena2.charAt(cadena2.length()-1)!=':' && !cadena1.equals(cadena2)){
            user1=usuario11;
            user2=usuario22;
            cadena1=cadena1;
            cadena2=cadena2;
            //System.out.println("caso 4");
        }
        return new String[]{user1,user2};
    }
    //dice si exite una palabra e en una list y devuelve su indice
    public int containsIn(ArrayList<String> list,String e){
        int indice=-1;
        for(int i=0;i<list.size();i++){
            if(list.get(i).contains(e)){
                indice=i;
                return indice;
            }

        }
        return indice;
    }
    //divide en subprocesos las palabras mas frecuentes
    public void procesarVocabulario(int divisiones,int TAMANO) throws Exception{
        ArrayList<ProcesoLimpiadoMitad1> pl=new ArrayList<>();
        int SECCION=TAMANO/divisiones;
        for(int i=0;i<divisiones;i++){
            if(i<divisiones-1){
                pl.add(new ProcesoLimpiadoMitad1());
                pl.get(i).setValues(i*SECCION,(i+1)*SECCION,mensajesU1,analisisWhatsflags);
                pl.get(i).run();
            }
            else{
                pl.add(new ProcesoLimpiadoMitad1());
                pl.get(i).setValues(i*SECCION,TAMANO,mensajesU1,analisisWhatsflags);
                pl.get(i).run();
            }
        }
        /*estos arrays siempre seran de 150*/
        ArrayList<Integer> frec=new ArrayList<>();
        ArrayList<String> palabras=new ArrayList<>();
        for(int i=0;i<divisiones;i++){
            pl.get(i).join();
            for(int j=0;j<pl.get(i).getVocabulario().length;j++){
                if(!palabras.contains(pl.get(i).getVocabulario()[j])){
                    palabras.add(pl.get(i).getVocabulario()[j]);
                    frec.add(pl.get(i).getFrecuencia_vocabulario()[j]);
                }else{
                    int index=palabras.indexOf((pl.get(i).getVocabulario()[j]));
                    frec.set(index,frec.get(index)+pl.get(i).getFrecuencia_vocabulario()[j]);
                }
            }
            cad.addAll(pl.get(i).getCad());
        }
        //**SE OBTIENEN LAS 10 palabras
        for(int i=0;i<10;i++){
            int frecuencia=Collections.max(frec);
            int index=frec.indexOf(frecuencia);
            String word=palabras.get(index);
            palabras_mas_usadas+=word+"-"+frecuencia+"*";
            palabras.remove(index);
            frec.remove(index);
        }
    }
    //se obtienen las groserias y sus frecuencias
    public void obtenerGroserias(String s){
        for(int j=0;j<groserias.size();j++){
            String groseria=groserias.get(j);
            if(s.contains(groseria)){
                if(!groserias_unicas.contains(groseria)){
                    groserias_unicas.add(groseria);
                    groserias_frec.add(1);
                }else{
                    int indice= groserias_unicas.indexOf(groseria);
                    groserias_frec.set(indice,groserias_frec.get(indice)+1);
                }
            }
        }
    }
    //convierte una lista de fechas de string en tipo Localdate
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<LocalDate> convertirADate(ArrayList<String> fechas){
        ArrayList<LocalDate> fechas_unicas=new ArrayList<>();
        try {
            SimpleDateFormat format=new SimpleDateFormat(formato);
            for(int i=0;i<fechas.size();i++){
                String fecha_sin_space=fechas.get(i).split(" ")[0];
                Date date=format.parse(fecha_sin_space);//se obtiene fecha y se cambia a tipo localdate para la resta
                LocalDate localdate=date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                fechas_unicas.add(localdate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fechas_unicas;
    }
    //actualiza los campos de un analisis de la tabla de contactos
    public void update(String nombre,int analizado ,String fecha,String usuario_nom1 ,String usuario_nom2 ,String lapso,long tiempoRespuestaU1 ,long tiempoRespuestaU2 ,int veces_inicio1,int veces_inicio2 ,int cantidadMensajesU1 ,int cantidadMensajesU2,String emoji_fav_u1 ,String emoji_fav_u2 ,int frec_fav_u1,int frec_fav_u2,String frecLinks,int numLinks ,int eliminados_u1 ,int eliminados_u2 ,int dia_mas_m ,int dia_men_m ,int cantidad_mas ,int cantidad_menos ,long hora_masM ,long hora_menosM,String palabras_mas_usadas,int llamadas_perdidas,int video_perdidas,int multimedia_u1,int multimedia_u2,String groseriasFrec,float porGroserias,int mensajesMadrugada,int mensajes_al_ano,long maxLapso,float horas_inU1,String mensajes2021,String mensajes2022,int redflags,int greenflags,String banderas_red_cad,String banderas_green_cad){
        DBHelper adminSQLiteOpenHelper=new DBHelper(analisisWhatsflags,null,null,1);
        SQLiteDatabase sqLiteDatabase=adminSQLiteOpenHelper.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("analizado",analizado);
        cv.put("fecha",fecha); //These Fields should be your String values of actual column names
        cv.put("usuario_nom1",usuario_nom1);
        cv.put("usuario_nom2",usuario_nom2);
        cv.put("lapso",lapso);
        cv.put("tiempoRespuestaU1",tiempoRespuestaU1);
        cv.put("tiempoRespuestaU2",tiempoRespuestaU2);
        cv.put("veces_inicio1",veces_inicio1);
        cv.put("veces_inicio2",veces_inicio2);
        cv.put("cantidadMensajesU1",cantidadMensajesU1);
        cv.put("cantidadMensajesU2",cantidadMensajesU2);
        cv.put("emoji_fav_u1",emoji_fav_u1);
        cv.put("emoji_fav_u2",emoji_fav_u2);
        cv.put("frec_fav_u1",frec_fav_u1);
        cv.put("frec_fav_u2",frec_fav_u2);
        cv.put("frecLinks",frecLinks);
        cv.put("numLinks",numLinks);
        cv.put("eliminados_u1",eliminados_u1);
        cv.put("eliminados_u2",eliminados_u2);
        cv.put("dia_mas_m",dia_mas_m);
        cv.put("dia_men_m",dia_men_m);
        cv.put("cantidad_mas",cantidad_mas);
        cv.put("cantidad_menos",cantidad_menos);
        cv.put("hora_masM",hora_masM);
        cv.put("hora_menosM",hora_menosM);
        cv.put("palabras_mas_usadas",palabras_mas_usadas);
        cv.put("llamadas_perdidas",llamadas_perdidas);
        cv.put("video_perdidas",video_perdidas);
        cv.put("multimedia_u1",multimedia_u1);
        cv.put("multimedia_u2",multimedia_u2);
        cv.put("groseriasFrec",groseriasFrec);
        cv.put("porGroserias",porGroserias);
        cv.put("mensajesMadrugada",mensajesMadrugada);
        cv.put("mensajes_al_ano",mensajes_al_ano);
        cv.put("maxLapso",maxLapso);
        cv.put("horas_inU1",horas_inU1);
        cv.put("mensajes2021",mensajes2021);
        cv.put("mensajes2022",mensajes2022);
        cv.put("redflags",redflags);
        cv.put("greenflags",greenflags);
        cv.put("banderas_red_cad",banderas_red_cad);
        cv.put("banderas_green_cad",banderas_green_cad);
        sqLiteDatabase.update("t_contactos",cv,"nombre=?",new String[]{nombre});
    }
    //este metodo genera la vista para el analisis
    public void generarVista(){
        analisisWhatsflags.runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run(){
                //FLAGS
                Mensaje flags=new Mensaje(analisisWhatsflags);
                flags.crearMensajeFlags_I(redflags,greenflags,banderas_red,banderas_green);
                analisisWhatsflags.getFlags().addChild(flags.getRelativ_l());//aqui se ira agregando cada view (MENSAJE)
                //SE HACE CON getAnalisis se requiere otra interfaz
                //LAPSO
                Mensaje mLapso=new Mensaje(analisisWhatsflags);
                mLapso.crearMensajeLapso_I(lapso);
                analisisWhatsflags.getAnalisis().addChild(mLapso.getRelativ_l());
                //TIEMPO
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
        });
    }
    /**CALSE PARA ARRAY DE ARRAYS**/
    public class ArrayArrays {
        int indice;
        String[] datos;
        public ArrayArrays(int indice,String[] datos){
            this.datos=datos;
            this.indice=indice;
        }
        public int getIndice(){ return  this.indice; }
        public String[] getDatos() { return datos; }
    }

}

