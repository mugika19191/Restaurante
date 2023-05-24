package com.example.restaurante;

import java.util.ArrayList;

public class Carrito {

    private static Carrito single_instance = null;

    // Declaring a variable of type String
    private ArrayList<Comida> carrito;

    // Constructor
    // Here we will be creating private constructor
    // restricted to this class itself
    private Carrito()
    {
        carrito = new ArrayList<Comida>();
    }

    // Static method
    // Static method to create instance of Singleton class
    public static synchronized Carrito getInstance()
    {
        if (single_instance == null)
            single_instance = new Carrito();

        return single_instance;
    }
    public ArrayList<Comida> getCarro(){
        return this.carrito;
    }
    public double getFullPrice(){
        //suma todos los precios para obtener el precio total
        double count=0.0;
        for(int i=0;i<this.carrito.size();i++){
            count+=this.carrito.get(i).precio;
        }
        return count;
    }
}
