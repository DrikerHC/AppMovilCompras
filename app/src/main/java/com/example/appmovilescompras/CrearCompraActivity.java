package com.example.appmovilescompras;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class CrearCompraActivity extends BaseActivity {

    private EditText etNombre, etFecha;
    private Button btnElegirColor, btnGuardar;
    private ImageView imgPreview;

    private DatabaseReference comprasRef;
    private String colorSeleccionado = "#808080";

    // Variables para sonido
    private SoundPool soundPool;
    private int idSonidoClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Bloquear modo oscuro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_lista);

        configurarSonido();

        etNombre = findViewById(R.id.et_nombre_lista);
        etFecha = findViewById(R.id.et_fecha_lista);
        btnElegirColor = findViewById(R.id.btn_elegir_imagen);
        btnGuardar = findViewById(R.id.btn_guardar);
        imgPreview = findViewById(R.id.img_preview);

        comprasRef = FirebaseDatabase.getInstance().getReference("compras");

        etFecha.setFocusable(false);
        etFecha.setOnClickListener(v -> {
            reproducirSonido();
            mostrarSelectorFecha();
        });

        btnElegirColor.setOnClickListener(v -> {
            reproducirSonido();
            mostrarSelectorColores();
        });

        btnGuardar.setOnClickListener(v -> {
            reproducirSonido();
            guardarCompra();
        });
    }

    private void mostrarSelectorFecha() {
        final Calendar c = Calendar.getInstance();
        int año = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH);
        int dia = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    String fechaSeleccionada = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    etFecha.setText(fechaSeleccionada);
                }, año, mes, dia);

        datePickerDialog.show();
    }

    private void mostrarSelectorColores() {
        // ADAPTADO: Obtenemos los nombres de los colores de los recursos de idioma
        final String[] nombresColores = {
                getString(R.string.color_rojo),
                getString(R.string.color_azul),
                getString(R.string.color_verde),
                getString(R.string.color_amarillo),
                getString(R.string.color_naranja),
                getString(R.string.color_rosa),
                getString(R.string.color_morado)
        };

        final String[] codigosColores = {"#FF0000", "#0000FF", "#00FF00", "#FFFF00", "#FF8C00", "#FFC0CB", "#800080"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // ADAPTADO: Título traducido
        builder.setTitle(R.string.titulo_selector_color);

        builder.setItems(nombresColores, (dialog, which) -> {
            reproducirSonido();
            colorSeleccionado = codigosColores[which];
            imgPreview.setImageDrawable(null);
            imgPreview.setBackgroundColor(Color.parseColor(colorSeleccionado));

            // ADAPTADO: Toast traducido
            Toast.makeText(this, R.string.color_seleccionado, Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }

    private void guardarCompra() {
        String nombre = etNombre.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();

        if (nombre.isEmpty() || fecha.isEmpty()) {
            // ADAPTADO: Mensaje de error traducido
            Toast.makeText(this, R.string.error_campos_vacios, Toast.LENGTH_SHORT).show();
            return;
        }

        Compra compra = new Compra(nombre, fecha, colorSeleccionado);

        comprasRef.push().setValue(compra)
                .addOnSuccessListener(aVoid -> {
                    // ADAPTADO: Mensaje de éxito traducido
                    Toast.makeText(this, R.string.lista_guardada, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    // ADAPTADO: Mensaje de fallo traducido
                    Toast.makeText(this, R.string.error_guardar, Toast.LENGTH_SHORT).show();
                });
    }

    private void configurarSonido() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build();
        idSonidoClick = soundPool.load(this, R.raw.sonidoboton, 1);
    }

    private void reproducirSonido() {
        if (idSonidoClick != 0) {
            soundPool.play(idSonidoClick, 1, 1, 0, 0, 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}