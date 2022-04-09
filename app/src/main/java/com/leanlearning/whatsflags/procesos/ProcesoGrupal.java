package com.leanlearning.whatsflags.procesos;

import com.leanlearning.whatsflags.AnalisisWhatsflags;
import com.leanlearning.whatsflags.FragmentoChat.Contacto;
import com.leanlearning.whatsflags.fragmentosAnalisis.Mensaje;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcesoGrupal extends Thread{
    AnalisisWhatsflags analisisWhatsflags;
    ArrayList<String> lines;
    ArrayList<String> datos;
    ArrayList<String> nombres;
    ArrayList<PersonaInicia> personasInician;
    ArrayList<PersonaElimina> pE;
    ArrayList<PersonaGroserias> pG;
    ArrayList<PersonaPalabra> pP;
    ArrayList<PersonaMultimedia> pM;
    String nombreChat="";

    ArrayList<PersonaBanderas> banderasPP=new ArrayList<>();//contiene las banderas por persona
    ArrayList<String>[] msjPus;//BLOQUE EMOJIS
    String[] emojis_positivos={"😃","😄","😁","😆","😀","☺️","😊","🙂","😇","😌","😍","🥰","😘","😗","😝","😛","😋","😚","😜","🤪","😙","😏","😎","🤩","🤗","🥴","🤑","🤠"};
    String[] emojis_negativos={"🤨","🧐","😒","😞","😔","😟","😕","😫","😖","😣","☹️","🙁","😩","😢","🥺","😭","😤","😠","😡","🤬","🥵","🥶","😰","😥","😓","🤔","😑","🙄","🤒","😷","🤕","🤢","🤮","🤧","😵‍"};
    ArrayList<String> dominios=new ArrayList<>();//BLOQUE LINKS
    ArrayList<Integer> frecuenciaLinks=new ArrayList<>();//LINNKS
    ArrayList<String> eliminados=new ArrayList<>();//BLOQUE ELIMINADOS
    ArrayList<Integer> eliminadosPP=new ArrayList<>();//BLOQUE ELIMINADOS
    ArrayList<String> groserias=new ArrayList<>();//BLOQUE GROSERIAS
    private ArrayList<String> groserias_unicas=new ArrayList<>();//Bloque groserias
    private ArrayList<Integer> groserias_frec=new ArrayList<>();//bloque groserias la frecuencia groseria:frec,groseria1:frec1,...
    //----------------BLOQUE DIAS CON MAS Y MENOS
    ArrayList<String> mensajes_fecha=new ArrayList<>();//guarda las fechas y las horas con mas mensajes
    ArrayList<String> mensajes_fecha_menos_frec=new ArrayList<>();//guarda las fechas y las horas con menos mensajes
    String formato="";//es el formato de la fecha
    String fecha_frec;
    String fecha_menos_frec;
    ArrayList<PersonaMTotales> personaMTotales=new ArrayList<>();
    ArrayList<String> fechas=new ArrayList<>();
    //------------------------------------------
    private  ArrayList<String> multimedia=new ArrayList<>();//BLOQUE MULTIMEDIA
    /**BASE DE DATOS**/


    String quien_inicio="";//BLOQUE INICIACON
    private int mensajes_totales=0;//BLOQUE MENSAJES TOTALES
    String mensajesPP_cad="";//BLOQUE MENSAJES TOTALES|
    String emojisFavs="";//BLOQUE EMOJIS
    String frecFavs="";//BLOQUE EMOJIS
    private String goodEnergy="";//BLOQUE EMOJIS (GOODENERGY) BANDERAS
    String frecLinks="";//BLOQUE LINKS
    String eliminadosPP_cad="";//BLOQUE ELIMINADOS
    int eliminados_totales=0;//BLOQUE ELIMINADOS
    String groseriasPersonaTotal="";//BLOQUE GROSERIAS
    int total_groserias=0;//BLOQUE GROSERIAS
    private int dia_mas_m=0,dia_men_m=0,cantidad_mas=0,cantidad_menos=0;//Bloque dia con mas y menos mensajes
    private long hora_masM=0,hora_menosM=0;//Bloque dia con mas y menos mensajes
    private String vocabulario="";//BLOQUE VOCABULARIO
    private int video_perdidas=0,llamadas_perdidas=0;//BLOQUE LLAMADAS
    String multimedia_cad="";//BLOQUE MULTIMEDIA  usuario:multim,...
    int greenflags=0;
    int redflags=0;
    String bandera_goodenergy_cad="";
    @Override
    public void run() {
        super.run();
        /**BLOQUE QUIEN COMIENZA LA CONVERSACION**/
        quienComienzaConversacion();
        /**BLOQUE MENSAJES TOTALES**/
        mensajesTotales();
        /**BLOQUE EMOJIS**/
        obtenerEmojis();
        /**BLOQUE LINKS**/
        obtenerLinks();
        /**BLOQUE ELIMINADOS**/
        obtenerEliminados();
        /**BLOQUE GROSERIAS**/
        obtenerGroserias();
        /**BLOQUE DIAS CON MAS Y MENOS**/
        obtenerDiasMasMenos();
        /**VOCABULARIO**/
        obtenerVocabulario();
        /**BLOQUE LLAMADAS**/
        obtenerLlamadas();
        /**BLOQUE MULTIMEDIA**/
        obtenerMultimedia();
        /****/
        obtenerFlags();
        print(redflags+"");
        print(greenflags+"");
        /**GENERACION DE VISTA**/
        generarVista();
    }
    public  void setValues(ArrayList<String> datos,ArrayList<String> lines,ArrayList<String> nombres,ArrayList<String> links,ArrayList<String> eliminados,int vid_perdidas,int llam_perdidas,ArrayList<String> multi,AnalisisWhatsflags analisisWhatsflags,String nombreChat){
        this.datos=datos;
        this.lines=lines;
        this.nombres=nombres;
        this.dominios=links;
        this.eliminados=eliminados;
        this.video_perdidas=vid_perdidas;
        this.llamadas_perdidas=llam_perdidas;
        this.multimedia=multi;
        this.analisisWhatsflags=analisisWhatsflags;
        this.nombreChat=nombreChat;
    }

    /**BLOQUES PRINCIPALES***/
    /*QUIEN COMIENZA LA CONVERSACION*/
    public void quienComienzaConversacion(){
        /*Inicializa las banderas*/
        for(String nombre: nombres){
            banderasPP.add(new PersonaBanderas(nombre));
        }
        /*--------COMIENZA QUIEN INICIA-----------------*/
        String fechas_extr= "(\\d{1,2})(/|-)(\\d{1,2})(/|-)(\\d{2,4})(,?\\s)(\\d{1,2})";
        Pattern pat=Pattern.compile(fechas_extr);
        Matcher mat;
        ArrayList<String> b5li=new ArrayList<>(),b5lista_fu=new ArrayList<>();
        for(int i=0;i<lines.size();i++){
            mat=pat.matcher(lines.get(i));
            if(mat.find()) b5li.add(mat.group());
        }

        String fechas_extr2= "(\\d{1,2})(/|-)(\\d{1,2})(/|-)(\\d{2,4})";
        pat=Pattern.compile(fechas_extr2);
        for(int i=0;i<b5li.size();i++){
            mat=pat.matcher(b5li.get(i));
            if(mat.find()) if(!b5lista_fu.contains(mat.group())) {b5lista_fu.add(mat.group());}
        }
        /*SOLO INICIALIZA ENCONTRA FECHA*/
        ArrayList<String>[] encontraFecha=new ArrayList[nombres.size()];
        for(int i=0;i<nombres.size();i++){
            encontraFecha[i]=new ArrayList<>();
        }
        //se llena encontra fecha
        for(int i=0;i<b5lista_fu.size();i++){
            int index;
            if((index=containsIn(datos,b5lista_fu.get(i)))!=-1){
                int indice_usuario;
                if((indice_usuario=containsNombre(datos.get(index)))!=-1){
                    encontraFecha[indice_usuario].add(datos.get(index));
                }
            }
        }
        mensajes_totales=datos.size();
        //pára no afectar directamente nombres
        ArrayList<String > nombresAux=new ArrayList<>();
        nombresAux.addAll(nombres);

        ArrayList<Integer> veces_inicio_usuarios=new ArrayList<>();
        //se obtiene la cantidad de veces que inicio por usuario
        for(int i=0;i<encontraFecha.length;i++){
            veces_inicio_usuarios.add(encontraFecha[i].size());
        }
        quien_inicio="";
        personasInician=new ArrayList<>();
        for(int i=0;i<nombres.size();i++){
            int veces= Collections.max(veces_inicio_usuarios);
            int index=veces_inicio_usuarios.indexOf(veces);
            String nombre=nombresAux.get(index);
            quien_inicio+=nombre+":"+veces+",";
            personasInician.add(new PersonaInicia(nombre,veces));
            nombresAux.remove(index);
            veces_inicio_usuarios.remove(index);
        }

        /**BANDERAS**/

        for(int i=0; i<personasInician.size();i++){
            /*GREENFLAGS*/
            int index=containsNombre(personasInician.get(i).getNombre());
            //el primero arriba de 3 veces +4
            if(i==0 && personasInician.get(i).getVeces()>3){
                banderasPP.get(index).setGreenflags(4);
                banderasPP.get(index).setGreenFlag_cad(0,banderasPP.get(index).getNombre()+" es una persona super interesada en el grupo, inicia muchas veces la conversación.");
            }
            //el segundo arriba de 3 +3
            if(i==1 && personasInician.get(i).getVeces()>3){
                banderasPP.get(index).setGreenflags(3);
                banderasPP.get(index).setGreenFlag_cad(0,"A "+banderasPP.get(index).getNombre()+" le encanta inicar la conversación en el grupo.");
            }
            //el 3ro arriba de 3 +2
            if(i==2 && personasInician.get(i).getVeces()>3){
                banderasPP.get(index).setGreenflags(2);
                banderasPP.get(index).setGreenFlag_cad(0,""+banderasPP.get(index).getNombre()+" regularmente escribe el primer mensaje.");
            }
            //el 4ro arriba de 3 +1
            if(i==3 && personasInician.get(i).getVeces()>3){
                banderasPP.get(index).setGreenflags(1);
                banderasPP.get(index).setGreenFlag_cad(0,""+banderasPP.get(index).getNombre()+" de vez en cuando inicia la convesación.");
            }
            /*REDFLAGS*/
            //si es 1-3 +2
            if(personasInician.get(i).getVeces()<3 && personasInician.get(i).getVeces()>1){
                banderasPP.get(index).setRedflags(2);
                banderasPP.get(index).setRedFlagCad(0,"0");//es 0 para decir que es el segundo mensaje
            }
            //si es 0 +3
            if(personasInician.get(i).getVeces()==0){
                banderasPP.get(index).setRedflags(3);
                banderasPP.get(index).setRedFlagCad(0,"1");//es 1 para decir que es el segundo mensaje
            }
        }
        /*for(PersonaBanderas persona:banderasPP){
            print(persona.getNombre()+" -"+persona.getRedflags()+" :"+persona.getGreenflags()+persona.getGreenflagsCad()[0]+" "+persona.getRedflagsCad()[0]);
        }*/
    }
    /*MENSAJES TOTALES*/
    public void mensajesTotales(){
        msjPus=new ArrayList[nombres.size()];
        for(int i=0;i<nombres.size();i++){
            msjPus[i]=new ArrayList<>();
        }
        for(int i=0;i<lines.size();i++){
            int index;
            if((index=containsNombre(lines.get(i)))!=-1){
                msjPus[index].add(lines.get(i));
            }
        }
        ArrayList<Integer> mensajesPP=new ArrayList<>();
        mensajesPP_cad="";//nombre:menssjes,...
        for(int i=0;i<msjPus.length;i++){
            mensajesPP.add(msjPus[i].size());
        }
        //se obtienen ordenados se obtienen copias auxiliares
        ArrayList<Integer> mensajesPP_copy=new ArrayList<>();
        ArrayList<String> nombres_copy=new ArrayList<>();
        mensajesPP_copy.addAll(mensajesPP);
        nombres_copy.addAll(nombres);
        //array que contiene la cantidad de mensajes ordenado

        //se obtiene el orden de mayor a menor
        for(int i=0;i<msjPus.length;i++){
            int mtmax=Collections.max(mensajesPP_copy);
            int index=mensajesPP_copy.indexOf(mtmax);
            String nombre=nombres_copy.get(index);
            mensajesPP_cad+=nombre+":"+mtmax+",";
            personaMTotales.add(new PersonaMTotales(nombre,mtmax));
            mensajesPP_copy.remove(index);
            nombres_copy.remove(index);
        }
        /**BANDERAS**/
        for(int i=0;i<personaMTotales.size();i++){
            int index=containsNombre(personaMTotales.get(i).getNombre());
            //GREENFLAGS
            if(i==0 && personaMTotales.get(i).getTotales()>10){
                banderasPP.get(index).setGreenflags(banderasPP.get(index).getGreenflags()+3);
                banderasPP.get(index).setGreenFlag_cad(1,"A "+banderasPP.get(index).getNombre()+" le encanta enviar mensajes, es la persona que más mensajes envia.");
            }
            if(i==1 && personaMTotales.get(i).getTotales()>10){
                banderasPP.get(index).setGreenflags(banderasPP.get(index).getGreenflags()+2);
                banderasPP.get(index).setGreenFlag_cad(1,""+banderasPP.get(index).getNombre()+" envia muchos mensajes al grupo ¡Felicidades!.");
            }
            if(i==2 && personaMTotales.get(i).getTotales()>10){
                banderasPP.get(index).setGreenflags(banderasPP.get(index).getGreenflags()+1);
                banderasPP.get(index).setGreenFlag_cad(1,""+banderasPP.get(index).getNombre()+" tiene el 3er puesto por enviar mensajes, es un fan de este grupo.");
            }
            //REDFLAGS
            if(personaMTotales.get(i).getTotales()<=10 && personaMTotales.get(i).getTotales()>1){
                banderasPP.get(index).setRedflags(banderasPP.get(index).getRedflags()+2);
                banderasPP.get(index).setRedFlagCad(1,"0");
            }
            if(personaMTotales.get(i).getTotales()==1){
                banderasPP.get(index).setRedflags(banderasPP.get(index).getRedflags()+3);
                banderasPP.get(index).setRedFlagCad(1,"1");
            }

        }
        /*for(PersonaBanderas persona:banderasPP){
            print(persona.getNombre()+" -"+persona.getRedflags()+" :"+persona.getGreenflags()+persona.getGreenflagsCad()[1]+" "+persona.getRedflagsCad()[1]);
        }*/
    }
    /*EMOJIS*/
    public void obtenerEmojis(){
        String regex="";
        for(int i=0;i<nombres.size();i++){
            if(i<nombres.size()-1)
                regex+=nombres.get(i)+"|";
            else
                regex+=nombres.get(i);
        }
        ArrayList<String>[] emojisPP=new ArrayList[nombres.size()];
        for(int i=0;i<nombres.size();i++){
            emojisPP[i]=new ArrayList<>();
        }
        ArrayList<String> emojisTotales=new ArrayList<>();
        for(int i=0;i<msjPus.length;i++){
            for(int j=0;j<msjPus[i].size();j++){
                if(EmojiManager.containsEmoji(msjPus[i].get(j))) {
                    emojisPP[i].addAll(EmojiParser.extractEmojis(msjPus[i].get(j).replaceAll( regex,"")));
                    emojisTotales.addAll(EmojiParser.extractEmojis(msjPus[i].get(j).replaceAll( regex,"")));
                }
            }
        }
        /*SACANDO FRECUENCIAS*/
        ArrayList<Integer>[] frecEmojisPP=new ArrayList[nombres.size()];
        for(int i=0;i<nombres.size();i++){
            frecEmojisPP[i]=new ArrayList<>();
        }
        ArrayList<String> emojisUnicosPP=new ArrayList<>();
        for(int i=0;i<nombres.size();i++){
            emojisUnicosPP.add("-");
        }

        ArrayList<Integer> frecUnicasPP=new ArrayList<>();
        for(int i=0;i<nombres.size();i++){
            frecUnicasPP.add(0);
        }
        for(int i=0;i<emojisPP.length;i++){
            if(emojisPP[i].size()>0){
                frecEmojisPP[i].addAll(obtenerFrecuenciasEmoji(emojisPP[i]));
            }
        }
        for(int i=0;i<nombres.size();i++){
            if(frecEmojisPP[i].size()>0){
                int frecA=Collections.max(frecEmojisPP[i]);
                int index=frecEmojisPP[i].indexOf(frecA);
                String emojiA=emojisPP[i].get(index);
                frecUnicasPP.set(i,frecA);
                emojisUnicosPP.set(i,emojiA);
            }
        }
         emojisFavs="";
         frecFavs="";
        for(int i=0;i<nombres.size();i++){
            emojisFavs+=nombres.get(i)+":"+emojisUnicosPP.get(i)+",";
            frecFavs+=frecUnicasPP.get(i)+",";
        }
        /*SACANDO GOOD ENERGY*/
        if(emojisTotales.size()>0){
            int frecuenciaPositivos=0,frecuenciaNegativos=0;
            for(int i=0;i<emojis_positivos.length;i++){
                if(emojisTotales.contains(emojis_positivos[i])){frecuenciaPositivos+=Collections.frequency(emojisTotales,emojis_positivos[i]);}
            }
            for(int i=0;i<emojis_negativos.length;i++){
                if(emojisTotales.contains(emojis_negativos[i])){frecuenciaNegativos+=Collections.frequency(emojisTotales,emojis_negativos[i]);}
            }
            if(frecuenciaPositivos>frecuenciaNegativos){
                greenflags+=2;
            }
            //mensajes
            if(emojis_negativos.length!=0 && emojis_positivos.length!=0){
                goodEnergy=String.format("%.02f",(float)(((float)frecuenciaPositivos/((float)frecuenciaNegativos+(float) frecuenciaPositivos))*100.0f));
                bandera_goodenergy_cad="La conversacion en "+nombreChat+" es "+goodEnergy+"% Good Energy";
            }
        }else{
            //banderas_green[0]="Tu conversación con "+usuario_nom2+" es 0% Good Energy";
        }
        //
        //print(emojisFavs);
        //print(frecFavs);
    }
    /*LINKS*/
    public void obtenerLinks(){
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
        for(int i=0;i<frecuenciaLinks.size();i++){
            frecLinks+=i+":"+frecuenciaLinks.get(i)+",";
        }
        /**BANDERAS**/
        //numLinks=frecuenciaLinks.size();
        /*if(numLinks>15){
            greenflags++;
            banderas_green[9]="Enviarse links es lo de ustedes, se han enviado "+numLinks+" links en el chat.";
        }else{
            redflags++;
            banderas_red[8]="No les encanta enviarse links con información,¿Cierto? Solo tienen "+numLinks+" links enviados.";
        }*/
    }
    /*ELIMINADOS*/
    public void obtenerEliminados(){
        for(String nombre:nombres){
            eliminadosPP.add(0);
        }
        for(int i=0;i<eliminados.size();i++){
            int index;
            if((index=containsNombre(eliminados.get(i)))!=-1){
                eliminadosPP.set(index,eliminadosPP.get(index)+1);
            }
        }
        ArrayList<String> nombres_copy=new ArrayList<>();
        ArrayList<Integer> eliminadosPP_copy=new ArrayList<>();
        nombres_copy.addAll(nombres);
        eliminadosPP_copy.addAll(eliminadosPP);
        pE=new ArrayList<>();

        for(int i=0;i<eliminadosPP.size();i++){
            int maxEliminados=Collections.max(eliminadosPP_copy);
            int index=eliminadosPP_copy.indexOf(maxEliminados);
            String nombre=nombres_copy.get(index);
            eliminadosPP_cad+=nombre+":"+maxEliminados+",";
            pE.add(new PersonaElimina(nombre,maxEliminados));
            eliminados_totales+=maxEliminados;
            eliminadosPP_copy.remove(index);
            nombres_copy.remove(index);
        }
        /**BANDERAS**/
        for(int i=0;i<pE.size();i++){
            int index=containsNombre(pE.get(i).getNombre());
            /*REDFLAGS*/
            if(i==0 && pE.get(i).getEliminados()>0){
                banderasPP.get(index).setRedFlagCad(2,"¡ALERTA! "+banderasPP.get(index).getNombre()+" quizá oculta algo, borra muchisimos mensajes");
                banderasPP.get(index).setRedflags(banderasPP.get(index).getRedflags()+3);
            }
            if(i==1 && pE.get(i).getEliminados()>0){
                banderasPP.get(index).setRedFlagCad(2,""+banderasPP.get(index).getNombre()+" borra muchisimos mensajes, ¿Por?.");
                banderasPP.get(index).setRedflags(banderasPP.get(index).getRedflags()+2);
            }
            if(i==2 && pE.get(i).getEliminados()>0){
                banderasPP.get(index).setRedFlagCad(2,""+banderasPP.get(index).getNombre()+" esta en top 3 de las personas que borra mensajes.");
                banderasPP.get(index).setRedflags(banderasPP.get(index).getRedflags()+1);
            }
            /*GREENFLAGS*/
            if(pE.get(i).getEliminados()==0){
                banderasPP.get(index).setGreenFlag_cad(3,"0");
                banderasPP.get(index).setGreenflags(banderasPP.get(index).getGreenflags()+3);
            }
        }
        /*for(PersonaBanderas persona:banderasPP){
            print(persona.getNombre()+" -"+persona.getRedflags()+" :"+persona.getGreenflags()+persona.getGreenflagsCad()[3]+" "+persona.getRedflagsCad()[2]);
        }*/
    }
    /*GROSERIAS*/
    public void obtenerGroserias(){
        //se declaran las groserias conocidas
        groserias.add("wey");groserias.add("capullo");groserias.add("boludo");groserias.add("boluda");groserias.add("zonzo");groserias.add("zonza");
        groserias.add("tonto");groserias.add("tonta");groserias.add("joder");groserias.add("gilipollas");groserias.add("bruto");groserias.add("bruta");groserias.add("coño");groserias.add("conchesumare");
        groserias.add("babosa");groserias.add("burro");groserias.add("burra");groserias.add("ojete");groserias.add("idiota");groserias.add("mamón");groserias.add("mamona");groserias.add("cabrona");groserias.add("cabrón");
        groserias.add("puñetas");groserias.add("culo");groserias.add("huevos");groserias.add("pito");groserias.add("estupida");groserias.add("estupido");groserias.add("chale");groserias.add("puta");groserias.add("mierda");
        groserias.add("verga");groserias.add("pendejo");groserias.add("culero");groserias.add("puto");groserias.add("puta madre");groserias.add("pinche pendejo");groserias.add("pinche pendeja");groserias.add("vale verga");groserias.add("lame huevos");
        groserias.add("pendeja");groserias.add("zorra");groserias.add("pinches mamadas");groserias.add("pinche perro");groserias.add("no mames");groserias.add("valiendo madre");groserias.add("vale madres");groserias.add("no chingues");groserias.add("no jodas");
        groserias.add("a huevo");groserias.add("al chile");groserias.add("no friegues");groserias.add("hijo de puta");groserias.add("hija de puta");groserias.add("hija de perra");groserias.add("hijo de perra");groserias.add("chinga tu madre");
        groserias.add("hijo de la chingada");groserias.add("hija de la chingada");groserias.add("vete a la verga");
        //se inicializa  array con nombre y groserias totales
         pG = new ArrayList<>();
        for(String nombre:nombres){
            pG.add(new PersonaGroserias(nombre,0));
        }
        for(int i=0;i<datos.size();i++){
            obtenerGroserias2(datos.get(i),pG);
        }
        Collections.sort(pG, new Comparator<PersonaGroserias>() {
            @Override
            public int compare(PersonaGroserias pg, PersonaGroserias pg1) {
                return ((Integer)pg1.getTotal()).compareTo(pg.getTotal());
            }
        });
        /*for(int i=0;i<pG.size();i++){
            print(pG.get(i).getNombre()+" "+pG.get(i).getTotal());
        }*/
        //para la db
        /**BANDERAS**/
        for(int i=0;i<pG.size();i++){
            groseriasPersonaTotal+=pG.get(i).getNombre()+":"+pG.get(i).getTotal()+",";//se llena para la db
            int index=containsNombre(pG.get(i).getNombre());
            /*REDFLAGS*/
            if(i==0 && pG.get(i).getTotal()>0){
                banderasPP.get(index).setRedflags(banderasPP.get(index).getRedflags()+3);
                banderasPP.get(index).setRedFlagCad(3,banderasPP.get(index).getNombre()+" es la persona más grosera del chat, diganle algo.");
            }
            if(i==1 && pG.get(i).getTotal()>0){
                banderasPP.get(index).setRedflags(banderasPP.get(index).getRedflags()+2);
                banderasPP.get(index).setRedFlagCad(3,"0 estrellas "+banderasPP.get(index).getNombre()+" dice muchas groserías.");
            }
            if(i==2 && pG.get(i).getTotal()>0){
                banderasPP.get(index).setRedflags(banderasPP.get(index).getRedflags()+1);
                banderasPP.get(index).setRedFlagCad(3,"Casi no lo hace pero "+banderasPP.get(index).getNombre()+" de vez en cuando manda  groserías.");
            }
            /*GREEN FLAGS*/
            if(i>2){
                banderasPP.get(index).setGreenflags(banderasPP.get(index).getGreenflags()+2);
                banderasPP.get(index).setGreenFlag_cad(4,"0");//se obtiene el nombre
            }
            if(pG.get(i).getTotal()==0){
                banderasPP.get(index).setGreenflags(banderasPP.get(index).getGreenflags()+3);
                banderasPP.get(index).setGreenFlag_cad(4,"Cuanta educación "+banderasPP.get(index).getNombre()+" no envías groserías  ni por educación.");//se obtiene el nombre
            }
        }
        /*for(PersonaBanderas persona:banderasPP){
            print(persona.getNombre()+" -"+persona.getRedflags()+" :"+persona.getGreenflags()+persona.getGreenflagsCad()[4]+" "+persona.getRedflagsCad()[3]);
        }*/

    }
    /*DIAS MAS Y MENOS*/
    public void obtenerDiasMasMenos(){
        String regex="(\\d{1,2})(/|-)(\\d{1,2})(/|-)(\\d{2,4})(,?\\s)";
        Pattern pat0=Pattern.compile(regex);
        Matcher mat0;
        for(int i=0;i<datos.size();i++){
            mat0= pat0.matcher(datos.get(i));
            if(mat0.find()){
                fechas.add(mat0.group());
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
        for(int i=0;i<datos.size();i++){
            if(datos.get(i).contains(fecha_frec)){
                mat2=pat2.matcher(datos.get(i));
                if(mat2.find()){
                    mensajes_fecha.add(mat2.group());
                }
                /**ESTO SE SACA PARA EL DIA CON MENOS MENSAJES SOLO LA AÑADE A UN ARRAY PARA USARLA DESPUES**/
            }else if(datos.get(i).contains(fecha_menos_frec)){
                mat2=pat2.matcher(datos.get(i));
                if(mat2.find()){
                    mensajes_fecha_menos_frec.add(mat2.group());

                }
            }
        }
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
    /*LLAMADAS PERDIDAS*/
    public void obtenerLlamadas(){
        //PENDIENTE
    }
    public void obtenerMultimedia(){
        ArrayList<Integer> multimediasPP;
        multimediasPP=new ArrayList<>();
        for(String nombre:nombres){
            multimediasPP.add(0);
        }

        for(int i=0;i<multimedia.size();i++){
            int index;
            if((index=containsNombre(multimedia.get(i)))!=-1){
                multimediasPP.set(index,multimediasPP.get(index)+1);
            }
        }
        ArrayList<String> nombres_copy=new ArrayList<>();
        nombres_copy.addAll(nombres);
        pM=new ArrayList<>();
        for(int i=0;i<nombres.size();i++){
            int maxMulti=Collections.max(multimediasPP);
            int index=multimediasPP.indexOf(maxMulti);
            String nombre=nombres_copy.get(index);
            multimedia_cad+=nombre+":"+maxMulti+",";
            pM.add(new PersonaMultimedia(nombre,maxMulti));
            multimediasPP.remove(index);
            nombres_copy.remove(index);
        }
        /**BANDERAS**/
        for(int i=0;i<pM.size();i++){
            int index=containsNombre(pM.get(i).getNombre());
            if(i==0 && pM.get(i).getMulti()>0 ){
                banderasPP.get(index).setGreenFlag_cad(5,"A "+banderasPP.get(index).getNombre()+" le encanta enviar archivos multimedia, es la que más envía en el grupo.");
                banderasPP.get(index).setGreenflags(banderasPP.get(index).getGreenflags()+3);
            }
            if(i==1 && pM.get(i).getMulti()>0 ){
                banderasPP.get(index).setGreenFlag_cad(5,"Los archivos multimedia son el hit de "+banderasPP.get(index).getNombre()+".");
                banderasPP.get(index).setGreenflags(banderasPP.get(index).getGreenflags()+2);
            }
            if(i==2 && pM.get(i).getMulti()>0 ){
                banderasPP.get(index).setGreenFlag_cad(5,""+banderasPP.get(index).getNombre()+"esta en top 3 de las personas que más archivos multimedia envía.");
                banderasPP.get(index).setGreenflags(banderasPP.get(index).getGreenflags()+1);
            }
        }
        /*for(PersonaBanderas persona:banderasPP){
            print(persona.getNombre()+" -"+persona.getRedflags()+" :"+persona.getGreenflags()+persona.getGreenflagsCad()[5]+" "+persona.getRedflagsCad()[2]);
        }*/
    }
    public void obtenerVocabulario(){
        //se crea el tipo de dato con las palabras mas frec por usuario
        pP=new ArrayList<>();
        //msjPus contiene mensajes por usuario
        //se crea un array de procesos
        ArrayList<ProcesoExtraccionPalabras> pEP=new ArrayList<>();

        //se realiza el proceso extraccion palabra frec
        for(int i=0;i<nombres.size();i++){
            //se cra un proceso
            pEP.add(new ProcesoExtraccionPalabras());
            //se manda su array
            pEP.get(i).setValues(msjPus[i]);
            //se ejecuta
            pEP.get(i).run();
        }
        for(int i=0;i<nombres.size();i++){
            //espera la resp de los procesos
            try {
                pEP.get(i).join();
                pP.add(new PersonaPalabra(nombres.get(i),pEP.get(i).getPalabra()));
                vocabulario=nombres.get(i)+":"+pEP.get(i).getPalabra()+",";
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        /*for(int i=0;i<pP.size();i++){
            print(pP.get(i).getNombre()+" "+pP.get(i).getPalabra());
        }*/
        //guardarla en string nombre:palabra,nombre:palabra,nombre:palabra
    }
    /**FUNCION SECUNDARIA**/
    //dice si exite una palabra e en una list y devuelve su indice
    public void obtenerGroserias2(String s ,ArrayList<PersonaGroserias> pG){
        for(int j=0;j<groserias.size();j++){
            String groseria=groserias.get(j);
            if(s.contains(groseria)){
                int index=containsNombre(s);
                if((index=containsNombre(s))!=-1){
                    pG.get(index).setTotal(pG.get(index).getTotal()+1);
                    total_groserias++;//se obtienen las totales
                }
            }
        }
    }
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
    public int containsNombre(String mensaje){
        for(String nombre: nombres){
            if(mensaje.contains(nombre)){
                //print(nombre);
                return nombres.indexOf(nombre);
            }
        }
        return -1;
    }
    public ArrayList<Integer> obtenerFrecuenciasEmoji(ArrayList<String> emojis){
        ArrayList<Integer> frecuencias=new ArrayList<>();
        for(int i=0;i<emojis.size();i++){
            int frec=Collections.frequency(emojis,emojis.get(i));
            frecuencias.add(frec);
        }
        return frecuencias;
    }
    public void print(String s){
        System.out.println(s);
    }
    public void generarVista(){
        analisisWhatsflags.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //INICIA CONVERSACION
                Mensaje mInicon=new Mensaje(analisisWhatsflags);
                mInicon.crearMensajeInicon_G(personasInician);
                analisisWhatsflags.getAnalisis().addChild(mInicon.getRelativ_l());
                //M TOTALES
                Mensaje mTotales=new Mensaje(analisisWhatsflags,analisisWhatsflags);
                mTotales.crearMensajeTotales_G(mensajes_totales,personaMTotales);
                analisisWhatsflags.getAnalisis().addChild(mTotales.getRelativ_l());
                //EMOJIS
                Mensaje mEmojis=new Mensaje(analisisWhatsflags);
                mEmojis.crearMensajeEmoji_G(emojisFavs,frecFavs);
                analisisWhatsflags.getAnalisis().addChild(mEmojis.getRelativ_l());
                //LINKS
                Mensaje mLinks=new Mensaje(analisisWhatsflags,analisisWhatsflags);
                mLinks.crearMensajeLinks_I(frecuenciaLinks);
                analisisWhatsflags.getAnalisis().addChild(mLinks.getRelativ_l());
                //ELIMINADOS
                Mensaje mEliminados=new Mensaje(analisisWhatsflags,analisisWhatsflags);
                mEliminados.crearMensajeEliminados_G(eliminados_totales,pE);
                analisisWhatsflags.getAnalisis().addChild(mEliminados.getRelativ_l());
                //GROSERIAS
                Mensaje mGroserias=new Mensaje(analisisWhatsflags,analisisWhatsflags);
                mGroserias.crearMensajeGroserias_G(total_groserias,pG);
                analisisWhatsflags.getAnalisis().addChild(mGroserias.getRelativ_l());

                //DIAS MAS MENOS
                Mensaje mDiasMM=new Mensaje(analisisWhatsflags,analisisWhatsflags);
                mDiasMM.crearMensajeDiasMasMenos(dia_mas_m,dia_men_m,hora_masM,hora_menosM,cantidad_mas,cantidad_menos,"");
                analisisWhatsflags.getAnalisis().addChild(mDiasMM.getRelativ_l());
                //VOCABULARIO
                Mensaje mVocab=new Mensaje(analisisWhatsflags,analisisWhatsflags);
                mVocab.crearMensajeVocabulario_G(pP);
                analisisWhatsflags.getAnalisis().addChild(mVocab.getRelativ_l());
                //LLAMADAS
                Mensaje mLlamadas=new Mensaje(analisisWhatsflags,analisisWhatsflags);
                mLlamadas.crearMensajePerdidas(llamadas_perdidas,video_perdidas);
                analisisWhatsflags.getAnalisis().addChild(mLlamadas.getRelativ_l());
                //MULTIMEDIA
                Mensaje mMulti=new Mensaje(analisisWhatsflags,analisisWhatsflags);
                mMulti.crearMensajeMulti_G(pM);
                analisisWhatsflags.getAnalisis().addChild(mMulti.getRelativ_l());
                //COMPARTIR
                Mensaje mCompartir=new Mensaje(analisisWhatsflags,analisisWhatsflags);
                mCompartir.crearMensajeCompartir();
                analisisWhatsflags.getAnalisis().addChild(mCompartir.getRelativ_l());
            }
        });
    }
    public void obtenerFlags(){
        for(PersonaBanderas pB:banderasPP){
            redflags+=pB.getRedflags();
            greenflags+=pB.getGreenflags();
        }
    }
    /**clases para tipos de datos**/
    public class PersonaInicia{
        public int getVeces() {
            return veces;
        }

        public void setVeces(int veces) {
            this.veces = veces;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        int veces;
        String nombre;
        public PersonaInicia(String nombre,int veces){
            this.nombre=nombre;
            this.veces=veces;
        }

    }
    public class PersonaMTotales{
        public int getTotales() {
            return totales;
        }

        public void setTotales(int veces) {
            this.totales = veces;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        int totales;
        String nombre;
        public PersonaMTotales(String nombre,int veces){
            this.nombre=nombre;
            this.totales=veces;
        }

    }
    public class PersonaElimina{
        public int getEliminados() {
            return eliminados;
        }

        public void setEliminados(int veces) {
            this.eliminados = veces;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        int eliminados;
        String nombre;
        public PersonaElimina(String nombre,int eliminados){
            this.nombre=nombre;
            this.eliminados=eliminados;
        }

    }
    public class PersonaMultimedia{
        public int getMulti() {
            return multi;
        }

        public void setMulti(int veces) {
            this.multi = veces;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        int multi;
        String nombre;
        public PersonaMultimedia(String nombre,int multi){
            this.nombre=nombre;
            this.multi=multi;
        }

    }
    public class PersonaGroserias{
        public int getTotal() {
            return total;
        }

        public void setTotal(int veces) {
            this.total = veces;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        int total;
        String nombre;
        public PersonaGroserias(String nombre,int total){
            this.nombre=nombre;
            this.total=total;
        }

    }
    public class PersonaPalabra{
        public String  getPalabra() {
            return palabra;
        }

        public void setPalabra(String  palabra) {
            this.palabra = palabra;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        String palabra;
        String nombre;
        public PersonaPalabra(String nombre,String palabra){
            this.nombre=nombre;
            this.palabra=palabra;
        }

    }

    public class PersonaBanderas{
        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
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
        public void setRedFlagCad(int index,String mensaje){
            redflags_cad[index]=mensaje;
        }
        public void setGreenFlag_cad(int index,String mensaje){
            greenflags_cad[index]=mensaje;
        }
        public String[] getGreenflagsCad(){
            return greenflags_cad;
        }
        public String[] getRedflagsCad(){
            return redflags_cad;
        }
        int greenflags=0;
        int redflags=0;
        String[] greenflags_cad= {"","","","","",""};//[0]=INICON,[1]=MTOTALES,[2]= GODENERGY,[3]=ELIMINADOS,[4]=GROSERIAS,[5]=MILTIMEDIA
        String[] redflags_cad= {"","","",""};//[0]=INICON,[1]=MTOTALES,[2]=ELIMINADOS,[3]=GROSERIAS
        String nombre;
        public PersonaBanderas(String nombre){
            this.nombre=nombre;
        }

    }

}
