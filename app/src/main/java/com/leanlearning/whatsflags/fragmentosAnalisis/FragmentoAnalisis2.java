package com.leanlearning.whatsflags.fragmentosAnalisis;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leanlearning.whatsflags.AnalisisWhatsflags;
import com.leanlearning.whatsflags.R;
import com.leanlearning.whatsflags.fragmentosAnalisis.fragmentoanalisis1.FragmentoAnalisis2Listener;
import com.leanlearning.whatsflags.procesos.ProcesoFrontendIndividual;

public class FragmentoAnalisis2 extends Fragment implements FragmentoAnalisis2Listener {
    ViewGroup layout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_fragmento_analisis2, container, false);
        ((AnalisisWhatsflags)getActivity()).setFragmentoAnalisis2Listener(this);
        layout =view.findViewById(R.id.content_analisis);
        if(((AnalisisWhatsflags) getActivity()).analizado && !((AnalisisWhatsflags) getActivity()).intent){
            //muestra analisis
            anadirAnalisis();
        }
        return view;
    }

    @Override
    public void addChild(View v) {
        layout.addView(v);
    }
    public void anadirAnalisis(){
        AnalisisWhatsflags analisisWhatsflags=((AnalisisWhatsflags) getActivity());
        //proceso de frontendIndividual
        analisisWhatsflags.crearDialogEspera();
        try {
            ProcesoFrontendIndividual pfi=new ProcesoFrontendIndividual();
            pfi.setValues(analisisWhatsflags,analisisWhatsflags.getIntent().getExtras().getString("analisis.nombre"),true);//se pone true por que es ahora el analisis
            pfi.run();
            pfi.join();
        }catch (Exception e){e.printStackTrace();}
        analisisWhatsflags.progressDialog.dismiss();
    }
}