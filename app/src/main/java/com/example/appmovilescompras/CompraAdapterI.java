package com.example.appmovilescompras;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CompraAdapterI extends RecyclerView.Adapter<CompraAdapterI.ViewHolder> {

    private List<Compra> lista;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Compra compra);
        void onItemLongClick(Compra compra); // Para eliminar
    }

    public CompraAdapterI(List<Compra> lista, OnItemClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Compra c = lista.get(position);
        holder.txtNombre.setText(c.getNombre());
        holder.txtFecha.setText(c.getFecha());

        // Coger la imagen desde el sqlite
        String pathImagen = c.getUrlImagen();

        if (pathImagen != null && !pathImagen.isEmpty()) {
            Picasso.get()
                    .load(Uri.parse(pathImagen)) // Convertimos el String a URI
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.stat_notify_error)
                    .into(holder.imgCompra);
        } else {
            // Si no hay imagen, pongo este icono por defecto
            holder.imgCompra.setImageResource(android.R.drawable.ic_menu_gallery);
        }


        holder.itemView.setOnClickListener(v -> listener.onItemClick(c));

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(c);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtFecha;
        ImageView imgCompra;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txt_nombre_lista);
            txtFecha = itemView.findViewById(R.id.txt_fecha_lista);
            imgCompra = itemView.findViewById(R.id.img_lista);
        }
    }
}