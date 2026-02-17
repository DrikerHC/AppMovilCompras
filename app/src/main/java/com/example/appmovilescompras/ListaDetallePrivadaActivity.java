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

import java.util.List;

public class ListaDetallePrivadaActivity extends AppCompatActivity {

    private ProductoAdapterI adapter;
    private DBHelper dbHelper;
    private long listaId;
    private List<Producto> productos;

    private SoundPool soundPool;
    private int idSonidoClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Bloquear modo oscuro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_detalle);

        configurarSonido();

        View mainView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        //Recogo datos
        listaId = getIntent().getLongExtra("LISTA_ID", -1);
        String nombreLista = getIntent().getStringExtra("NOMBRE_LISTA");

        if (listaId == -1) {
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
                finish();
            });
        }


        dbHelper = new DBHelper(this);
        productos = dbHelper.getProductosPorLista(listaId);


        RecyclerView rv = findViewById(R.id.recyclerViewProductos);
        if (rv != null) {
            rv.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ProductoAdapterI(productos, (producto, position) -> {
                reproducirSonido(); // Sonido al borrar
                dbHelper.deleteProducto(producto.getId());
                productos.remove(position);
                adapter.notifyItemRemoved(position);
            });
            rv.setAdapter(adapter);
        }


        View btnAdd = findViewById(R.id.btn_añadir_producto);
        if (btnAdd != null) {
            btnAdd.setOnClickListener(v -> {
                reproducirSonido();
                mostrarDialogoAñadir();
            });
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

                        long nuevoId = dbHelper.addProducto(listaId, nombre, cantidad);
                        Producto nuevo = new Producto((int)nuevoId, nombre, cantidad);

                        productos.add(nuevo);
                        adapter.notifyItemInserted(productos.size() - 1);
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