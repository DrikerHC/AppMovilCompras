package com.example.appmovilescompras;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);

        // 1. Aplicar Idioma
        String lang = prefs.getString("idioma", "es");
        cambiarIdioma(lang);

        // 2. Aplicar Tema (Color)
        // IMPORTANTE: Usamos getInt y la misma llave "tema_color" que en Settings
        int tema = prefs.getInt("tema_color", 0);

        if (tema == 1) { // 1 es Rojo
            setTheme(R.style.Theme_Rojo);
        } else if (tema == 2) { // 2 es Verde
            setTheme(R.style.Theme_Verde);
        } else { // 0 o por defecto es Azul
            setTheme(R.style.Theme_AppMovilesCompras);
        }

        // 3. Aplicar tamaño de letra ANTES del super.onCreate
        float escala = prefs.getFloat("mida_lletra", 1.0f);
        Configuration config = getResources().getConfiguration();
        config.fontScale = escala; // Cambia el tamaño de la fuente globalmente

        // Actualizamos la configuración con la escala de fuente
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // IMPORTANTE: super.onCreate debe ir DESPUÉS de setTheme e idioma
        super.onCreate(savedInstanceState);
    }

    private void cambiarIdioma(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}
