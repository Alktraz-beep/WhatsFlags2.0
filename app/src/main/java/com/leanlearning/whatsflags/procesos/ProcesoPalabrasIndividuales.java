package com.leanlearning.whatsflags.procesos;

import com.leanlearning.whatsflags.AnalisisWhatsflags;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class ProcesoPalabrasIndividuales extends Thread{
    ArrayList<String> mensajes;
    AnalisisWhatsflags analisisWhatsflags;
    ArrayList<String> stopWords=new ArrayList<>();
    int num_palabras=0;
    @Override
    public void run() {
        super.run();
        llenarStopWords();
        ArrayList<String> mensajesSinEspacio=new ArrayList<>();
        for(String mensaje:mensajes){
            mensajesSinEspacio.addAll(Arrays.asList(mensaje.split(" ")));
        }
        for(String mensajese:mensajesSinEspacio){
            mensajese.replaceAll("\\p{Punct}","").replaceAll("[0-9]+","").toLowerCase();
            if(!mensajese.isEmpty() && !isStopWord(mensajese)){
                num_palabras++;
            }
        }

    }

    public void setValues(ArrayList<String> mensajes, AnalisisWhatsflags analisisWhatsflags){
        this.mensajes=mensajes;
        this.analisisWhatsflags=analisisWhatsflags;
    }
    public boolean isStopWord(String word){
        /*String jaja="([Jjaa]*)";
        Pattern pat= Pattern.compile(jaja);
        Matcher mat=pat.matcher(word);
        if(mat.find()){
            return  true;
        }*/
        if(stopWords.contains(word)){
            return true;
        }
        return false;
    }
    public  void llenarStopWords(){
        try {

            BufferedReader oku = new BufferedReader(new InputStreamReader(analisisWhatsflags.getAssets().open("stop_words.txt")));
            String strCurrentLine;
            while ((strCurrentLine = oku.readLine ())!= null) {
                stopWords.add(strCurrentLine);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getNum_palabras() {
        return num_palabras;
    }
}
