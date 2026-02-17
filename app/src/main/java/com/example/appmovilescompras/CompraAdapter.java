package com.example.appmovilescompras;

import android.graphics.Color;import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;

public class CompraAdapter extends FirebaseRecyclerAdapter<Compra, CompraAdapter.ViewHolder> {

    private OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(DataSnapshot itemSnapshot);
        void onItemLongClick(DataSnapshot itemSnapshot);
    }

    public CompraAdapter(@NonNull FirebaseRecyclerOptions<Compra> options,
                         OnItemClickListener listener) {
        super(options);
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder,
                                    int position,
                                    @NonNull Compra model) {

        holder.txtNombre.setText(model.getNombre());
        holder.txtFecha.setText(model.getFecha());

        // Lógica de color (Sustituye a Picasso)
        String valorColor = model.getUrlImagen();
        if (valorColor != null && valorColor.startsWith("#")) {
            holder.imgCompra.setImageDrawable(null);
            holder.imgCompra.setBackgroundColor(Color.parseColor(valorColor));
        } else {
            holder.imgCompra.setBackgroundColor(Color.LTGRAY);
        }

        // Clic normal para abrir la lista y ver sus productos y eso
        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && listener != null) {
                DataSnapshot snapshot = getSnapshots().getSnapshot(pos);
                listener.onItemClick(snapshot);
            }
        });

        // El clic largo para poder eliminar listas
        holder.itemView.setOnLongClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && listener != null) {
                DataSnapshot snapshot = getSnapshots().getSnapshot(pos);
                listener.onItemLongClick(snapshot);
            }
            return true;
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lista, parent, false);
        return new ViewHolder(view);
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

