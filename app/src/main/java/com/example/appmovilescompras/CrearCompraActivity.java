package com.example.appmovilescompras;

import android.app.DatePickerDialog; // Añadido
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

import java.util.Calendar; // Añadido

public class CrearCompraActivity extends AppCompatActivity {

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
        // Esto es para bloquear que se pueda poner el modo oscuro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_lista);

        // llamo a la funcion de configurar los sonidos
        configurarSonido();

        etNombre = findViewById(R.id.et_nombre_lista);
        etFecha = findViewById(R.id.et_fecha_lista);
        btnElegirColor = findViewById(R.id.btn_elegir_imagen);
        btnGuardar = findViewById(R.id.btn_guardar);
        imgPreview = findViewById(R.id.img_preview);

        comprasRef = FirebaseDatabase.getInstance().getReference("compras");

        // He puesto el selector este de fechas que es mas comodo
        etFecha.setFocusable(false);
        etFecha.setOnClickListener(v -> {
            reproducirSonido();
            mostrarSelectorFecha();
        });

        //Elegir color porque en firebase no puedo subir imagenes
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
        // Esto simplemente es para que el selector aparezca por defecto en la fecha actual
        final Calendar c = Calendar.getInstance();
        int año = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH);
        int dia = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    // Formateo la fecha (Día/Mes/Año)
                    String fechaSeleccionada = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    etFecha.setText(fechaSeleccionada);
                }, año, mes, dia);

        datePickerDialog.show();
    }

    private void mostrarSelectorColores() {
        //Enseño las opciones de colores en la app(Estaria bien cambiarlos son un poco feos)
        final String[] nombresColores = {"Rojo", "Azul", "Verde", "Amarillo", "Naranja", "Rosa", "Morado"};
        final String[] codigosColores = {"#FF0000", "#0000FF", "#00FF00", "#FFFF00", "#FF8C00", "#FFC0CB", "#800080"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona un color");

        builder.setItems(nombresColores, (dialog, which) -> {
            reproducirSonido();
            colorSeleccionado = codigosColores[which];
            imgPreview.setImageDrawable(null);
            imgPreview.setBackgroundColor(Color.parseColor(colorSeleccionado));
            Toast.makeText(this, "Color seleccionado", Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }

    private void guardarCompra() {
        String nombre = etNombre.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();

        if (nombre.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Compra compra = new Compra(nombre, fecha, colorSeleccionado);

        comprasRef.push().setValue(compra)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Lista guardada", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show());
    }

    // Funcion que configura el sonido
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