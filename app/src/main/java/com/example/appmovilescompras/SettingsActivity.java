package com.example.appmovilescompras;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingsActivity extends BaseActivity {

    private Spinner spinnerIdioma, spinnerColor, spinnerLetra;
    private CheckBox checkNotis;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 1. Referencias (CORREGIDO EL ID PARA EL XML)
        spinnerIdioma = findViewById(R.id.spinner_idioma_nuevo); // Coincide con tu XML
        spinnerColor = findViewById(R.id.spinner_color);
        spinnerLetra = findViewById(R.id.spinner_letra);
        checkNotis = findViewById(R.id.check_notis);
        btnGuardar = findViewById(R.id.btn_guardar_settings);

        SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);

        // 2. CONFIGURACIÓN DE ADAPTADORES (CON MEJOR VISIBILIDAD)

        // --- IDIOMA ---
        String[] idiomasCodigos = {"es", "ca", "en"};
        String[] idiomasNombres = {"Español", "Català", "English"};

        // Usamos simple_spinner_item que es más compatible con colores de texto
        ArrayAdapter<String> adapterIdioma = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, idiomasNombres);
        // Esta línea es VITAL para que el desplegable sea visible y tenga espacio
        adapterIdioma.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIdioma.setAdapter(adapterIdioma);

        // --- COLOR ---
        String[] colores = {"Azul", "Rojo", "Verde"};
        ArrayAdapter<String> adapterColor = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, colores);
        adapterColor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColor.setAdapter(adapterColor);

        // --- LETRA ---
        String[] tamaños = {getString(R.string.letra_pequeña), getString(R.string.letra_normal), getString(R.string.letra_grande)};
        ArrayAdapter<String> adapterLetra = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tamaños);
        adapterLetra.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLetra.setAdapter(adapterLetra);

        // 3. CARGAR VALORES ACTUALES
        checkNotis.setChecked(prefs.getBoolean("notificaciones", true));

        // Cargar selección de idioma
        String currentLang = prefs.getString("idioma", "es");
        for (int i = 0; i < idiomasCodigos.length; i++) {
            if (idiomasCodigos[i].equals(currentLang)) spinnerIdioma.setSelection(i);
        }

        // Cargar selección de tema (0, 1, 2)
        spinnerColor.setSelection(prefs.getInt("tema_color", 0));

        // Cargar selección de letra
        float currentLetra = prefs.getFloat("mida_lletra", 1.0f);
        if (currentLetra == 0.8f) spinnerLetra.setSelection(0);
        else if (currentLetra == 1.2f) spinnerLetra.setSelection(2);
        else spinnerLetra.setSelection(1);

        // 4. BOTÓN GUARDAR
        btnGuardar.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();

            // Guardar código de idioma ("es", "ca"...)
            editor.putString("idioma", idiomasCodigos[spinnerIdioma.getSelectedItemPosition()]);

            // Guardar posición del tema (0, 1, 2)
            editor.putInt("tema_color", spinnerColor.getSelectedItemPosition());

            // Guardar tamaño letra
            editor.putFloat("mida_lletra", obtenerMidaLetra(spinnerLetra.getSelectedItemPosition()));

            // Guardar notificaciones
            editor.putBoolean("notificaciones", checkNotis.isChecked());

            editor.apply();

            Toast.makeText(this, R.string.guardar_ajustes, Toast.LENGTH_SHORT).show();

            // REINICIAR APP PARA APLICAR CAMBIOS
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private float obtenerMidaLetra(int position) {
        if (position == 0) return 0.8f;
        if (position == 2) return 1.2f;
        return 1.0f;
    }
}