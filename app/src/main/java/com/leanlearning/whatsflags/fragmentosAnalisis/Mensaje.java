package com.leanlearning.whatsflags.fragmentosAnalisis;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.leanlearning.whatsflags.AnalisisWhatsflags;
import com.leanlearning.whatsflags.R;
import com.leanlearning.whatsflags.procesos.ProcesoGrupal;

import java.util.ArrayList;
import java.util.Random;

//esta clase genera un view para añadir a FLAGS o ANALISIS
public class Mensaje{
    LinearLayout linearLayout;
    Context context;
    AnalisisWhatsflags analisisWhatsflags;
    int width=300;
    //PARA MENSAJE DE FLAGS
    public Mensaje(Context context) {//constructor para mensaje de flags individual
        this.context=context;
    }
    public Mensaje(Context context, AnalisisWhatsflags analisisWhatsflags) {//constructor para mensaje de flags individual
        this.context=context;
        this.analisisWhatsflags=analisisWhatsflags;
    }

    public LinearLayout getRelativ_l(){
        return this.linearLayout;
    }
    /*FUNCIONES PARA CREAR MENSAJE SI NO HAY ANALISIS */
    public void crearMensajeNoAnalisis(){
        LayoutInflater inflater=LayoutInflater.from(context);
        int id= R.layout.mensaje_instrucciones_i;//por defecto
        /*INSTANCIAN COMPONENTES*/
        this.linearLayout=(LinearLayout) inflater.inflate(id,null,false);
        TextView oops=linearLayout.findViewById(R.id.tv_oops);
        oops.setText("!Oops! Antes de obtener tu analisis de este chat realiza lo siguiente:");
        //aqui boton solo manda a whats
        linearLayout.findViewById(R.id.btn_irwsp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchIntent = analisisWhatsflags.getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                analisisWhatsflags.startActivity(launchIntent);
            }
        });
    }
    //Boton compartir NEUTRAL
    public  void crearMensajeCompartir(){
        LayoutInflater inflater=LayoutInflater.from(context);
        int id= R.layout.mensaje_compartir_i;//por defecto
        /*INSTANCIAN COMPONENTES*/
        this.linearLayout=(LinearLayout) inflater.inflate(id,null,false);
    }
    /*FUNCIONES PARA MY FLAGS*/
    public void crearMensajeFlags_MF(){
        LayoutInflater inflater=LayoutInflater.from(context);
        int id= R.layout.mensaje_flags_mf;//por defecto
        /*INSTANCIAN COMPONENTES*/
        this.linearLayout=(LinearLayout) inflater.inflate(id,null,false);
    }
    /*FUNCIONES PARA FLAGS Y ANALISIS GRUPAL*/
    public void crearMensajeInicon_G(ArrayList<ProcesoGrupal.PersonaInicia> list_pi){
        LayoutInflater inflater=LayoutInflater.from(context);
        int id= R.layout.mensaje_inicon_g;//por defecto
        /*INSTANCIAN COMPONENTES*/
        this.linearLayout=(LinearLayout) inflater.inflate(id,null,false);
        LinearLayout linear_inicon=linearLayout.findViewById(R.id.linear_g_inicon);
        LinearLayout flecha=linearLayout.findViewById(R.id.layout_expandir_inicon);
        ImageView iv_flecha=linearLayout.findViewById(R.id.iv_g_inicon_flecha);
        ViewGroup lv_personaVeces= linearLayout.findViewById(R.id.lv_g_inicon);
        lv_personaVeces.setVisibility(View.GONE);

        /*SE AÑADE LAS 3 primeras*/
        if(list_pi.size()>=3){

                TextView nombre1=linear_inicon.findViewById(R.id.tv_g_inicon_nom1);
                TextView veces1=linear_inicon.findViewById(R.id.tv_g_inicon_veces1);

                TextView nombre2=linear_inicon.findViewById(R.id.tv_g_inicon_nom2);
                TextView veces2=linear_inicon.findViewById(R.id.tv_g_inicon_veces2);

                TextView nombre3=linear_inicon.findViewById(R.id.tv_g_inicon_nom3);
                TextView veces3=linear_inicon.findViewById(R.id.tv_g_inicon_veces3);

                nombre1.setText(list_pi.get(0).getNombre());
                veces1.setText(list_pi.get(0).getVeces()+" veces");
                nombre2.setText(list_pi.get(1).getNombre());
                veces2.setText(list_pi.get(1).getVeces()+" veces");
                nombre3.setText(list_pi.get(2).getNombre());
                veces3.setText(list_pi.get(2).getVeces()+" veces");

                //lv_personaVeces.addView(layout_pi1);
        }else{
                //hay menos de 3 personas
        }
        /**SI HAY MAS DE 3 SE HACE LOS RESTANTES*/
        if(list_pi.size()>3){
            /*SE ANADE LOS RESTANTES*/
            for(int i=3;i<list_pi.size();i++){
                LinearLayout layout_pi= (LinearLayout) inflater.inflate(R.layout.mensaje_iniconpp_g,null,false);
                TextView nombre=layout_pi.findViewById(R.id.tv_g_inicon_nom);
                TextView veces=layout_pi.findViewById(R.id.tv_g_inicon_veces);
                nombre.setText(list_pi.get(i).getNombre());
                veces.setText(list_pi.get(i).getVeces()+"");
                lv_personaVeces.addView(layout_pi);
            }

            /*CONTROL DE LA LISTA*/
            final boolean[] expandir = {false};
            //si se clickea la flecha
            flecha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    expandir[0] = !expandir[0];//cambia de estado
                    //se cambia el sentido de la flecha de la imagem
                    if(expandir[0]){
                        //EXPANDE
                        iv_flecha.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up_24);
                        lv_personaVeces.setVisibility(View.VISIBLE);

                        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        lv_personaVeces.measure(widthSpec, heightSpec);

                        ValueAnimator mAnimator = slideAnimator(0, lv_personaVeces.getMeasuredHeight(),lv_personaVeces);
                        mAnimator.start();
                    }else{
                        //COLAPSA
                        iv_flecha.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24);
                        lv_personaVeces.setVisibility(View.GONE);
                    }
                }
            });
        }else{
            flecha.setVisibility(View.GONE);
        }


    }
    public void crearMensajeTotales_G(int mtolales, ArrayList<ProcesoGrupal.PersonaMTotales> totalesPP){
        LayoutInflater inflater=LayoutInflater.from(context);
        int id= R.layout.mensaje_totales_g;//por defecto
        /*INSTANCIAN COMPONENTES*/
        this.linearLayout=(LinearLayout) inflater.inflate(id,null,false);
        TextView titulo=linearLayout.findViewById(R.id.tv_g_totales_titulo);
        LinearLayout primeros3=linearLayout.findViewById(R.id.ll_g_totales_primeros3);
        LinearLayout flecha=linearLayout.findViewById(R.id.layout_expandir_total1);
        ImageView iv_flecha=linearLayout.findViewById(R.id.iv_g_totales_flecha);
        ViewGroup lv_contenedorRestantes= linearLayout.findViewById(R.id.lv_g_totales);
        lv_contenedorRestantes.setVisibility(View.GONE);
        /*SE OBTIENEN MEDIDAS PARA LA GRAFICA*/
        analisisWhatsflags.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                width=analisisWhatsflags.getWindowManager().getDefaultDisplay().getWidth();

            }
        });
        width=(int)(width*.5);

        //se obtienen colores aleatorios
        int[] androidColors = analisisWhatsflags.getResources().getIntArray(R.array.androidcolors);
        /*SE MUESTRAN LOS PRIMEROS 3*/
        titulo.setText("Mensajes totales: "+mtolales);
        if(totalesPP.size()>=3){
            for(int i=0;i<3;i++){
                //color aleatorio
                int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                int totales=totalesPP.get(i).getTotales();
                float porC=((float)totales/(float) mtolales);
                String nombre=totalesPP.get(i).getNombre();
                String por=String.format("%.02f",porC*100.0f);
                //se usa la plantilla para la gráfica
                LinearLayout grafica=(LinearLayout) inflater.inflate(R.layout.mensaje_grafica_pp,null,false);
                //se ponen las caracteristicas de la grafica
                ((TextView)grafica.findViewById(R.id.tv_nom_mtotalespp)).setText(nombre);
                ((TextView)grafica.findViewById(R.id.tv_num_mtotalespp)).setText(totales+" mensajes");
                ((ImageView)grafica.findViewById(R.id.iv_mtotalespp)).setBackgroundColor(randomAndroidColor);
                ((ImageView)grafica.findViewById(R.id.iv_mtotalespp)).getLayoutParams().width=(int)(width*porC);
                ((TextView)grafica.findViewById(R.id.tv_pormtotalespp)).setText(por+"%");
                primeros3.addView(grafica);
            }
        }
        if(totalesPP.size()>=3){
            //se rellenan los restantes
            for(int i=3;i<totalesPP.size();i++){
                LinearLayout layout_pT= (LinearLayout) inflater.inflate(R.layout.mensaje_grafica_pp,null,false);
                //se obitenen los colores y los datos restantes
                int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                int totales=totalesPP.get(i).getTotales();
                float porC=((float)totales/(float) mtolales);
                String nombre=totalesPP.get(i).getNombre();
                String por=String.format("%.02f",porC*100.0f);
                //se ponen las caracteristicas de la grafica
                ((TextView)layout_pT.findViewById(R.id.tv_nom_mtotalespp)).setText(nombre);
                ((TextView)layout_pT.findViewById(R.id.tv_num_mtotalespp)).setText(totales+" mensajes");
                ((ImageView)layout_pT.findViewById(R.id.iv_mtotalespp)).setBackgroundColor(randomAndroidColor);
                ((ImageView)layout_pT.findViewById(R.id.iv_mtotalespp)).getLayoutParams().width=(int)(width*porC);
                ((TextView)layout_pT.findViewById(R.id.tv_pormtotalespp)).setText(por+"%");
                lv_contenedorRestantes.addView(layout_pT);
            }
            //PARA CUANDO SE ABRE LA PESTAÑA
            /*CONTROL DE LA LISTA*/
            final boolean[] expandir = {false};
            //si se clickea la flecha
            flecha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    expandir[0] = !expandir[0];//cambia de estado
                    //se cambia el sentido de la flecha de la imagem
                    if(expandir[0]){
                        //EXPANDE
                        iv_flecha.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up_24);
                        lv_contenedorRestantes.setVisibility(View.VISIBLE);

                        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        lv_contenedorRestantes.measure(widthSpec, heightSpec);

                        ValueAnimator mAnimator = slideAnimator(0, lv_contenedorRestantes.getMeasuredHeight(),lv_contenedorRestantes);
                        mAnimator.start();
                    }else{
                        //COLAPSA
                        iv_flecha.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24);
                        lv_contenedorRestantes.setVisibility(View.GONE);
                    }
                }
            });
        }else{
            //no hay 3 personas sobrantes
            flecha.setVisibility(View.GONE);
        }
    }
    public void crearMensajeEmoji_G(String personaEmoji,String frecuencias){
        String[] arrayPE=personaEmoji.split(",");
        String[] arrayFrec=frecuencias.split(",");
        ArrayList<String > nombres=new ArrayList<>();
        ArrayList<String > emojis=new ArrayList<>();
        ArrayList<String > frec=new ArrayList<>();
        //se descomponen los datos y se añaden a cada array
        for(int i=0;i<arrayPE.length;i++){
            String[] persona_emoji=arrayPE[i].split(":");
            nombres.add(persona_emoji[0]);
            emojis.add(persona_emoji[1]);
            frec.add(arrayFrec[i]);
        }
        /**VISTA**/
        LayoutInflater inflater=LayoutInflater.from(context);
        int id= R.layout.mensaje_emojis_g;//por defecto
        /*INSTANCIAN COMPONENTES*/
        this.linearLayout=(LinearLayout) inflater.inflate(id,null,false);
        LinearLayout primeros3=linearLayout.findViewById(R.id.ll_g_emoji_primeros3);
        LinearLayout restantes=linearLayout.findViewById(R.id.lv_g_emoji);
        LinearLayout flecha=linearLayout.findViewById(R.id.layout_expandir_emoji);
        ImageView iv_flecha=linearLayout.findViewById(R.id.iv_g_emoji_flecha);
        restantes.setVisibility(View.GONE);
        //se añaden primeros 3
        if(nombres.size()>=3){
            for (int i=0;i<3;i++){
                LinearLayout layout_pE= (LinearLayout) inflater.inflate(R.layout.mensaje_emoji_pp,null,false);
                ((TextView) layout_pE.findViewById(R.id.tv_emojipp_nom)).setText(nombres.get(i));
                ((TextView) layout_pE.findViewById(R.id.tv_emojipp_veces)).setText(frec.get(i));
                ((TextView) layout_pE.findViewById(R.id.tv_emojipp_emoji)).setText(emojis.get(i));
                primeros3.addView(layout_pE);
            }
        }
        //se añaden los restantes
        if(nombres.size()>3){
            for (int i=3;i<nombres.size();i++){
                LinearLayout layout_pE= (LinearLayout) inflater.inflate(R.layout.mensaje_emoji_pp,null,false);
                ((TextView) layout_pE.findViewById(R.id.tv_emojipp_nom)).setText(nombres.get(i));
                ((TextView) layout_pE.findViewById(R.id.tv_emojipp_veces)).setText(frec.get(i));
                ((TextView) layout_pE.findViewById(R.id.tv_emojipp_emoji)).setText(emojis.get(i));
                restantes.addView(layout_pE);
            }
            //para el evento de click
            /*CONTROL DE LA LISTA*/
            final boolean[] expandir = {false};
            //si se clickea la flecha
            flecha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    expandir[0] = !expandir[0];//cambia de estado
                    //se cambia el sentido de la flecha de la imagem
                    if(expandir[0]){
                        //EXPANDE
                        iv_flecha.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up_24);
                        restantes.setVisibility(View.VISIBLE);

                        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        restantes.measure(widthSpec, heightSpec);

                        ValueAnimator mAnimator = slideAnimator(0, restantes.getMeasuredHeight(),restantes);
                        mAnimator.start();
                    }else{
                        //COLAPSA
                        iv_flecha.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24);
                        restantes.setVisibility(View.GONE);
                    }
                }
            });
        }else{
            flecha.setVisibility(View.GONE);
        }

    }
    public void crearMensajeEliminados_G(int eliminados,ArrayList<ProcesoGrupal.PersonaElimina> pE){
        LayoutInflater inflater=LayoutInflater.from(context);
        int id= R.layout.mensaje_totales_g;//por defecto
        /*INSTANCIAN COMPONENTES*/
        this.linearLayout=(LinearLayout) inflater.inflate(id,null,false);
        TextView titulo=linearLayout.findViewById(R.id.tv_g_totales_titulo);
        LinearLayout primeros3=linearLayout.findViewById(R.id.ll_g_totales_primeros3);
        LinearLayout flecha=linearLayout.findViewById(R.id.layout_expandir_total1);
        ImageView iv_flecha=linearLayout.findViewById(R.id.iv_g_totales_flecha);
        ViewGroup lv_contenedorRestantes= linearLayout.findViewById(R.id.lv_g_totales);
        lv_contenedorRestantes.setVisibility(View.GONE);
        /*SE OBTIENEN MEDIDAS PARA LA GRAFICA*/
        analisisWhatsflags.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                width=analisisWhatsflags.getWindowManager().getDefaultDisplay().getWidth();

            }
        });
        width=(int)(width*.5);
        //se obtienen colores aleatorios
        int[] androidColors = analisisWhatsflags.getResources().getIntArray(R.array.androidcolors);
        /*SE MUESTRAN LOS PRIMEROS 3*/
        titulo.setText("Mensajes eliminados: "+eliminados);
        if(pE.size()>=3){
            for(int i=0;i<3;i++){
                //color aleatorio
                int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                int totales=pE.get(i).getEliminados();
                float porC=((float)totales/(float) eliminados);
                String nombre=pE.get(i).getNombre();
                String por=String.format("%.02f",porC*100.0f);
                //se usa la plantilla para la gráfica
                LinearLayout grafica=(LinearLayout) inflater.inflate(R.layout.mensaje_grafica_pp,null,false);
                //se ponen las caracteristicas de la grafica
                ((TextView)grafica.findViewById(R.id.tv_nom_mtotalespp)).setText(nombre);
                ((TextView)grafica.findViewById(R.id.tv_num_mtotalespp)).setText(totales+" mensajes");
                ((ImageView)grafica.findViewById(R.id.iv_mtotalespp)).setBackgroundColor(randomAndroidColor);
                ((ImageView)grafica.findViewById(R.id.iv_mtotalespp)).getLayoutParams().width=(int)(width*porC);
                ((TextView)grafica.findViewById(R.id.tv_pormtotalespp)).setText(por+"%");
                primeros3.addView(grafica);
            }
        }
        if(pE.size()>=3){
            //se rellenan los restantes
            for(int i=3;i<pE.size();i++){
                LinearLayout layout_pT= (LinearLayout) inflater.inflate(R.layout.mensaje_grafica_pp,null,false);
                //se obitenen los colores y los datos restantes
                int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                int totales=pE.get(i).getEliminados();
                float porC=((float)totales/(float) eliminados);
                String nombre=pE.get(i).getNombre();
                String por=String.format("%.02f",porC*100.0f);
                //se ponen las caracteristicas de la grafica
                ((TextView)layout_pT.findViewById(R.id.tv_nom_mtotalespp)).setText(nombre);
                ((TextView)layout_pT.findViewById(R.id.tv_num_mtotalespp)).setText(totales+" mensajes");
                ((ImageView)layout_pT.findViewById(R.id.iv_mtotalespp)).setBackgroundColor(randomAndroidColor);
                ((ImageView)layout_pT.findViewById(R.id.iv_mtotalespp)).getLayoutParams().width=(int)(width*porC);
                ((TextView)layout_pT.findViewById(R.id.tv_pormtotalespp)).setText(por+"%");
                lv_contenedorRestantes.addView(layout_pT);
            }
            //PARA CUANDO SE ABRE LA PESTAÑA
            /*CONTROL DE LA LISTA*/
            final boolean[] expandir = {false};
            //si se clickea la flecha
            flecha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    expandir[0] = !expandir[0];//cambia de estado
                    //se cambia el sentido de la flecha de la imagem
                    if(expandir[0]){
                        //EXPANDE
                        iv_flecha.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up_24);
                        lv_contenedorRestantes.setVisibility(View.VISIBLE);

                        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        lv_contenedorRestantes.measure(widthSpec, heightSpec);

                        ValueAnimator mAnimator = slideAnimator(0, lv_contenedorRestantes.getMeasuredHeight(),lv_contenedorRestantes);
                        mAnimator.start();
                    }else{
                        //COLAPSA
                        iv_flecha.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24);
                        lv_contenedorRestantes.setVisibility(View.GONE);
                    }
                }
            });
        }else{
            //no hay 3 personas sobrantes
            flecha.setVisibility(View.GONE);
        }
    }
    public void crearMensajeGroserias_G(int groseriasT, ArrayList<ProcesoGrupal.PersonaGroserias> pE){
        LayoutInflater inflater=LayoutInflater.from(context);
        int id= R.layout.mensaje_totales_g;//por defecto
        /*INSTANCIAN COMPONENTES*/
        this.linearLayout=(LinearLayout) inflater.inflate(id,null,false);
        TextView titulo=linearLayout.findViewById(R.id.tv_g_totales_titulo);
        LinearLayout primeros3=linearLayout.findViewById(R.id.ll_g_totales_primeros3);
        LinearLayout flecha=linearLayout.findViewById(R.id.layout_expandir_total1);
        ImageView iv_flecha=linearLayout.findViewById(R.id.iv_g_totales_flecha);
        ViewGroup lv_contenedorRestantes= linearLayout.findViewById(R.id.lv_g_totales);
        lv_contenedorRestantes.setVisibility(View.GONE);
        /*SE OBTIENEN MEDIDAS PARA LA GRAFICA*/
        analisisWhatsflags.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                width=analisisWhatsflags.getWindowManager().getDefaultDisplay().getWidth();

            }
        });
        width=(int)(width*.5);
        //se obtienen colores aleatorios
        int[] androidColors = analisisWhatsflags.getResources().getIntArray(R.array.androidcolors);
        /*SE MUESTRAN LOS PRIMEROS 3*/
        titulo.setText("Personas mas groseras del chat\nGroserias enviadas: "+groseriasT);
        if(pE.size()>=3){
            for(int i=0;i<3;i++){
                //color aleatorio
                int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                int totales=pE.get(i).getTotal();
                float porC=((float)totales/(float) groseriasT);
                String nombre=pE.get(i).getNombre();
                String por=String.format("%.02f",porC*100.0f);
                //se usa la plantilla para la gráfica
                LinearLayout grafica=(LinearLayout) inflater.inflate(R.layout.mensaje_grafica_pp,null,false);
                //se ponen las caracteristicas de la grafica
                ((TextView)grafica.findViewById(R.id.tv_nom_mtotalespp)).setText(nombre);
                ((TextView)grafica.findViewById(R.id.tv_num_mtotalespp)).setText(totales+" mensajes");
                ((ImageView)grafica.findViewById(R.id.iv_mtotalespp)).setBackgroundColor(randomAndroidColor);
                ((ImageView)grafica.findViewById(R.id.iv_mtotalespp)).getLayoutParams().width=(int)(width*porC);
                ((TextView)grafica.findViewById(R.id.tv_pormtotalespp)).setText(por+"%");
                primeros3.addView(grafica);
            }
        }
        if(pE.size()>=3){
            //se rellenan los restantes
            for(int i=3;i<pE.size();i++){
                LinearLayout layout_pT= (LinearLayout) inflater.inflate(R.layout.mensaje_grafica_pp,null,false);
                //se obitenen los colores y los datos restantes
                int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                int totales=pE.get(i).getTotal();
                float porC=((float)totales/(float) groseriasT);
                String nombre=pE.get(i).getNombre();
                String por=String.format("%.02f",porC*100.0f);
                //se ponen las caracteristicas de la grafica
                ((TextView)layout_pT.findViewById(R.id.tv_nom_mtotalespp)).setText(nombre);
                ((TextView)layout_pT.findViewById(R.id.tv_num_mtotalespp)).setText(totales+" mensajes");
                ((ImageView)layout_pT.findViewById(R.id.iv_mtotalespp)).setBackgroundColor(randomAndroidColor);
                ((ImageView)layout_pT.findViewById(R.id.iv_mtotalespp)).getLayoutParams().width=(int)(width*porC);
                ((TextView)layout_pT.findViewById(R.id.tv_pormtotalespp)).setText(por+"%");
                lv_contenedorRestantes.addView(layout_pT);
            }
            //PARA CUANDO SE ABRE LA PESTAÑA
            /*CONTROL DE LA LISTA*/
            final boolean[] expandir = {false};
            //si se clickea la flecha
            flecha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    expandir[0] = !expandir[0];//cambia de estado
                    //se cambia el sentido de la flecha de la imagem
                    if(expandir[0]){
                        //EXPANDE
                        iv_flecha.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up_24);
                        lv_contenedorRestantes.setVisibility(View.VISIBLE);

                        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        lv_contenedorRestantes.measure(widthSpec, heightSpec);

                        ValueAnimator mAnimator = slideAnimator(0, lv_contenedorRestantes.getMeasuredHeight(),lv_contenedorRestantes);
                        mAnimator.start();
                    }else{
                        //COLAPSA
                        iv_flecha.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24);
                        lv_contenedorRestantes.setVisibility(View.GONE);
                    }
                }
            });
        }else{
            //no hay 3 personas sobrantes
            flecha.setVisibility(View.GONE);
        }
    }
    public void crearMensajeVocabulario_G(ArrayList<ProcesoGrupal.PersonaPalabra> pP){
        LayoutInflater inflater=LayoutInflater.from(context);
        int id= R.layout.mensaje_totales_g;//por defecto
        /*INSTANCIAN COMPONENTES*/
        this.linearLayout=(LinearLayout) inflater.inflate(id,null,false);
        TextView titulo=linearLayout.findViewById(R.id.tv_g_totales_titulo);
        LinearLayout primeros3=linearLayout.findViewById(R.id.ll_g_totales_primeros3);
        LinearLayout flecha=linearLayout.findViewById(R.id.layout_expandir_total1);
        ImageView iv_flecha=linearLayout.findViewById(R.id.iv_g_totales_flecha);
        ViewGroup lv_contenedorRestantes= linearLayout.findViewById(R.id.lv_g_totales);
        lv_contenedorRestantes.setVisibility(View.GONE);
        /*SE OBTIENEN MEDIDAS PARA LA GRAFICA*/
        analisisWhatsflags.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                width=analisisWhatsflags.getWindowManager().getDefaultDisplay().getWidth();

            }
        });
        width=(int)(width*.5);
        //se obtienen colores aleatorios
        int[] androidColors = analisisWhatsflags.getResources().getIntArray(R.array.androidcolors);
        /*SE MUESTRAN LOS PRIMEROS 3*/
        titulo.setText("Palabra más usada por participante ");
        if(pP.size()>=3){
            for(int i=0;i<3;i++){
                //color aleatorio
                String nombre=pP.get(i).getNombre();
                //se usa la plantilla para cada palabra
                LinearLayout vocab=(LinearLayout) inflater.inflate(R.layout.mensaje_palabra_pp,null,false);
                //se ponen las caracteristicas de la grafica
                ((TextView)vocab.findViewById(R.id.tv_nom_palabrapp)).setText(nombre+" :");
                ((TextView)vocab.findViewById(R.id.tv_palabra_palabrapp)).setText(pP.get(i).getPalabra());
                primeros3.addView(vocab);
            }
        }
        if(pP.size()>=3){
            //se rellenan los restantes
            for(int i=3;i<pP.size();i++){

                //color aleatorio
                String nombre=pP.get(i).getNombre();
                //se usa la plantilla para cada palabra
                LinearLayout vocab=(LinearLayout) inflater.inflate(R.layout.mensaje_palabra_pp,null,false);
                //se ponen las caracteristicas de la grafica
                ((TextView)vocab.findViewById(R.id.tv_nom_palabrapp)).setText(nombre+" :");
                ((TextView)vocab.findViewById(R.id.tv_palabra_palabrapp)).setText(pP.get(i).getPalabra());
                lv_contenedorRestantes.addView(vocab);
            }
            //PARA CUANDO SE ABRE LA PESTAÑA
            /*CONTROL DE LA LISTA*/
            final boolean[] expandir = {false};
            //si se clickea la flecha
            flecha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    expandir[0] = !expandir[0];//cambia de estado
                    //se cambia el sentido de la flecha de la imagem
                    if(expandir[0]){
                        //EXPANDE
                        iv_flecha.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up_24);
                        lv_contenedorRestantes.setVisibility(View.VISIBLE);

                        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        lv_contenedorRestantes.measure(widthSpec, heightSpec);

                        ValueAnimator mAnimator = slideAnimator(0, lv_contenedorRestantes.getMeasuredHeight(),lv_contenedorRestantes);
                        mAnimator.start();
                    }else{
                        //COLAPSA
                        iv_flecha.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24);
                        lv_contenedorRestantes.setVisibility(View.GONE);
                    }
                }
            });
        }else{
            //no hay 3 personas sobrantes
            flecha.setVisibility(View.GONE);
        }
    }
    public void crearMensajeMulti_G(ArrayList<ProcesoGrupal.PersonaMultimedia> pM){
        LayoutInflater inflater=LayoutInflater.from(context);
        int id= R.layout.mensaje_totales_g;//por defecto
        /*INSTANCIAN COMPONENTES*/
        this.linearLayout=(LinearLayout) inflater.inflate(id,null,false);
        TextView titulo=linearLayout.findViewById(R.id.tv_g_totales_titulo);
        LinearLayout primeros3=linearLayout.findViewById(R.id.ll_g_totales_primeros3);
        LinearLayout flecha=linearLayout.findViewById(R.id.layout_expandir_total1);
        ImageView iv_flecha=linearLayout.findViewById(R.id.iv_g_totales_flecha);
        ViewGroup lv_contenedorRestantes= linearLayout.findViewById(R.id.lv_g_totales);
        lv_contenedorRestantes.setVisibility(View.GONE);
        /*SE OBTIENEN MEDIDAS PARA LA GRAFICA*/
        analisisWhatsflags.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                width=analisisWhatsflags.getWindowManager().getDefaultDisplay().getWidth();

            }
        });
        width=(int)(width*.5);
        //se obtienen colores aleatorios
        int[] androidColors = analisisWhatsflags.getResources().getIntArray(R.array.androidcolors);
        /*SE MUESTRAN LOS PRIMEROS 3*/
        titulo.setText("Multimedialovers \uD83D\uDCF7");
        if(pM.size()>=3){
            for(int i=0;i<3;i++){
                //color aleatorio
                String nombre=pM.get(i).getNombre();
                //se usa la plantilla para cada palabra
                LinearLayout vocab=(LinearLayout) inflater.inflate(R.layout.mensaje_palabra_pp,null,false);
                //se ponen las caracteristicas de la grafica
                ((TextView)vocab.findViewById(R.id.tv_nom_palabrapp)).setText(nombre+" :");
                ((TextView)vocab.findViewById(R.id.tv_palabra_palabrapp)).setText("Envió "+pM.get(i).getMulti()+" audios, imágenes, videos o stickers");
                primeros3.addView(vocab);
            }
        }
        if(pM.size()>=3){
            //se rellenan los restantes
            for(int i=3;i<pM.size();i++){

                //color aleatorio
                String nombre=pM.get(i).getNombre();
                //se usa la plantilla para cada palabra
                LinearLayout vocab=(LinearLayout) inflater.inflate(R.layout.mensaje_palabra_pp,null,false);
                //se ponen las caracteristicas de la grafica
                ((TextView)vocab.findViewById(R.id.tv_nom_palabrapp)).setText(nombre+" :");
                ((TextView)vocab.findViewById(R.id.tv_palabra_palabrapp)).setText("Envió "+pM.get(i).getMulti()+" archivos multimedia");
                lv_contenedorRestantes.addView(vocab);
            }
            //PARA CUANDO SE ABRE LA PESTAÑA
            /*CONTROL DE LA LISTA*/
            final boolean[] expandir = {false};
            //si se clickea la flecha
            flecha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    expandir[0] = !expandir[0];//cambia de estado
                    //se cambia el sentido de la flecha de la imagem
                    if(expandir[0]){
                        //EXPANDE
                        iv_flecha.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up_24);
                        lv_contenedorRestantes.setVisibility(View.VISIBLE);

                        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        lv_contenedorRestantes.measure(widthSpec, heightSpec);

                        ValueAnimator mAnimator = slideAnimator(0, lv_contenedorRestantes.getMeasuredHeight(),lv_contenedorRestantes);
                        mAnimator.start();
                    }else{
                        //COLAPSA
                        iv_flecha.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24);
                        lv_contenedorRestantes.setVisibility(View.GONE);
                    }
                }
            });
        }else{
            //no hay 3 personas sobrantes
            flecha.setVisibility(View.GONE);
        }
    }
    /*FUNCIONES PARA FLAGS Y ANALISIS INDIVIDUAL*/
    public void crearMensajeLapso_I(String lapso){
        LayoutInflater inflater=LayoutInflater.from(context);
        int id= R.layout.mensaje_lapso_i;//por defecto
        /*INSTANCIAN COMPONENTES*/
        this.linearLayout=(LinearLayout) inflater.inflate(id,null,false);
        TextView tv_mensaje=linearLayout.findViewById(R.id.tv_lapso_i);
        tv_mensaje.setText(lapso);
    }
    public void crearMensajeTiempoInvertido(float tiempoinv,String nom1,String nom2){
        String mensaje=nom1+", has invertido "+String.format("%.02f",tiempoinv)+" horas en tu chat con "+nom2;
        LayoutInflater inflater=LayoutInflater.from(context);
        int id= R.layout.mensaje_tinvertido_i;//por defecto
        /*INSTANCIAN COMPONENTES*/
        this.linearLayout=(LinearLayout) inflater.inflate(id,null,false);
        TextView tv_mensaje=linearLayout.findViewById(R.id.tiempoinv_u1_i);
        tv_mensaje.setText(mensaje);
    }
    public void crearMensajeMultimedia(int mul1,int mul2,String nom1,String nom2){

        LayoutInflater inflater=LayoutInflater.from(context);
        int id= R.layout.mensaje_mmultimedia_i;//por defecto
        /*INSTANCIAN COMPONENTES*/
        this.linearLayout=(LinearLayout) inflater.inflate(id,null,false);
        TextView tv_mensaje=linearLayout.findViewById(R.id.tv_multimedia_msj);
        String multimedialover="";
        float porMul=0;
        String mensaje ="El multimedialover del chat es ";
        int total=mul1+mul2;
        if(total>0){

            if(mul1>mul2){
                multimedialover=nom1;
                porMul=(float) (((float) mul1/(float) total)*100.0f);
                mensaje+=multimedialover+" , envió "+mul1+"  audios, imágenes, videos o stickers. ("+String.format("%.02f",porMul)+"%)";
            }else if(mul2>mul1){
                multimedialover=nom2;
                porMul=(float) (((float) mul1/(float) total)*100.0f);
                mensaje+=multimedialover+" ,envió "+mul1+"("+String.format("%.02f",porMul)+"%)";
            }else if(mul1==mul2){
                mensaje="Ambos son el multimedialover enviaron la misma cantidad de audios, imágenes, videos o stickers.";
            }

        }else{
            mensaje="Ninguno es el multimedialover, no se mandan audios, imágenes, videos o stickers.";
        }
        tv_mensaje.setText(mensaje);

    }
    public void crearMensajePerdidas(int llamadas,int videos){
        LayoutInflater inflater=LayoutInflater.from(context);
        int id= R.layout.mensaje_mperdidas_i;//por defecto
        /*INSTANCIAN COMPONENTES*/
        this.linearLayout=(LinearLayout) inflater.inflate(id,null,false);
        TextView tv_video=linearLayout.findViewById(R.id.tv_perdidas_video);
        TextView tv_llamadas=linearLayout.findViewById(R.id.tv_perdidas_llamadas);
        tv_video.setText("Videollamadas perdidas: "+videos);
        tv_llamadas.setText("Llamadas perdidas: "+llamadas);
    }
    public void crearMensajeVocabulario(String palabras_frec,String nom2){
        LayoutInflater inflater=LayoutInflater.from(context);
        int id= R.layout.mensaje_mvocabulario_i;//por defecto
        /*INSTANCIAN COMPONENTES*/
        this.linearLayout=(LinearLayout) inflater.inflate(id,null,false);
        ViewGroup group=linearLayout.findViewById(R.id.contenedor_vocabulario);
        int groserias_layout=R.layout.groserias_layout;

        /*SE OBTIENEN LAS FRECUENCIAS Y LAS PALABRAS*/
        String[] pal_frec=palabras_frec.split("\\*");
        String[] palabras=new String[pal_frec.length];
        int [] frec= new int[pal_frec.length];
        for(int i=0;i<pal_frec.length;i++){
            String[] pf= pal_frec[i].split("-");
            palabras[i]=pf[0];
            frec[i]=Integer.parseInt(pf[1]);
        }
        int total=0;
        for(int i=0;i< frec.length;i++){
            total+=frec[i];
        }
        if(total>0){
            //se saca el tamaño del width
            //se asignan medidas de porcentajes
            analisisWhatsflags.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    width=analisisWhatsflags.getWindowManager().getDefaultDisplay().getWidth();

                }
            });
            width=(int) (width*.5);
            //se obtienen colores aleatorios
            int[] androidColors = analisisWhatsflags.getResources().getIntArray(R.array.androidcolors);
            TextView tv_titulo=linearLayout.findViewById(R.id.tv_voc_titulo);
            tv_titulo.setText("Las palabras que mas usaste con "+nom2+" fueron:");
            for(int i=0;i<palabras.length;i++){
                if(frec[i]!=0){
                    LinearLayout groserias_linear=(LinearLayout) inflater.inflate(groserias_layout,null,false);
                    TextView tv_groserias_groseria=groserias_linear.findViewById(R.id.tv_groserias_groseria);
                    ImageView iv_graf=groserias_linear.findViewById(R.id.iv_groserias_graf);
                    TextView tv_groserias_frec=groserias_linear.findViewById(R.id.tv_groserias_frec);

                    int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                    float porGroseria=((float)frec[i]/(float)total);

                    tv_groserias_groseria.setText(palabras[i]);
                    iv_graf.setBackgroundColor(randomAndroidColor);
                    iv_graf.getLayoutParams().width=(int)(width*porGroseria);
                    tv_groserias_frec.setText(frec[i]+"");
                    group.addView(groserias_linear);
                }
            }
        }

    }
    public void crearMensajeDiasMasMenos(int diaMas,int diaMenos,long horaMas,long horaMenos,int cantMas,int cantMenos,String nom2){
        String dias[]={"Domingo","Lunes","Martes","Miércoles","Jueves","Viernes","Sábado"};
        String hora_Men=horaMenos+":00";
        String hora_Mas=horaMas+":00";
        LayoutInflater inflater=LayoutInflater.from(context);
        int id= R.layout.mensaje_mdiasmasmenos_i;//por defecto
        /*INSTANCIAN COMPONENTES*/
        this.linearLayout=(LinearLayout) inflater.inflate(id,null,false);
        TextView tv_titulo=linearLayout.findViewById(R.id.tv_diasmm_titulo);
        TextView tv_diaMas=linearLayout.findViewById(R.id.tv_diasmm_diamas);
        TextView tv_diaMenos=linearLayout.findViewById(R.id.tv_diasmm_diamen);
        if(!nom2.equals(""))
            tv_titulo.setText("Dia con más y menos mensajes con "+nom2);
        else
            tv_titulo.setText("Dia con más y menos mensajes ");
        tv_diaMas.setText(dias[diaMas]+" a las "+hora_Mas+" ("+cantMas+" mensajes)");
        tv_diaMenos.setText(dias[diaMenos]+" a las "+hora_Men+" ("+cantMenos+" mensajes)");
    }
    public void crearMensajeGroseria_I(ArrayList<String> groserias,ArrayList<Integer> frec,float porGroserias){
        LayoutInflater inflater=LayoutInflater.from(context);
        int id= R.layout.mensaje_mgroserias_i;//por defecto
        /*INSTANCIAN COMPONENTES*/
        this.linearLayout=(LinearLayout) inflater.inflate(id,null,false);

        ViewGroup group=linearLayout.findViewById(R.id.contenedor_groserias);
        int groserias_layout=R.layout.groserias_layout;

        int total=0;
        for(int frecuencia:frec){
            total+=frecuencia;
        }
        TextView tv_gral=linearLayout.findViewById(R.id.tv_groserias_gral);
        if(total>0){
            //se saca el tamaño del width
            //se asignan medidas de porcentajes
            analisisWhatsflags.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    width=analisisWhatsflags.getWindowManager().getDefaultDisplay().getWidth();

                }
            });
            width=(int) (width*.4);
            //se obtienen colores aleatorios
            int[] androidColors = analisisWhatsflags.getResources().getIntArray(R.array.androidcolors);

            tv_gral.setText("GROSERIAS: "+total+"("+String.format("%.02f",porGroserias)+"%)");
            for(int i=0;i<groserias.size();i++){
                LinearLayout groserias_linear=(LinearLayout) inflater.inflate(groserias_layout,null,false);
                TextView tv_groserias_groseria=groserias_linear.findViewById(R.id.tv_groserias_groseria);
                ImageView iv_graf=groserias_linear.findViewById(R.id.iv_groserias_graf);
                TextView tv_groserias_frec=groserias_linear.findViewById(R.id.tv_groserias_frec);

                int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                float porGroseria=((float)frec.get(i)/(float)total);

                tv_groserias_groseria.setText(groserias.get(i));
                iv_graf.setBackgroundColor(randomAndroidColor);
                iv_graf.getLayoutParams().width=(int)(width*porGroseria);
                tv_groserias_frec.setText(frec.get(i)+"");
                group.addView(groserias_linear);
            }
        }else {
            //no hay groserias
            tv_gral.setText("Ustedes no dicen groserías,¡Bien hecho!");
        }

    }
    public void crearMensajeEliminados_I(String nom1, String nom2, int cant1, int cant2){
        int total=cant1+cant2;
        float por1=0;
        if(cant1>0){
            por1=(float)((float)cant1/(float)total);
        }
        float por2=0;
        if(cant2>0){
            por2=(float)((float)cant2/(float)total);
        }

        LayoutInflater inflater=LayoutInflater.from(context);
        int id= R.layout.mensaje_mtotales_i;//por defecto
        /*INSTANCIAN COMPONENTES*/
        this.linearLayout=(LinearLayout) inflater.inflate(id,null,false);
        TextView titulo=linearLayout.findViewById(R.id.titulo_mtotalesi);
        TextView tv_u1=linearLayout.findViewById(R.id.tv_nom1_mtotales);
        TextView tv_u2=linearLayout.findViewById(R.id.tv_nom2_mtotales);
        TextView tv_cant1=linearLayout.findViewById(R.id.tv_num1_mtotales);
        TextView tv_cant2=linearLayout.findViewById(R.id.tv_num2_mtotales);
        TextView tv_por1=linearLayout.findViewById(R.id.tv_pormtotales1);
        TextView tv_por2=linearLayout.findViewById(R.id.tv_pormtotales2);
        ImageView iv_mensajesU1=linearLayout.findViewById(R.id.iv_mtotales1);
        ImageView iv_mensajesU2=linearLayout.findViewById(R.id.iv_mtotales2);
        titulo.setText("Mensajes eliminados: "+total);
        tv_u1.setText(nom1);
        tv_u2.setText(nom2);
        tv_cant1.setText(cant1+" mensajes");
        tv_cant2.setText(cant2+" mensajes");
        tv_por1.setText(String.format("%.0f",por1*100)+"%");
        tv_por2.setText(String.format("%.0f",por2*100)+"%");

        if(cant1>cant2){
            //iv_mensajesU1.setColorFilter(context.getResources().getColor(R.color.green));
            iv_mensajesU1.setBackgroundColor(Color.parseColor("#128C7E"));
            iv_mensajesU2.setBackgroundColor(Color.parseColor("#FF3333"));
        }else if(cant2<cant1){
            iv_mensajesU1.setBackgroundColor(Color.parseColor("#FF3333"));
            iv_mensajesU2.setBackgroundColor(Color.parseColor("#128C7E"));
        }else{
            iv_mensajesU1.setBackgroundColor(Color.parseColor("#FF3333"));
            iv_mensajesU2.setBackgroundColor(Color.parseColor("#FF3333"));
        }

        //se asignan medidas de porcentajes
        analisisWhatsflags.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                width=analisisWhatsflags.getWindowManager().getDefaultDisplay().getWidth();

            }
        });
        width=(int)(width*.8);//para que el maximo se considere el mensaje
        iv_mensajesU1.getLayoutParams().width=(int) (por1*width);
        iv_mensajesU2.getLayoutParams().width=(int) (por2*width);
    }
    public void crearMensajeLinks_I(ArrayList<Integer> links){
        LayoutInflater inflater=LayoutInflater.from(context);
        int id= R.layout.mensaje_links_i;//por defecto
        /*INSTANCIAN COMPONENTES*/
        this.linearLayout=(LinearLayout) inflater.inflate(id,null,false);

        ViewGroup group=linearLayout.findViewById(R.id.contenedor_links);
        //se crea otro layout para cada link que exista
        //imagenes de los links ya cargados
        //bloque links 0:tiktok,1:facebook,2:instagram,3:whatsApp,4:messenger,5:zoom,6:snapchat,7:telegram,8:capcut,9:meet,10:whatsapp bussines,11: picsart,12:youtube,13:twitter,14:spotify,15:pinterest,16:kuaishou,17:netflix,18:shein,19:inshot,20:por defectp
        int[] linksBG={R.drawable.tiktok,R.drawable.facebook,R.drawable.insta,R.drawable.whatsapp,R.drawable.messenger,R.drawable.zoom,R.drawable.snapchat,R.drawable.telegram,R.drawable.capcut,R.drawable.meet
                ,R.drawable.bussines,R.drawable.picsart,R.drawable.youtube,R.drawable.twitter,R.drawable.spotify,R.drawable.pinterest,R.drawable.kawai,R.drawable.netflix,R.drawable.shein,R.drawable.inshot
                ,R.drawable.defecto};
        for(int i=0;i<links.size();i++){
            if(links.get(i)!=0){
                int layout_link=R.layout.link_layout;
                LinearLayout layout_links=(LinearLayout)inflater.inflate(layout_link,null,false);
                ((TextView)layout_links.findViewById(R.id.frec_link)).setText(""+links.get(i));
                ((ImageView)layout_links.findViewById(R.id.iv_link)).setBackgroundResource(linksBG[i]);
                group.addView(layout_links);
            }
        }


    }
    public void crearMensajeEmois_I(String nom1,String nom2,String emojiFav1,String emojiFav2,int frecEmoji1,int frecEmoji2){
        LayoutInflater inflater=LayoutInflater.from(context);
        int id= R.layout.mensaje_emojis_i;//por defecto
        /*INSTANCIAN COMPONENTES*/
        this.linearLayout=(LinearLayout) inflater.inflate(id,null,false);
        TextView tv_nom1=linearLayout.findViewById(R.id.tv_nom1_emojis);
        TextView tv_nom2=linearLayout.findViewById(R.id.tv_nom2_emojis);
        TextView tv_emoji1=linearLayout.findViewById(R.id.tv_emojifrec1_emoji);
        TextView tv_emoji2=linearLayout.findViewById(R.id.tv_emojifrec2_emoji);
        tv_nom1.setText(nom1);
        tv_nom2.setText(nom2);
        if(!emojiFav1.equals("")){
            tv_emoji1.setText(frecEmoji1+"   "+emojiFav1);
        }else{
            //no tiene emoji fav
            tv_emoji1.setText("no usa emojis.");
        }
        if(!emojiFav2.equals("")){
            tv_emoji2.setText(frecEmoji2+"   "+emojiFav2);
        }else{
            //no tiene emoji fav
            tv_emoji2.setText("no usa emojis.");
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void crearMensajeTotales_I(String nom1, String nom2, int cant1, int cant2){
        int total=cant1+cant2;
        float por1=(float)((float)cant1/(float)total);
        float por2=(float)((float)cant2/(float)total);
        LayoutInflater inflater=LayoutInflater.from(context);
        int id= R.layout.mensaje_mtotales_i;//por defecto
        /*INSTANCIAN COMPONENTES*/
        this.linearLayout=(LinearLayout) inflater.inflate(id,null,false);
        TextView titulo=linearLayout.findViewById(R.id.titulo_mtotalesi);
        TextView tv_u1=linearLayout.findViewById(R.id.tv_nom1_mtotales);
        TextView tv_u2=linearLayout.findViewById(R.id.tv_nom2_mtotales);
        TextView tv_cant1=linearLayout.findViewById(R.id.tv_num1_mtotales);
        TextView tv_cant2=linearLayout.findViewById(R.id.tv_num2_mtotales);
        TextView tv_por1=linearLayout.findViewById(R.id.tv_pormtotales1);
        TextView tv_por2=linearLayout.findViewById(R.id.tv_pormtotales2);
        ImageView iv_mensajesU1=linearLayout.findViewById(R.id.iv_mtotales1);
        ImageView iv_mensajesU2=linearLayout.findViewById(R.id.iv_mtotales2);
        titulo.setText("Mensajes totales: "+total);
        tv_u1.setText(nom1);
        tv_u2.setText(nom2);
        tv_cant1.setText(cant1+" mensajes");
        tv_cant2.setText(cant2+" mensajes");
        tv_por1.setText(String.format("%.0f",por1*100)+"%");
        tv_por2.setText(String.format("%.0f",por2*100)+"%");

        if(cant1>cant2){
            //iv_mensajesU1.setColorFilter(context.getResources().getColor(R.color.green));
            iv_mensajesU1.setBackgroundColor(Color.parseColor("#128C7E"));
            iv_mensajesU2.setBackgroundColor(Color.parseColor("#FF3333"));
        }else if(cant2>cant1){
            iv_mensajesU1.setBackgroundColor(Color.parseColor("#FF3333"));
            iv_mensajesU2.setBackgroundColor(Color.parseColor("#128C7E"));
        }else{
            iv_mensajesU1.setBackgroundColor(Color.parseColor("#128C7E"));
            iv_mensajesU2.setBackgroundColor(Color.parseColor("#128C7E"));
        }

        //se asignan medidas de porcentajes
        analisisWhatsflags.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                width=analisisWhatsflags.getWindowManager().getDefaultDisplay().getWidth();

            }
        });
        iv_mensajesU1.getLayoutParams().width=(int) (por1*width);
        iv_mensajesU2.getLayoutParams().width=(int) (por2*width);
    }
    public void crearMensajeINICON_I(String nom1,String nom2,int vecesu1,int vecesu2){
        LayoutInflater inflater=LayoutInflater.from(context);
        int id= R.layout.mensaje_inicon_i;//por defecto
        /*INSTANCIAN COMPONENTES*/
        this.linearLayout=(LinearLayout) inflater.inflate(id,null,false);
        TextView tv_nom1=linearLayout.findViewById(R.id.tv_inicon_nom1);
        TextView tv_veces1=linearLayout.findViewById(R.id.tv_inicon_veces1);
        TextView tv_nom2=linearLayout.findViewById(R.id.tv_inicon_nom2);
        TextView tv_veces2=linearLayout.findViewById(R.id.tv_inicon_veces2);
        tv_nom1.setText(nom1+":");
        tv_nom2.setText(nom2+":");
        String por1="";
        String por2="";
        if(vecesu1>0){
            por1=String.format("%.02f",(float)((float)vecesu1/((float)vecesu1+(float) vecesu2))*100.0f);
        }else{
            por1="0";
        }
        if(vecesu2>0){
            por2=String.format("%.02f",(float)((float)vecesu2/((float)vecesu1+(float) vecesu2))*100.0f);
        }else {
            por2 = "0";
        }

        tv_veces1.setText("Inició la conversación "+vecesu1+" veces ("+por1+"%)");
        tv_veces2.setText("Inició la conversación "+vecesu2+" veces ("+por2+"%)");
    }
    public void crearMensajeTiempo_I(String nom1,String nom2,long t1,long t2){

        int tu1= (int) (t1/60);
        int tu2= (int) (t2/60);
        LayoutInflater inflater=LayoutInflater.from(context);
        int id= R.layout.mensaje_tiempo_i;//por defecto
        /*INSTANCIAN COMPONENTES*/
        this.linearLayout=(LinearLayout) inflater.inflate(id,null,false);
        TextView tv_nombreu1=linearLayout.findViewById(R.id.tv_nombre_u1_tr);
        TextView tv_tru1=linearLayout.findViewById(R.id.tv_tr_u1_tr);
        TextView tv_nombreu2=linearLayout.findViewById(R.id.tv_nombre_u2_tr);
        TextView tv_tru2=linearLayout.findViewById(R.id.tv_tr_u2_tr);
        tv_nombreu1.setText(nom1+" : ");
        tv_nombreu2.setText(nom2+" : ");

        tv_tru1.setText(tu1+" minutos");
        tv_tru2.setText(tu2+" minutos");
    }
    public void  crearMensajeFlags_I(int redflags,int greenflags,String[] banderasRed,String[] banderasGreen){
        LayoutInflater inflater=LayoutInflater.from(context);
        int id= R.layout.mensaje_flags_i;//por defecto
        /*INSTANCIAN COMPONENTES*/
        this.linearLayout=(LinearLayout) inflater.inflate(id,null,false);
        TextView greenflagsTv=(TextView) linearLayout.findViewById(R.id.tv_num_gf);
        TextView redflagsTv=(TextView) linearLayout.findViewById(R.id.tv_num_rf);
        LinearLayout greenflags_l= linearLayout.findViewById(R.id.linear_green_flags);
        LinearLayout redflags_l= linearLayout.findViewById(R.id.linear_red_flags);
        ImageView iv_greenf=linearLayout.findViewById(R.id.iv_flag_green);
        ImageView iv_redf=linearLayout.findViewById(R.id.iv_flag_red);
        /**FLAGS VERDES**/
        LinearLayout goodenergy_g=linearLayout.findViewById(R.id.gflag_goodenergy);
        TextView tv_goodg=linearLayout.findViewById(R.id.tv_goodenergy_gf);
        TextView cant_goodenergy=linearLayout.findViewById(R.id.cant_goodenergy);

        LinearLayout borrados_g=linearLayout.findViewById(R.id.gflag_m_borrado);
        TextView tv_borradog=linearLayout.findViewById(R.id.tv_mborrado_gf);
        TextView cant_borradosg=linearLayout.findViewById(R.id.cant_borrados);

        LinearLayout reciprocidad_g=linearLayout.findViewById(R.id.gflag_reciprocidad);
        TextView tv_reciprocidadg=linearLayout.findViewById(R.id.tv_reciprocidad_gf);
        TextView cant_reciprocidadg=linearLayout.findViewById(R.id.cant_reciprocidad);

        LinearLayout trespuesta_g=linearLayout.findViewById(R.id.gflag_trespuesta);
        TextView tv_trespuestag=linearLayout.findViewById(R.id.tv_trespuesta_gf);
        TextView cant_trespuestag=linearLayout.findViewById(R.id.cant_trespuesta);

        LinearLayout frec_conv_g=linearLayout.findViewById(R.id.gflag_frecconver);
        TextView tv_frec_convg=linearLayout.findViewById(R.id.tv_frecconver_gf);
        TextView cant_frecconverg=linearLayout.findViewById(R.id.cant_frecconver);

        LinearLayout madrugada_g=linearLayout.findViewById(R.id.gflag_madrugada);
        TextView tv_madrugadag=linearLayout.findViewById(R.id.tv_madrugada_gf);
        TextView cant_madrugadag=linearLayout.findViewById(R.id.cant_madrugada);

        LinearLayout groserias_g=linearLayout.findViewById(R.id.gflag_groserias);
        TextView tv_groseriasg=linearLayout.findViewById(R.id.tv_groserias_gf);
        TextView cant_groseriasg=linearLayout.findViewById(R.id.cant_groserias);

        LinearLayout perdidas_g=linearLayout.findViewById(R.id.gflag_perdidas);
        TextView tv_perdidasg=linearLayout.findViewById(R.id.tv_perdidas_gf);
        TextView cant_perdidasg=linearLayout.findViewById(R.id.cant_perdidas);

        LinearLayout multimedia_g=linearLayout.findViewById(R.id.gflag_multimedia);
        TextView tv_multimediag=linearLayout.findViewById(R.id.tv_multimedia_gf);
        TextView cant_multimediag=linearLayout.findViewById(R.id.cant_multimedia);

        LinearLayout links_g=linearLayout.findViewById(R.id.gflag_links);
        TextView tv_linksg=linearLayout.findViewById(R.id.tv_links_gf);
        TextView cant_linksg=linearLayout.findViewById(R.id.cant_links);
        /**FLAGS ROJAS**/
        LinearLayout borrado_r=linearLayout.findViewById(R.id.rflag_m_borrado);
        TextView tv_borrador=linearLayout.findViewById(R.id.tv_mborrado_rf);
        TextView cant_boradosr=linearLayout.findViewById(R.id.cant_borados_r);

        LinearLayout reciprocidad_r=linearLayout.findViewById(R.id.rflag_reciprocidad);
        TextView tv_reciprocidadr=linearLayout.findViewById(R.id.tv_reciprocidad_rf);
        TextView cant_reciprocidadr=linearLayout.findViewById(R.id.cant_reciprocidad_r);

        LinearLayout iniciac_r=linearLayout.findViewById(R.id.rflag_iniciac);
        TextView tv_iniciacr=linearLayout.findViewById(R.id.tv_iniciac_rf);
        TextView cant_iniconr=linearLayout.findViewById(R.id.cant_iniconver_r);

        LinearLayout lapso_r=linearLayout.findViewById(R.id.rflag_lapsos);
        TextView tv_lapsor=linearLayout.findViewById(R.id.tv_lapso_rf);
        TextView cant_lapsor=linearLayout.findViewById(R.id.cant_lapso_r);

        LinearLayout frec_conv_r=linearLayout.findViewById(R.id.rflag_frecconver);
        TextView tv_frec_convr=linearLayout.findViewById(R.id.tv_frecconver_rf);
        TextView cant_frecconverr=linearLayout.findViewById(R.id.cant_frecconver_r);

        LinearLayout madrugada_r=linearLayout.findViewById(R.id.rflag_madrugada);
        TextView tv_madrugadar=linearLayout.findViewById(R.id.tv_madrugada_rf);
        TextView cant_madrugadar=linearLayout.findViewById(R.id.cant_madrugada_r);

        LinearLayout groserias_r=linearLayout.findViewById(R.id.rflag_groserias);
        TextView tv_groseriasr=linearLayout.findViewById(R.id.tv_groserias_rf);
        TextView cant_groseriasr=linearLayout.findViewById(R.id.cant_groserias_r);

        LinearLayout perdidas_r=linearLayout.findViewById(R.id.rflag_perdidas);
        TextView tv_perdidasr=linearLayout.findViewById(R.id.tv_perdidas_rf);
        TextView cant_perdidasr=linearLayout.findViewById(R.id.cant_perdidas_r);

        LinearLayout links_r=linearLayout.findViewById(R.id.rflag_links);
        TextView tv_linksr=linearLayout.findViewById(R.id.tv_links_rf);
        TextView cant_linksr=linearLayout.findViewById(R.id.cant_links_r);

        greenflagsTv.setText(greenflags+"");
        redflagsTv.setText(redflags+"");

        /**INICIALMENTE SE VEN SOLO LAS GREENFLAGS**/
        redflags_l.setVisibility(View.GONE);
        iv_greenf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                greenflags_l.setVisibility(View.VISIBLE);
                redflags_l.setVisibility(View.GONE);
            }
        });
        iv_redf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                greenflags_l.setVisibility(View.GONE);
                redflags_l.setVisibility(View.VISIBLE);
            }
        });
        /**SE ACOMODAN DE ACUERDO A LAS BANDERAS DE ENTRADA GREEN**/
        if(!banderasGreen[0].equals("")) {

            if(banderasGreen[0].contains("*")){
                String[] aux=banderasGreen[0].split("\\*");
                cant_goodenergy.setText(aux[0]);
                tv_goodg.setText(aux[1]);
            }else{
                tv_goodg.setText(banderasGreen[0]);
            }
        }else goodenergy_g.setVisibility(View.GONE);
        if(!banderasGreen[1].equals("")) {
            if(banderasGreen[1].contains("*")){
                String[] aux=banderasGreen[1].split("\\*");
                cant_borradosg.setText(aux[0]);
                tv_borradog.setText(aux[1]);
            }else{
                tv_borradog.setText(banderasGreen[1]);
            }
        }else  borrados_g.setVisibility(View.GONE);
        if(!banderasGreen[2].equals("")) {
            if(banderasGreen[2].contains("*")){
                String[] aux=banderasGreen[2].split("\\*");
                cant_reciprocidadg.setText(aux[0]);
                tv_reciprocidadg.setText(aux[1]);
            }else{
                tv_reciprocidadg.setText(banderasGreen[2]);
            }
        }else  reciprocidad_g.setVisibility(View.GONE);
        if(!banderasGreen[3].equals("")) {
            String auxS=banderasGreen[3];
            if(auxS.contains("*")){
                String[] aux=auxS.split("\\*");
                cant_trespuestag.setText(aux[0]);
                tv_trespuestag.setText(aux[1]);
            }else{
                tv_trespuestag.setText(auxS);
            }
        }else  trespuesta_g.setVisibility(View.GONE);
        if(!banderasGreen[4].equals("")) {
            String auxS=banderasGreen[4];
            if(auxS.contains("*")){
                String[] aux=auxS.split("\\*");
                cant_frecconverg.setText(aux[0]);
                tv_frec_convg.setText(aux[1]);
            }else{
                tv_frec_convg.setText(auxS);
            }
        }else  frec_conv_g.setVisibility(View.GONE);
        if(!banderasGreen[5].equals("")) {
            String auxS=banderasGreen[5];
            if(auxS.contains("*")){
                String[] aux=auxS.split("\\*");
                cant_madrugadag.setText(aux[0]);
                tv_madrugadag.setText(aux[1]);
            }else{
                tv_madrugadag.setText(auxS);
            }
        }else  madrugada_g.setVisibility(View.GONE);
        if(!banderasGreen[6].equals("")) {
            String auxS=banderasGreen[6];
            if(auxS.contains("*")){
                String[] aux=auxS.split("\\*");
                cant_groseriasg.setText(aux[0]);
                tv_groseriasg.setText(aux[1]);
            }else{
                tv_groseriasg.setText(auxS);
            }
        }else  groserias_g.setVisibility(View.GONE);
        if(!banderasGreen[7].equals("")) {
            String auxS=banderasGreen[7];
            if(auxS.contains("*")){
                String[] aux=auxS.split("\\*");
                cant_perdidasg.setText(aux[0]);
                tv_perdidasg.setText(aux[1]);
            }else{
                tv_perdidasg.setText(auxS);
            }
        }else  perdidas_g.setVisibility(View.GONE);
        if(!banderasGreen[8].equals("")) {
            String auxS=banderasGreen[8];
            if(auxS.contains("*")){
                String[] aux=auxS.split("\\*");
                cant_multimediag.setText(aux[0]);
                tv_multimediag.setText(aux[1]);
            }else{
                tv_multimediag.setText(auxS);
            }
        }else  multimedia_g.setVisibility(View.GONE);
        if(!banderasGreen[9].equals("")) {
            String auxS=banderasGreen[9];
            if(auxS.contains("*")){
                String[] aux=auxS.split("\\*");
                cant_linksg.setText(aux[0]);
                tv_linksg.setText(aux[1]);
            }else{
                tv_linksg.setText(auxS);
            }
        }else  links_g.setVisibility(View.GONE);
        /**SE ACOMODAN DE ACUERDO A LAS BANDERAS DE ENTRADA RED**/
        if(!banderasRed[0].equals("")) {
            String auxS=banderasRed[0];
            if(auxS.contains("*")){
                String[] aux=auxS.split("\\*");
                cant_boradosr.setText(aux[0]);
                tv_borrador.setText(aux[1]);
            }else{
                tv_borrador.setText(auxS);
            }
        }else borrado_r.setVisibility(View.GONE);
        if(!banderasRed[1].equals("")) {
            String auxS=banderasRed[1];
            if(auxS.contains("*")){
                String[] aux=auxS.split("\\*");
                cant_reciprocidadr.setText(aux[0]);
                tv_reciprocidadr.setText(aux[1]);
            }else{
                tv_reciprocidadr.setText(auxS);
            }
        }else reciprocidad_r.setVisibility(View.GONE);
        if(!banderasRed[2].equals("")) {
            String auxS=banderasRed[2];
            if(auxS.contains("*")){
                String[] aux=auxS.split("\\*");
                cant_iniconr.setText(aux[0]);
                tv_iniciacr.setText(aux[1]);
            }else{
                tv_iniciacr.setText(auxS);
            }
        }else iniciac_r.setVisibility(View.GONE);
        if(!banderasRed[3].equals("")) {
            String auxS=banderasRed[3];
            if(auxS.contains("*")){
                String[] aux=auxS.split("\\*");
                cant_lapsor.setText(aux[0]);
                tv_lapsor.setText(aux[1]);
            }else{
                tv_lapsor.setText(auxS);
            }
        }else lapso_r.setVisibility(View.GONE);
        if(!banderasRed[4].equals("")) {
            String auxS=banderasRed[4];
            if(auxS.contains("*")){
                String[] aux=auxS.split("\\*");
                cant_frecconverr.setText(aux[0]);
                tv_frec_convr.setText(aux[1]);
            }else{
                tv_frec_convr.setText(auxS);
            }
        }else frec_conv_r.setVisibility(View.GONE);
        if(!banderasRed[5].equals("")) {
            String auxS=banderasRed[5];
            if(auxS.contains("*")){
                String[] aux=auxS.split("\\*");
                cant_madrugadar.setText(aux[0]);
                tv_madrugadar.setText(aux[1]);
            }else{
                tv_madrugadar.setText(auxS);
            }
        }else madrugada_r.setVisibility(View.GONE);
        if(!banderasRed[6].equals("")) {
            String auxS=banderasRed[6];
            if(auxS.contains("*")){
                String[] aux=auxS.split("\\*");
                cant_groseriasr.setText(aux[0]);
                tv_groseriasr.setText(aux[1]);
            }else{
                tv_groseriasr.setText(auxS);
            }
        }else groserias_r.setVisibility(View.GONE);
        if(!banderasRed[7].equals("")) {
            String auxS=banderasRed[7];
            if(auxS.contains("*")){
                String[] aux=auxS.split("\\*");
                cant_perdidasr.setText(aux[0]);
                tv_perdidasr.setText(aux[1]);
            }else{
                tv_perdidasr.setText(auxS);
            }
        } else perdidas_r.setVisibility(View.GONE);
        if(!banderasRed[8].equals("")) {
            String auxS=banderasRed[8];
            if(auxS.contains("*")){
                String[] aux=auxS.split("\\*");
                cant_linksr.setText(aux[0]);
                tv_linksr.setText(aux[1]);
            }else{
                tv_linksr.setText(auxS);
            }
        } else links_r.setVisibility(View.GONE);
    }
    //***func secundarias
    private ValueAnimator slideAnimator(int start, int end,ViewGroup collapsablelayout)
    {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = collapsablelayout.getLayoutParams();
                layoutParams.height = value;
                collapsablelayout.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }
}
