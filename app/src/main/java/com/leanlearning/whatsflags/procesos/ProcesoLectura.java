package com.leanlearning.whatsflags.procesos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.leanlearning.whatsflags.AnalisisWhatsflags;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcesoLectura extends Thread{
    Bundle bundle;
    AnalisisWhatsflags analisisWhatsflags;
    String nombreChat;
    Intent intent;

    ArrayList<String> datos=new ArrayList<>();//contiene linea sin \n
    ArrayList<String> lines=new ArrayList<>();//contiene linea con \n

    ArrayList<String>  links=new ArrayList<>();//links enviados en el chat
    ArrayList<String> nombres=new ArrayList<>();//nombres de lineas
    @Override
    public void run() {
        super.run();
        try{


            //System.out.println(bundle.getString(Intent.EXTRA_TEXT));
            if(intent.getAction()==Intent.ACTION_SEND_MULTIPLE){
                nombreChat=(bundle.getString(Intent.EXTRA_TEXT).replaceAll("El historial del chat se adjuntó a este correo como \"Chat de WhatsApp con ","").replaceAll("\".",""));
            }
            if(intent.getAction()==Intent.ACTION_SEND){
                nombreChat=(bundle.getString(Intent.EXTRA_TEXT).replaceAll("Chat de WhatsApp con ","").replaceAll(".txt",""));
            }

            obtenerChat();
            System.out.println("INTENT EXPORTAR "+nombreChat);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setValues(AnalisisWhatsflags analisisWhatsFlag, Bundle bundle,Intent intent){
        this.analisisWhatsflags=analisisWhatsFlag;
        this.bundle=bundle;
        this.intent=intent;
    }
    public void obtenerChat() throws  Exception{
        ArrayList<String> datos1=new ArrayList<>();//contiene linea sin \n
        ArrayList<String> lines1=new ArrayList<>();//contiene linea con \n
        Uri uri=null;
        uri= analisisWhatsflags.getIntent().getClipData().getItemAt(0).getUri();

        InputStream inputstream = analisisWhatsflags.getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));
        /******SE LEE CADA LINEA Y SE ASIGNA A DOS LISTAS DIFERENTES SOLO DEL AÑO QUE SE DESEA********/
        String line="";
        String regex="(\\d{1,2})(/|-)(\\d{1,2})(/|-)(2021|2022|21|22)(,?\\s)(\\d{1,2})(:|\\.)(\\d{2})(\\s)?([apAP]\\.?)?(\\s)?([mM]\\.?)?(\\]?\\s-?\\s?\\s?)(.+)";
        Pattern pat=Pattern.compile(regex);
        Matcher mat;
        while ((line=reader.readLine()) != null) {
            mat=pat.matcher(line);
            if(mat.find()){
                //System.out.println(line);
                datos1.add(line);
                lines1.add(line+"\n");

            }
        }
        /********************SE ELIMINAN CADENAS SOBRANTES ********************************************/

        String cadena_cifrado="Los mensajes y las llamadas están cifrados de extremo a extremo. Nadie fuera de este chat, ni siquiera WhatsApp, puede leerlos ni escucharlos. Toca para obtener más información.";
        String cadena_empresa0="Esta cuenta de empresa ahora se ha registrado como una cuenta estándar. Toca para más información.";
        String cadena_empresa1="Este chat es con una cuenta de empresa. Toca para obtener más información.";
        String cadena_codigo="Cambió tu código de seguridad";
        String creaste="Creaste el grupo";
        String anadiste="Añadiste a";
        String cambiaste="Cambiaste la descripción del grupo";
        String grupo_anadio="añadió a";
        String grupo_teanadio="te añadió";
        String grupo_unio="se unió usando el enlace de invitación de este grupo";
        String grupo_cambio="cambió el ícono de este grupo";
        String grupo_cambio_desc="cambió la descripción del grupo";
        String multimedia="<Multimedia omitido>";
        String elimino="Se eliminó este mensaje";//eliminaste
        String http="http";
        String https="https";
        String admin="Ahora eres admin. del grupo";
        String asunto="cambió el asunto de";
        String bloqueaste="Bloqueaste a este contacto. Toca para desbloquearlo.";
        String desbloqueaste="Desbloqueaste a este contacto";
        String salio="salió del grupo";
        String llamada_perdida="Llamada perdida";
        String video_perdida="Videollamada perdida";
        String creo_grupo="creó el grupo";
        String elimino_a="eliminó a";
        /************PARA OBTENER CADENAS ESPECIFICAS DE BLOQUES****************/
        //para links
        String linksRegex="(?i)\\b((?:https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,6}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\".,<>?«»“”‘’]))";
        Pattern patL=Pattern.compile(linksRegex);
        Matcher matL;
        //para eliminados
        ArrayList<String> eliminados=new ArrayList<>();
        //para llamadas y videollamadas
        int llam_perd=0,vi_perd=0;
        //para multimedia
        ArrayList<String> multimedia_om=new ArrayList<>();
        for(int i=0;i<datos1.size();i++){
            if(datos1.get(i).indexOf(cadena_codigo)!=-1 || datos1.get(i).indexOf(cadena_cifrado)!=-1
                    || datos1.get(i).indexOf(cadena_empresa0)!=-1 || datos1.get(i).indexOf(cadena_empresa1)!=-1 || datos1.get(i).indexOf(grupo_anadio)!=-1
                    || datos1.get(i).indexOf(grupo_teanadio)!=-1 || datos1.get(i).indexOf(grupo_unio)!=-1 || datos1.get(i).indexOf(grupo_cambio)!=-1
                    || datos1.get(i).indexOf(grupo_cambio_desc)!=-1 || datos1.get(i).indexOf(multimedia)!=-1 || datos1.get(i).indexOf(elimino)!=-1
                    || datos1.get(i).indexOf(http)!=-1 || datos1.get(i).indexOf(admin)!=-1 || datos1.get(i).indexOf(asunto)!=-1
                    || datos1.get(i).indexOf(https)!=-1 || datos1.get(i).indexOf(bloqueaste)!=-1 || datos1.get(i).indexOf(desbloqueaste)!=-1 || datos1.get(i).indexOf(creaste)!=-1
                    || datos1.get(i).indexOf(anadiste)!=-1 || datos1.get(i).indexOf(cambiaste)!=-1 || datos1.get(i).indexOf(salio)!=-1 || datos1.get(i).indexOf(llamada_perdida)!=-1 || datos1.get(i).indexOf(video_perdida)!=-1
                    || datos1.get(i).indexOf(creo_grupo)!=-1 || datos1.get(i).indexOf(elimino_a)!=-1){

                //Si hay links los sacamos en Array links
                if(datos1.get(i).indexOf(http)!=-1 ||datos1.get(i).indexOf(https)!=-1){
                    matL=patL.matcher(datos1.get(i));
                    if(matL.find()){
                        String link=matL.group(1);
                        links.add(getDomain(link));
                    }
                }
                if(datos1.get(i).indexOf(elimino)!=-1){
                    eliminados.add(datos1.get(i));
                }
                if(datos1.get(i).indexOf(video_perdida)!=-1){
                    vi_perd++;
                }
                if(datos1.get(i).indexOf(llamada_perdida)!=-1){
                    llam_perd++;
                }
                if(datos1.get(i).indexOf(multimedia)!=-1)
                    multimedia_om.add(datos1.get(i));
            }else{
                datos.add(datos1.get(i));
                lines.add(lines1.get(i));
            }

        }
        /********               OBTENCION DE NOMBRES                                       *******/
        System.out.println("-------------------");
        regex="(\\s)(-)(\\s)([^:]+)([^/w]+)";
        pat=Pattern.compile(regex);
        for(int i=0;i<datos.size();i++){
            mat=pat.matcher(datos.get(i));
            if(mat.find()){
                //System.out.println(mat.group());
                if(!nombres.contains(mat.group(4)))
                    nombres.add(mat.group(4));
            }
        }
        if(nombres.size()>2){
            //proceso analisis de grupo
            /*ProcesoGrupal pG=new ProcesoGrupal();
            pG.setValues(datos,lines,nombres,links,eliminados,vi_perd,llam_perd,multimedia_om,analisisWhatsflags,nombreChat);
            pG.start();*/
            analisisWhatsflags.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(analisisWhatsflags, "Próximamente se añadirá función a grupos", Toast.LENGTH_LONG).show();
                }
            });
        }else{
            //proceso analisis individual
            ProcesoIndividual pI=new ProcesoIndividual();
            pI.setValues(datos,lines,nombres,nombreChat,links,eliminados,llam_perd,vi_perd,multimedia_om,analisisWhatsflags);
            pI.start();
        }
        analisisWhatsflags.ponerNombre( nombreChat);
    }
    ///FUNCIONES SECUNDARIAS
    public void imprimir(List<String > lista){
        for(int i=0;i<lista.size();i++){
            System.out.println(lista.get(i)+" **"+i);
        }
    }
    public String getDomain(String link){
        String dominio;
        String[] domain;
        dominio=link.replaceAll("http://","");
        dominio=dominio.replaceAll("https://","");

        domain=dominio.split("/");
        return domain[0];

    }


}
