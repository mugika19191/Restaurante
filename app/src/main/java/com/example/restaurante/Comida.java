package com.example.restaurante;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Comida {
    String nombre;
    String imagen;
    double precio;

    public Comida(String nombre, String imagen, double precio) {
        this.nombre = nombre;
        this.imagen = imagen;
        this.precio = precio;
    }
    public String getNombre(){
        return this.nombre;
    }
    public String getImagen(){
        return this.imagen;
    }
    public Double getPrecio(){
        return this.precio;
    }
    public Bitmap getImagenBitMap(){
        byte[] decodedString = Base64.decode(this.imagen, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
}
