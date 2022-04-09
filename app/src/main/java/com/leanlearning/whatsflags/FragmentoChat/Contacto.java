package com.leanlearning.whatsflags.FragmentoChat;

public class Contacto {
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getRedFlags() {
        return redFlags;
    }

    public void setRedFlags(int redFlags) {
        this.redFlags = redFlags;
    }

    public int getGreenFLags() {
        return greenFLags;
    }

    public void setGreenFLags(int greenFLags) {
        this.greenFLags = greenFLags;
    }

    public boolean isAnalizado() {
        return analizado;
    }

    public void setAnalizado(boolean analizado) {
        this.analizado = analizado;
    }

    private String nombre;
    private String num;
    private int redFlags=0;
    private int greenFLags=0;
    private boolean analizado=false;

    public Contacto(String nombre,boolean analizado, int redFlags, int greenFLags) {
        this.nombre = nombre;
        this.redFlags = redFlags;
        this.greenFLags = greenFLags;
        this.analizado=analizado;
    }
    public Contacto(String nombre,String num,boolean analizado) {
        this.nombre = nombre;
        this.analizado=analizado;
        this.num=num;
    }


    public String getNum() {
        return num;
    }
}
