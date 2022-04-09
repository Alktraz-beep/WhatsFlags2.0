package com.leanlearning.whatsflags.procesos;

import com.leanlearning.whatsflags.AnalisisWhatsflags;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcesoLimpiadoMitad1 extends Thread{
    /*ENTRADAS*/
    ArrayList<String> mensajesU1;
    int INICIO;
    int FINAL;

    /*SALIDAS*/
    ArrayList<String> cad=new ArrayList<>();
    ArrayList<String> p_unicas=new ArrayList<>();
    ArrayList<Integer> frecuencias=new ArrayList<>();
    String[] vocabulario={"","","","","","","","","",""};
    int[] frecuencia_vocabulario={0,0,0,0,0,0,0,0,0,0};
    ArrayList<String> stopWords=new ArrayList<>();
    AnalisisWhatsflags analisisWhatsflags;
    public void setValues(int INICIO, int FINAL, ArrayList<String> mensajesU1, AnalisisWhatsflags analisisWhatsflags){
        this.mensajesU1=mensajesU1;
        this.INICIO=INICIO;
        this.FINAL=FINAL;
        this.analisisWhatsflags=analisisWhatsflags;
    }

    @Override
    public void run() {
        super.run();
        //llenado de la lista de stopwords
        llenarStopWords();
        String filtroChatPER="(-)(\\s)(.+)(:)(\\s)(.+)";
        Pattern pat=Pattern.compile(filtroChatPER);
        Matcher mat;

        for(int i=INICIO;i<FINAL;i++){
            mat=pat.matcher(mensajesU1.get(i));
            if(mat.find()){
                String[]  separadas= mat.group(6).split(" ");
                for(int j=0;j<separadas.length;j++){
                    String word=separadas[j].replaceAll("\\p{Punct}","").replaceAll("[0-9]+","").toLowerCase();
                    if(!word.isEmpty() && !isStopWord(word)){
                        cad.add(word);
                        if(!p_unicas.contains(word))
                            p_unicas.add(word);
                    }
                }
            }
        }
        for(int i=0;i<p_unicas.size();i++){
            frecuencias.add(Collections.frequency(cad,p_unicas.get(i)));
        }
        if(frecuencias.size()>=10){
            for(int i=0;i<10;i++){
                int frec= Collections.max(frecuencias);
                int index=frecuencias.indexOf(frec);
                String word=p_unicas.get(index);
                vocabulario[i]=word;
                frecuencia_vocabulario[i]=frec;
                //print(word+" "+frec);
                /*SE BORRA*/
                frecuencias.remove(index);
                p_unicas.remove(index);
            }
        }else {
            for(int i=0;i<p_unicas.size();i++){
                int frec= Collections.max(frecuencias);
                int index=frecuencias.indexOf(frec);
                String word=p_unicas.get(index);
                vocabulario[i]=word;
                frecuencia_vocabulario[i]=frec;
                /*SE BORRA*/
                frecuencias.remove(index);
                p_unicas.remove(index);
            }
        }
    }
    //RETORNA TRUE SI ES UNA STOPWORD DE LO CONTRARIO UN FALSE

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
    public void print(String s){
        System.out.println(s);
    }
    /*GETTERS*/
    public String[] getVocabulario(){
        return this.vocabulario;
    }
    public int[] getFrecuencia_vocabulario(){
        return this.frecuencia_vocabulario;
    }
    public ArrayList<String> getCad(){ return this.cad;}


}
