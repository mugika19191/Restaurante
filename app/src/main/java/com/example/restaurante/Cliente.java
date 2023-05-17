package com.example.restaurante;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Cliente extends AppCompatActivity implements RecycleviewInterface,NavigationView.OnNavigationItemSelectedListener{
    Carta_adapter adapter;
    Button pedir, logout;
    RecyclerView carta;
    ArrayList<Comida> comidaCarta, pedido;
    Spinner idiomas;
    DrawerLayout drawerLayout;

    NavigationView navigationView;
    ImageView iconoMen;
    TextView tit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cliente);

        comidaCarta= new ArrayList<>();
        pedido= new ArrayList<>();

        pedir = findViewById(R.id.btnPedirCliente);
        pedir.setText(getString(R.string.carro) +"("+pedido.size()+")");
        logout = findViewById(R.id.btnLogoutCliente);
        carta = findViewById(R.id.carta);
        iconoMen=findViewById(R.id.iconoMenu);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        adapter = new Carta_adapter(this,comidaCarta,this);
        cargarCarta();
        idiomas= findViewById(R.id.spinner2);
        tit= findViewById(R.id.tvTitCliente);

        pedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PedirCliente.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        iconoMen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        idiomas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {   //si ha escogido un idioma
                    String idioma = adapterView.getItemAtPosition(i).toString();  //obtiene el valor escogido
                    cambiarIdioma(idioma);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void cargarCarta(){
        //obtener la carta
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
        // crea los elementos (comida) y los adjunta en un ArrayList
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

        //actividad que realizará la carta seleccionada
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
        //pasar valores a ElementoSeleccionado
        intent.putExtra("Nombre",comidaCarta.get(position).getNombre());
        intent.putExtra("User","nombreDeUsuario");//para más adelante
        startActivity(intent);
    }

    private void cambiarIdioma(String idioma){
        Locale nuevaloc = new Locale(idioma);
        Locale.setDefault(nuevaloc);
        Resources resources= getBaseContext().getResources();
        Configuration configuration =resources.getConfiguration();
        configuration.setLocale(nuevaloc);
        configuration.setLayoutDirection(nuevaloc);
        Context context = getBaseContext().createConfigurationContext(configuration);
        resources.updateConfiguration(configuration, context.getResources().getDisplayMetrics());
        actualizar();
    }

    private void actualizar(){
        pedir.setText(getString(R.string.carro)+"("+pedido.size()+")");
        logout.setText(R.string.salir);
        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(this, R.array.idiomas ,
                android.R.layout.simple_spinner_item);
        idiomas.setAdapter(adapter);
        tit.setText(R.string.cartaTit);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.logOut:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        return true;
    }
}
