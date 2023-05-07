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

    Context context;
    ArrayList<Comida> carta;

    public Carta_adapter(Context context, ArrayList<Comida> carta){
        this.carta = carta;
        this.context = context;
    }
    @NonNull
    @Override
    public Carta_adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.elemento_carta,parent,false);
        return new Carta_adapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Carta_adapter.MyViewHolder holder, int position) {
        holder.imagen.setImageBitmap(carta.get(position).getImagenBitMap());
        /*holder.imagen.setMaxHeight();
        holder.imagen.getMinimumHeight();*/
        holder.nombre.setText(carta.get(position).getNombre());
        holder.precio.setText(carta.get(position).getPrecio().toString());
    }

    @Override
    public int getItemCount() {

        return this.carta.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imagen;
        TextView nombre,precio;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imagen = itemView.findViewById(R.id.fotoCarta);
            nombre = itemView.findViewById(R.id.NombreCarta);
            precio = itemView.findViewById(R.id.precioCarta);
        }
    }
}
