package com.example.appmovilescompras;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Cambiado: Hereda de BaseActivity para aplicar preferencias de idioma/tema
public class MainActivity extends BaseActivity implements CompraAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private CompraAdapter adapter;
    private DatabaseReference comprasRef;

    private SoundPool soundPool;
    private int idSonidoClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1. Bloquear modo oscuro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // 2. Ejecutar constructor del padre (aplica idioma y tema en BaseActivity)
        super.onCreate(savedInstanceState);

        // 3. Cargar el Layout ANTES de buscar los botones
        setContentView(R.layout.lista_listas_compra);

        // 4. Ahora ya podemos configurar el botón de Ajustes sin que de error
        View btnAjustes = findViewById(R.id.btn_ir_ajustes);
        if (btnAjustes != null) {
            btnAjustes.setOnClickListener(v -> {
                reproducirSonido();
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            });
        }

        // Configuración de Sonido
        configurarSonido();

        // Responsive Insets (Ajuste barra de navegación)
        View mainView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        // Configuración de Firebase y RecyclerView
        comprasRef = FirebaseDatabase.getInstance().getReference("compras");
        recyclerView = findViewById(R.id.rv_listas_compra);

        recyclerView.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (IndexOutOfBoundsException e) {
                    Log.e("MainActivity", "Inconsistencia de índices detectada.");
                }
            }
        });

        FirebaseRecyclerOptions<Compra> options = new FirebaseRecyclerOptions.Builder<Compra>()
                .setQuery(comprasRef, Compra.class)
                .build();

        adapter = new CompraAdapter(options, this);
        recyclerView.setAdapter(adapter);

        // Botón añadir compra compartida (FAB)
        findViewById(R.id.fab_add_lista).setOnClickListener(view -> {
            reproducirSonido();
            Intent intent = new Intent(MainActivity.this, CrearCompraActivity.class);
            startActivity(intent);
        });

        // Botón para ir a compras privadas (SQLite)
        findViewById(R.id.btn_nav_privadas).setOnClickListener(v -> {
            reproducirSonido();
            Intent intentPrivadas = new Intent(MainActivity.this, ListaPrivadaActivity.class);
            intentPrivadas.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intentPrivadas);
        });
    }

    private void configurarSonido() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(5)
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
    public void onItemClick(DataSnapshot itemSnapshot) {
        reproducirSonido();
        String listaKey = itemSnapshot.getKey();
        Compra compra = itemSnapshot.getValue(Compra.class);

        if (compra != null) {
            Intent intent = new Intent(this, ListaDetalleActivity.class);
            intent.putExtra("LISTA_KEY", listaKey);
            intent.putExtra("NOMBRE_LISTA", compra.getNombre());
            startActivity(intent);
        }
    }

    @Override
    public void onItemLongClick(DataSnapshot itemSnapshot) {
        Compra compra = itemSnapshot.getValue(Compra.class);
        String nombre = (compra != null) ? compra.getNombre() : getString(R.string.esta_lista);

        new AlertDialog.Builder(this)
                .setTitle(R.string.titulo_borrar_compartida)
                .setMessage(getString(R.string.mensaje_borrar_generico, nombre))
                .setPositiveButton(R.string.eliminar, (dialog, which) -> {
                    reproducirSonido();
                    itemSnapshot.getRef().removeValue()
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(this, R.string.eliminado_ok, Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, R.string.error_eliminar_firebase, Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton(R.string.boton_cancelar, null)
                .show();
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
    protected void onRestart() {
        super.onRestart();
        // Esto refresca la actividad al volver de Settings para aplicar cambios de idioma/tema
        recreate();
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