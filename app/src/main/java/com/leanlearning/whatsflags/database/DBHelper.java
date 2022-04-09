package com.leanlearning.whatsflags.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION=3;//este se cambia
    private static final String DATABASE_NAME="whatsflags1.db";
    private static final String TABLA_CONTACTOS="t_contactos";
    private static final String TABLA_GRUPOS="t_grupos";
    private static final String TABLA_GENERAL="t_general";

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+TABLA_CONTACTOS+"("+"id INTEGER PRIMARY KEY AUTOINCREMENT,"+"nombre TEXT NOT NULL,"+"numero TEXT NOT NULL,"+"analizado INTEGER,fecha TEXT,usuario_nom1 TEXT,usuario_nom2 TEXT,lapso TEXT,tiempoRespuestaU1 INTEGER,tiempoRespuestaU2 INTEGER,veces_inicio1 INTEGER,veces_inicio2 INTEGER,cantidadMensajesU1 INTEGER,cantidadMensajesU2 INTEGER,emoji_fav_u1 TEXT,emoji_fav_u2 TEXT,frec_fav_u1 INTEGER,frec_fav_u2 INTEGER,frecLinks TEXT,numLinks INTEGER,eliminados_u1 INTEGER,eliminados_u2 INTEGER,dia_mas_m INTEGER,dia_men_m INTEGER,cantidad_mas INTEGER,cantidad_menos INTEGER,hora_masM INTEGER,hora_menosM INTEGER,palabras_mas_usadas TEXT,llamadas_perdidas INTEGER,video_perdidas INTEGER,multimedia_u1 INTEGER,multimedia_u2 INTEGER,groseriasFrec TEXT,porGroserias NUMERIC,mensajesMadrugada INTEGER,mensajes_al_ano INTEGER,maxLapso INTEGER,horas_inU1 NUMERIC,mensajes2021 TEXT,mensajes2022 TEXT,redflags INTEGER,greenflags INTEGER,banderas_red_cad TEXT,banderas_green_cad TEXT"+")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE "+TABLA_CONTACTOS);
        onCreate(sqLiteDatabase);
        System.out.println("Actualizacion");
    }
}
