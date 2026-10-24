package com.example.appmovilescompras;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListaPrivadaActivity extends BaseActivity { // Hereda de BaseActivity para el tema

    private RecyclerView recyclerView;
    private CompraAdapterI adapter;
    private DBHelper dbHelper;
    private List<Compra> listasPrivadas;

    private SoundPool soundPool;
    private int idSonidoClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // El tema se aplica automáticamente gracias a BaseActivity.onCreate
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_listas_compra);

        // Configuración de Sonido
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build();

        idSonidoClick = soundPool.load(this, R.raw.sonidoboton, 1);

        // Ajuste Responsive para barras de sistema
        View mainView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        // --- BOTÓN DE AJUSTES (AÑADIDO) ---
        findViewById(R.id.btn_ir_ajustes).setOnClickListener(v -> {
            reproducirSonido();
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        dbHelper = new DBHelper(this);
        recyclerView = findViewById(R.id.rv_listas_compra);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listasPrivadas = dbHelper.getAllListas();

        adapter = new CompraAdapterI(listasPrivadas, new CompraAdapterI.OnItemClickListener() {
            @Override
            public void onItemClick(Compra compra) {
                reproducirSonido();
                Intent intent = new Intent(ListaPrivadaActivity.this, ListaDetallePrivadaActivity.class);
                intent.putExtra("LISTA_ID", compra.getId());
                intent.putExtra("NOMBRE_LISTA", compra.getNombre());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(Compra compra) {
                mostrarDialogoEliminar(compra);
            }
        });

        recyclerView.setAdapter(adapter);

        findViewById(R.id.fab_add_lista).setOnClickListener(v -> {
            reproducirSonido();
            Intent intent = new Intent(this, CrearCompraPrivadaActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btn_nav_compartidas).setOnClickListener(v -> {
            reproducirSonido();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void reproducirSonido() {
        if (idSonidoClick != 0 && soundPool != null) {
            soundPool.play(idSonidoClick, 1, 1, 0, 0, 1);
        }
    }

    private void mostrarDialogoEliminar(Compra compra) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.titulo_borrar_lista)
                .setMessage(getString(R.string.mensaje_borrar_lista, compra.getNombre()))
                .setPositiveButton(R.string.eliminar, (dialog, which) -> {
                    reproducirSonido();
                    boolean eliminado = dbHelper.deleteLista((int) compra.getId());
                    if (eliminado) {
                        listasPrivadas.remove(compra);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(this, R.string.lista_eliminada, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, R.string.error_eliminar, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.boton_cancelar, null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshListasPrivadas();
    }

    private void refreshListasPrivadas() {
        if (listasPrivadas != null) {
            listasPrivadas.clear();
            listasPrivadas.addAll(dbHelper.getAllListas());
            if (adapter != null) adapter.notifyDataSetChanged();
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