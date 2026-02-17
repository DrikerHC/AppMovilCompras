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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ListaDetalleActivity extends AppCompatActivity {

    private ProductoAdapter adapter;
    private DatabaseReference productosRef;

    private SoundPool soundPool;
    private int idSonidoClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Bloquear modo oscuro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_detalle);

        configurarSonido();

        // Esto de aqui es simplemente porque en algunos moviles se superpone la barra inferior del propio telefono con la de la app,
        // y entonces no se puede usar bien
        View mainView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Aplico padding abajo para que el botón no se pegue a la barra de navegación
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        // Se recogen los datos
        String listaKey = getIntent().getStringExtra("LISTA_KEY");
        String nombreLista = getIntent().getStringExtra("NOMBRE_LISTA");

        if (listaKey == null) {
            Toast.makeText(this, "Error al abrir la lista", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvTitulo = findViewById(R.id.textViewNombreLista);
        if (tvTitulo != null) {
            tvTitulo.setText(nombreLista);
        }

        View btnVolver = findViewById(R.id.btnVolver);
        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> {
                reproducirSonido();
                Intent intent = new Intent(ListaDetalleActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }

        productosRef = FirebaseDatabase.getInstance().getReference("compras")
                .child(listaKey)
                .child("productos");

        View btnAdd = findViewById(R.id.btn_añadir_producto);
        if (btnAdd != null) {
            btnAdd.setOnClickListener(v -> {
                reproducirSonido();
                mostrarDialogoAñadir();
            });
        }

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
                .setPositiveButton("Añadir", (dialog, which) -> {
                    reproducirSonido();
                    String nombre = etNombre.getText().toString().trim();
                    String cantidadStr = etCantidad.getText().toString().trim();

                    if (!nombre.isEmpty() && !cantidadStr.isEmpty()) {
                        int cantidad = Integer.parseInt(cantidadStr);
                        Producto nuevo = new Producto(nombre, cantidad);
                        productosRef.push().setValue(nuevo)
                                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Producto añadido", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    reproducirSonido();
                    dialog.dismiss();
                });

        builder.create().show();
    }

    // Las funciones para el sonido
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