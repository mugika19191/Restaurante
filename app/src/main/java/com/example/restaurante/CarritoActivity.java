package com.example.restaurante;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CarritoActivity extends AppCompatActivity {

    TextView carroInfo,precioTotal;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carro);

        carroInfo=findViewById(R.id.elementos);
        precioTotal=findViewById(R.id.precio_total);
        cargarDatos();
    }

    private void cargarDatos() {
        Carrito carrito = Carrito.getInstance();
        String elementos="";
        for (int i=0;i<carrito.getCarro().size();i++){
            elementos+=carrito.getCarro().get(i).getNombre();
            elementos+="\n";
        }
        carroInfo.setText(elementos);
        String formattedString = String.format("%.02f", carrito.getFullPrice());
        precioTotal.setText(formattedString);
    }


}
