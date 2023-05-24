package com.example.restaurante;

import java.util.ArrayList;

public class Recibo {

    // Declaring a variable of type String
    private String elementos,precio,fecha,estado;


    // Constructor
    // Here we will be creating private constructor
    // restricted to this class itself
    public Recibo(String eleme, String pre, String fe, String est) {
        //eraikitzailea
        this.elementos=eleme;
        this.precio=pre;
        this.fecha=fe;
        this.estado=est;
    }

    // Static method
    // Static method to create instance of Singleton class
    public String getElementos(){
        return this.elementos;
    }
    public String getPrecio(){
        return this.precio;
    }
    public String getFecha(){
        return this.fecha;
    }
    public String getEstado(){
        return this.estado;
    }
}
