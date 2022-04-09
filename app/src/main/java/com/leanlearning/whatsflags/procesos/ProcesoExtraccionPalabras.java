package com.leanlearning.whatsflags.procesos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcesoExtraccionPalabras extends  Thread{
    ArrayList<String > mensajes;
    String palabra;
    /*SALIDAS*/
    ArrayList<String> cad=new ArrayList<>();
    ArrayList<String> p_unicas=new ArrayList<>();
    ArrayList<Integer> frecuencias=new ArrayList<>();
    String[] vocabulario={"","","","","","","","","",""};
    int[] frecuencia_vocabulario={0,0,0,0,0,0,0,0,0,0};
    @Override
    public void run() {
        super.run();
        String filtroChatPER="(-)(\\s)(.+)(:)(\\s)(.+)";
        Pattern pat=Pattern.compile(filtroChatPER);
        Matcher mat;


        for(int i=0;i<mensajes.size();i++){
            mat=pat.matcher(mensajes.get(i));
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
        int maxFrec=Collections.max(frecuencias);
        int index=frecuencias.indexOf(maxFrec);
        palabra=p_unicas.get(index);
    }
    public  void setValues(ArrayList<String > mensajes){
        this.mensajes=mensajes;
    }
    public String getPalabra(){
        return this.palabra;
    }
    public boolean isStopWord(String word){
        if(word.equalsIgnoreCase("y") || word.equalsIgnoreCase("o") || word.equalsIgnoreCase("a")
                || word.equalsIgnoreCase("e") || word.equalsIgnoreCase("de") || word.equalsIgnoreCase("en")
                || word.equalsIgnoreCase("que") || word.equalsIgnoreCase("el") || word.equalsIgnoreCase("la") || word.equalsIgnoreCase("es") || word.equalsIgnoreCase("lo")){
            return true;
        }
        return false;
    }
}
