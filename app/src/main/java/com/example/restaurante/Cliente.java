package com.example.restaurante;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cliente extends AppCompatActivity implements RecycleviewInterface{
    Carta_adapter adapter;
    Button pedir, logout;
    RecyclerView carta;

    ArrayList<Comida> comidaCarta= new ArrayList<>();
    ArrayList<Comida> pedido= new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cliente);

        pedir = findViewById(R.id.btnPedirCliente);
        logout = findViewById(R.id.btnLogoutCliente);
        carta = findViewById(R.id.carta);
        adapter = new Carta_adapter(this,comidaCarta,this);
        cargarCarta();

        pedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PedirCliente.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void cargarCarta(){
        String URL = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/imugica037/WEB/restaurante_php/get_carta.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray arr = null;
                try {
                    arr = new JSONArray(response);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                for(int i = 0; i < arr.length(); i++){
                    try {
                        crearComidas(arr.getJSONObject(i));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    //list.add(arr.getJSONObject(i).getString("nombre"));
                    //Carta_adapter adapter = new Carta_adapter(getApplicationContext(),comidaCarta,this);
                    adapter.notifyDataSetChanged();
                    carta.setAdapter(adapter);
                    carta.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }
                Toast.makeText(Cliente.this,"Response: " + response.toString(),Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Cliente.this,"Error: " + error.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQue= Volley.newRequestQueue(this);
        requestQue.add(stringRequest);
    }

    private void crearComidas(JSONObject elemento) throws JSONException {
        String CNombre = elemento.getString("nombre");
        String CImagen = elemento.getString("foto");
        float precio = Float.parseFloat(elemento.getString("precio"));
        //carta
        comidaCarta.add(new Comida(CNombre,CImagen,precio));
    }

    @Override
    public void onItemClick(int position) {
        /*Intent intent = new Intent(getApplicationContext(), ElementoSeleccionado.class);

        intent.putExtra("Nombre","Se va ha editar");

        startActivity(intent);*/
        //////HAY QUE PONER UN ACTIVITY PARA EDITAR EL ELEMENTO SELECCIONADO/////////
        boolean found=false;
        String nombre = comidaCarta.get(position).getNombre();
        for(int i=0;i<pedido.size() && !found;i++){
            if (pedido.get(i).getNombre().equals(nombre)){
                found = true;
            }
        }
        if (!found){
            pedido.add(comidaCarta.get(position));
            pedir.setText("PEDIR ("+pedido.size()+")");
            Toast.makeText(Cliente.this,"Se ha añadido.",Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(Cliente.this,pedido.size(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(int position) {
        Intent intent = new Intent(getApplicationContext(), ElementoSeleccionado.class);

        intent.putExtra("Nombre",comidaCarta.get(position).getNombre());
        intent.putExtra("User","nombreDeUsuario");//para mas adelante

        startActivity(intent);
        }
}
