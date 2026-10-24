package com.example.appmovilescompras;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// CAMBIADO: Ahora hereda de BaseActivity
public class ListaDetalleActivity extends BaseActivity {

    private ProductoAdapter adapter;
    private DatabaseReference productosRef;
    private SoundPool soundPool;
    private int idSonidoClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // BaseActivity aplicará el idioma y el tema aquí
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_detalle);

        configurarSonido();

        // Configuración de Insets (pantalla completa)
        View mainView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        String listaKey = getIntent().getStringExtra("LISTA_KEY");
        String nombreLista = getIntent().getStringExtra("NOMBRE_LISTA");

        if (listaKey == null) {
            Toast.makeText(this, R.string.error_abrir_lista, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvTitulo = findViewById(R.id.textViewNombreLista);
        if (tvTitulo != null) {
            tvTitulo.setText(nombreLista);
        }

        // Botón Volver
        View btnVolver = findViewById(R.id.btnVolver);
        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> {
                reproducirSonido();
                finish(); // Simplemente cerramos para volver atrás
            });
        }

        productosRef = FirebaseDatabase.getInstance().getReference("compras")
                .child(listaKey)
                .child("productos");

        // Botón Añadir Producto
        View btnAdd = findViewById(R.id.btn_añadir_producto);
        if (btnAdd != null) {
            btnAdd.setOnClickListener(v -> {
                reproducirSonido();
                mostrarDialogoAñadir();
            });
        }

        // RecyclerView
        RecyclerView rv = findViewById(R.id.recyclerViewProductos);
        if (rv != null) {
            rv.setLayoutManager(new LinearLayoutManager(this));
            FirebaseRecyclerOptions<Producto> options = new FirebaseRecyclerOptions.Builder<Producto>()
                    .setQuery(productosRef, Producto.class)
                    .build();
            adapter = new ProductoAdapter(options);
            rv.setAdapter(adapter);
        }
    }

    private void mostrarDialogoAñadir() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.add_producto, null);

        EditText etNombre = v.findViewById(R.id.etNombreProducto);
        EditText etCantidad = v.findViewById(R.id.etCantidadProducto);

        builder.setView(v)
                .setPositiveButton(R.string.boton_añadir, (dialog, which) -> {
                    reproducirSonido();
                    String nombre = etNombre.getText().toString().trim();
                    String cantidadStr = etCantidad.getText().toString().trim();

                    if (!nombre.isEmpty() && !cantidadStr.isEmpty()) {
                        int cantidad = Integer.parseInt(cantidadStr);
                        Producto nuevo = new Producto(nombre, cantidad);
                        productosRef.push().setValue(nuevo)
                                .addOnSuccessListener(aVoid -> Toast.makeText(this, R.string.producto_añadido, Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(this, R.string.error_guardar_producto, Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(this, R.string.error_campos_vacios, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.boton_cancelar, (dialog, which) -> {
                    reproducirSonido();
                    dialog.dismiss();
                });

        builder.create().show();
    }

    // ... (El resto de métodos configurarSonido, onStart, etc. se mantienen igual)
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
    protected void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
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