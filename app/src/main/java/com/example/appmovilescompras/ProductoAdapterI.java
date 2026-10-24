package com.example.appmovilescompras;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
        Context context = holder.itemView.getContext();

        // 1. Nombre del producto
        holder.txtNombre.setText(producto.getNombre());

        // 2. Cantidad adaptada a Idiomas (Responsive)
        holder.txtCantidad.setText(context.getString(R.string.cantidad_con_numero, producto.getCantidad()));

        // 3. Botón Borrar con Diálogo de Confirmación (Multi-idioma)
        holder.btnBorrar.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.eliminar_producto)
                    .setMessage(context.getString(R.string.confirmar_eliminar_prod, producto.getNombre()))
                    .setPositiveButton(R.string.si, (dialog, which) -> {
                        // Solo si el usuario confirma, llamamos al listener
                        if (listener != null) {
                            listener.onItemDelete(producto, holder.getBindingAdapterPosition());
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
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