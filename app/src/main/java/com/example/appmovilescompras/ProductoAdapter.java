package com.example.appmovilescompras;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton; // Cambiado para que coincida con la papelera
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ProductoAdapter extends FirebaseRecyclerAdapter<Producto, ProductoAdapter.ProductoViewHolder> {

    public ProductoAdapter(@NonNull FirebaseRecyclerOptions<Producto> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductoViewHolder holder, int position, @NonNull Producto model) {
        holder.txtNombre.setText(model.getNombre());
        holder.txtCantidad.setText("Cantidad: " + model.getCantidad());

        //Esto es el boton de eliminar
        holder.btnBorrar.setOnClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                getSnapshots().getSnapshot(currentPosition).getRef().removeValue();
            }
        });
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // cogemos el diseño con el inflater
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