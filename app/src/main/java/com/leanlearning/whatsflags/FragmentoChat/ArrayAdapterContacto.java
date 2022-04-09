package com.leanlearning.whatsflags.FragmentoChat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.leanlearning.whatsflags.R;

import java.util.List;

public class ArrayAdapterContacto extends ArrayAdapter<Contacto> {
    Context context;
    int layout;
    public ArrayAdapterContacto(Context context, int resource, List<Contacto> objects) {
        super(context, resource, objects);
        this.layout=resource;
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String nombre=getItem(position).getNombre();
        int redFlags=getItem(position).getRedFlags();
        int greenFlags=getItem(position).getGreenFLags();
        boolean analizado=getItem(position).isAnalizado();
        Contacto contacto=new Contacto(nombre,"",analizado);//se pone "" en telefono por que no es necesario mostrar
        LayoutInflater inflater=LayoutInflater.from(context);
        convertView=inflater.inflate(layout,parent,false);

        TextView tvNombre=convertView.findViewById(R.id.nombre_itemContacto);
        tvNombre.setText(nombre);
        TextView redF=convertView.findViewById(R.id.redFlags_itemContacto);
        TextView greenF=convertView.findViewById(R.id.greenFlags_itemContacto);
        ImageView flags=convertView.findViewById(R.id.flagsImage_itemContacto);
        if(analizado){
            redF.setText(redFlags+"");
            greenF.setText(greenFlags+"");
        }else{
            redF.setVisibility(View.INVISIBLE);
            greenF.setVisibility(View.INVISIBLE);
            flags.setVisibility(View.INVISIBLE);
        }


        return convertView;
    }
}
