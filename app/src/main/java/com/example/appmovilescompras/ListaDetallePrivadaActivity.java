package com.example.appmovilescompras;

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

import java.util.List;

// CAMBIADO: Hereda de BaseActivity para aplicar Temas e Idiomas
public class ListaDetallePrivadaActivity extends BaseActivity {

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

        // BaseActivity aplica el idioma y el tema aquí
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_detalle);

        configurarSonido();

        // Ajuste de barras del sistema (Responsive)
        View mainView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        // Recojo datos del Intent
        listaId = getIntent().getLongExtra("LISTA_ID", -1);
        String nombreLista = getIntent().getStringExtra("NOMBRE_LISTA");

        if (listaId == -1) {
            // ADAPTADO: String del recurso de idioma
            Toast.makeText(this, R.string.error_abrir_lista, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Título de la lista
        TextView tvTitulo = findViewById(R.id.textViewNombreLista);
        if (tvTitulo != null) {
            tvTitulo.setText(nombreLista);
        }

        // Botón Volver
        View btnVolver = findViewById(R.id.btnVolver);
        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> {
                reproducirSonido();
                finish(); // Cierra la actividad y vuelve a la anterior
            });
        }

        // Configuración de base de datos local (SQLite)
        dbHelper = new DBHelper(this);
        productos = dbHelper.getProductosPorLista(listaId);

        // RecyclerView con el adaptador de productos internos (AdapterI)
        RecyclerView rv = findViewById(R.id.recyclerViewProductos);
        if (rv != null) {
            rv.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ProductoAdapterI(productos, (producto, position) -> {
                // El diálogo de confirmación ya está dentro del AdapterI
                dbHelper.deleteProducto(producto.getId());
                productos.remove(position);
                adapter.notifyItemRemoved(position);
            });
            rv.setAdapter(adapter);
        }

        // Botón Añadir Producto
        View btnAdd = findViewById(R.id.btn_añadir_producto);
        if (btnAdd != null) {
            btnAdd.setOnClickListener(v -> {
                reproducirSonido();
                mostrarDialogoAñadir();
            });
        }
    }

    private void mostrarDialogoAñadir() {
        // TIPO DE DIÁLOGO: Custom Layout Dialog
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

                        // Guardar en SQLite
                        long nuevoId = dbHelper.addProducto(listaId, nombre, cantidad);
                        Producto nuevo = new Producto((int)nuevoId, nombre, cantidad);

                        // Actualizar lista y adaptador
                        productos.add(nuevo);
                        adapter.notifyItemInserted(productos.size() - 1);
                        Toast.makeText(this, R.string.producto_añadido, Toast.LENGTH_SHORT).show();
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