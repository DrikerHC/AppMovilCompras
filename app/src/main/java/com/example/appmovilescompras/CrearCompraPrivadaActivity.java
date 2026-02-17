package com.example.appmovilescompras;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Calendar;

public class CrearCompraPrivadaActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;

    private EditText etNombre, etFecha;
    private ImageView imgPreview;
    private Button btnElegirImagen, btnGuardar;

    private Uri imageUri;
    private String imagePath;
    private DBHelper dbHelper;

    // Variables para sonido
    private SoundPool soundPool;
    private int idSonidoClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Bloquear modo oscuro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_lista);

        // Configurar Sonido
        configurarSonido();

        // 1. Referencias de UI
        etNombre = findViewById(R.id.et_nombre_lista);
        etFecha = findViewById(R.id.et_fecha_lista);
        imgPreview = findViewById(R.id.img_preview);
        btnElegirImagen = findViewById(R.id.btn_elegir_imagen);
        btnGuardar = findViewById(R.id.btn_guardar);

        // 2. Inicio el Sqlite
        dbHelper = new DBHelper(this);

        etFecha.setFocusable(false); // Es para que no se habra el teclado
        etFecha.setOnClickListener(v -> {
            reproducirSonido();
            mostrarSelectorFecha();
        });

        // 3. En este caso si que puedo meter una imagen porque va a la BD de sqlite
        btnElegirImagen.setOnClickListener(v -> {
            reproducirSonido();
            abrirGaleria();
        });

        btnGuardar.setOnClickListener(v -> {
            reproducirSonido();
            guardarCompraPrivada();
        });
    }

    private void mostrarSelectorFecha() {
        final Calendar c = Calendar.getInstance();
        int año = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH);
        int dia = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    // Formato Día/Mes/Año
                    String fechaSeleccionada = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    etFecha.setText(fechaSeleccionada);
                }, año, mes, dia);

        datePickerDialog.show();
    }

    //Esta funcion te abre la galeria para que puedas elegir una de las fotos
    private void abrirGaleria() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgPreview.setImageURI(imageUri);
            imagePath = imageUri.toString();
        }
    }

    private void guardarCompraPrivada() {
        String nombre = etNombre.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();

        if (nombre.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        long id = dbHelper.addLista(nombre, fecha, imagePath);

        if (id != -1) {
            Toast.makeText(this, "Lista privada guardada", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al guardar la lista", Toast.LENGTH_SHORT).show();
        }
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