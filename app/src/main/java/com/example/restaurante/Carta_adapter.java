package com.example.restaurante;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Carta_adapter extends RecyclerView.Adapter<Carta_adapter.MyViewHolder> {

    private final RecycleviewInterface recycleviewInterface;
    Context context;
    ArrayList<Comida> carta;

    public Carta_adapter(Context context, ArrayList<Comida> carta,RecycleviewInterface recycleviewInt){
        // constructor
        this.carta = carta;
        this.context = context;
        this.recycleviewInterface=recycleviewInt;
    }
    @NonNull
    @Override
    public Carta_adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.elemento_carta,parent,false);
        return new Carta_adapter.MyViewHolder(view,recycleviewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull Carta_adapter.MyViewHolder holder, int position) {
        holder.imagen.setImageBitmap(carta.get(position).getImagenBitMap());
        holder.nombre.setText(carta.get(position).getNombre());
        String formattedString = String.format("%.02f", carta.get(position).getPrecio());
        holder.precio.setText(formattedString+"€");
    }

    @Override
    public int getItemCount() {
        //obtener el tamaño
        return this.carta.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imagen;
        TextView nombre,precio;

        public MyViewHolder(@NonNull View itemView, RecycleviewInterface recycleviewInt) {
            super(itemView);

            imagen = itemView.findViewById(R.id.fotoCarta);
            nombre = itemView.findViewById(R.id.NombreCarta);
            precio = itemView.findViewById(R.id.precioCarta);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recycleviewInt != null){
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            recycleviewInt.onItemClick(pos);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (recycleviewInt != null){
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            recycleviewInt.onItemLongClick(pos);
                        }
                    }
                    return true;
                }
            });
        }
    }
}
