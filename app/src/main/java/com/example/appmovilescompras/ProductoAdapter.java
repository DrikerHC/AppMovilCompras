package com.example.appmovilescompras;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ProductoAdapter extends FirebaseRecyclerAdapter<Producto, ProductoAdapter.ProductoViewHolder> {

    public ProductoAdapter(@NonNull FirebaseRecyclerOptions<Producto> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductoViewHolder holder, int position, @NonNull Producto model) {
        Context context = holder.itemView.getContext();

        // 1. NOMBRE DEL PRODUCTO
        holder.txtNombre.setText(model.getNombre());

        // 2. CANTIDAD (ADAPTADO A IDIOMAS)
        // Usamos el string con formato para que cambie según el idioma del móvil
        holder.txtCantidad.setText(context.getString(R.string.cantidad_con_numero, model.getCantidad()));

        // 3. BOTÓN ELIMINAR CON DIÁLOGO DE CONFIRMACIÓN
        holder.btnBorrar.setOnClickListener(v -> {
            // Creamos un AlertDialog para confirmar (Requisito: Diálogos del sistema)
            new AlertDialog.Builder(context)
                    .setTitle(R.string.eliminar_producto)
                    .setMessage(context.getString(R.string.confirmar_eliminar_prod, model.getNombre()))
                    .setPositiveButton(R.string.si, (dialog, which) -> {
                        int currentPosition = holder.getBindingAdapterPosition();
                        if (currentPosition != RecyclerView.NO_POSITION) {
                            // Borrar de Firebase
                            getSnapshots().getSnapshot(currentPosition).getRef().removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        // Opcional: Toast de éxito traducido
                                        Toast.makeText(context, R.string.eliminado_ok, Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        });
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtCantidad;
        View btnBorrar;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.tv_nombre_prod);
            txtCantidad = itemView.findViewById(R.id.tv_cantidad_prod);
            btnBorrar = itemView.findViewById(R.id.btn_eliminar_prod);
        }
    }
}