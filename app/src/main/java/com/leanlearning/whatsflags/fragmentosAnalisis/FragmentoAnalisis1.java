package com.leanlearning.whatsflags.fragmentosAnalisis;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leanlearning.whatsflags.AnalisisWhatsflags;
import com.leanlearning.whatsflags.MainActivity;
import com.leanlearning.whatsflags.R;
import com.leanlearning.whatsflags.fragmentosAnalisis.fragmentoanalisis1.FragmentoAnalisis1Listener;
import com.leanlearning.whatsflags.procesos.ProcesoFrontendIndividual;

public class FragmentoAnalisis1 extends Fragment implements FragmentoAnalisis1Listener {
    ViewGroup layout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_fragmento_analisis1, container, false);
        layout =view.findViewById(R.id.content_flags);
        ((AnalisisWhatsflags) getActivity()).setFragmentoAnalisis1Listener(this);
        if(((AnalisisWhatsflags) getActivity()).analizado && !((AnalisisWhatsflags) getActivity()).intent){
            //muestra analisis
            anadirFlags();
        }else if(!((AnalisisWhatsflags) getActivity()).analizado && !((AnalisisWhatsflags) getActivity()).intent){
            //mensaje de que haga su analisis
            Mensaje mInstrucciones=new Mensaje(((AnalisisWhatsflags) getActivity()),((AnalisisWhatsflags) getActivity()));
            mInstrucciones.crearMensajeNoAnalisis();
            this.addChild(mInstrucciones.getRelativ_l());
        }
        return view;
    }

    @Override
    public void addChild(View v){
        layout.addView(v);
    }

    public void anadirFlags(){
        AnalisisWhatsflags analisisWhatsflags=((AnalisisWhatsflags) getActivity());
        //proceso de frontendIndividual
        analisisWhatsflags.crearDialogEspera();
        try {
            ProcesoFrontendIndividual pfi=new ProcesoFrontendIndividual();
            pfi.setValues(analisisWhatsflags,analisisWhatsflags.getIntent().getExtras().getString("analisis.nombre"),false);
            pfi.run();
            pfi.join();
        }catch (Exception e){e.printStackTrace();}
        analisisWhatsflags.progressDialog.dismiss();
    }
}