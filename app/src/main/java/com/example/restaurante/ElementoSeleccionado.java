package com.example.restaurante;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class ElementoSeleccionado extends AppCompatActivity {
    TextView nombre;
    ImageView foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_elemento);

        nombre = findViewById(R.id.NombreElemento);
        //foto = findViewById(R.id.ImagenElemento);

        nombre.setText(getIntent().getStringExtra("Nombre"));


        /*byte[] decodedString = Base64.decode(getIntent().getStringExtra("Foto"), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        foto.setImageBitmap(decodedByte);*/
    }
}
