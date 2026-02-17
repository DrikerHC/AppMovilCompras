package com.example.appmovilescompras;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductoAdapterI extends RecyclerView.Adapter<ProductoAdapterI.ProductoViewHolder> {

    private List<Producto> productos;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemDelete(Producto producto, int position);
    }

    public ProductoAdapterI(List<Producto> productos, OnItemClickListener listener) {
        this.productos = productos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = productos.get(position);
        holder.txtNombre.setText(producto.getNombre());
        holder.txtCantidad.setText("Cantidad: " + producto.getCantidad());

        holder.btnBorrar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemDelete(producto, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }
    
    public void setProductos(List<Producto> lista) {
        productos = lista;
        notifyDataSetChanged();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtCantidad;
        ImageButton btnBorrar;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.tv_nombre_prod);
            txtCantidad = itemView.findViewById(R.id.tv_cantidad_prod);
            btnBorrar = itemView.findViewById(R.id.btn_eliminar_prod);
        }
    }
}
