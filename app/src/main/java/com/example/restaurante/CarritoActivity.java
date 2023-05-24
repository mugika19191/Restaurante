package com.example.restaurante;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CarritoActivity extends AppCompatActivity implements RecycleviewInterface {

    //TextView carroInfo,precioTotal;
    Carta_adapter adapter;
    RecyclerView carta;

    TextView total;
    Button eliminar,pedir;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carro);

        carta = findViewById(R.id.carta);
        total = findViewById(R.id.totalCost);
        eliminar = findViewById(R.id.eliminarCarro);
        pedir = findViewById(R.id.btnPedirCliente);

        //carroInfo=findViewById(R.id.carta);
        //precioTotal=findViewById(R.id.precio_total);
        adapter = new Carta_adapter(this,Carrito.getInstance().getCarro(),this);
        carta.setAdapter(adapter);
        carta.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        setTotal();
        //cargarDatos();


        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAll();
            }
        });
        pedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Carrito.getInstance().getCarro().size()!=0){
                    cargarDatos();
                }
            }
        });

    }


    private void cargarDatos() {
        Carrito carrito = Carrito.getInstance();
        String elementos="";
        for (int i=0;i<carrito.getCarro().size();i++){
            elementos+=carrito.getCarro().get(i).getNombre();
            elementos+="\n";
        }
        String formattedString = String.format("%.02f", carrito.getFullPrice());


        String URL = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/imugica037/WEB/restaurante_php/insert_pedido.php";
        String finalElementos = elementos;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(CarritoActivity.this,"Pedido enviado",Toast.LENGTH_SHORT).show();
                removeAll();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CarritoActivity.this,"Error: " + error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //añadir elementos para realizar la consulta
                Map<String,String> parametros= new HashMap<String,String>();
                parametros.put("usuario",getIntent().getStringExtra("email"));
                parametros.put("elementos", finalElementos);
                parametros.put("precio",formattedString);
                parametros.put("fecha",java.time.LocalDate.now().toString());
                parametros.put("estado","Enviado");
                return parametros;
            }
        };
        RequestQueue requestQue= Volley.newRequestQueue(this);
        requestQue.add(stringRequest);
    }


    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getApplicationContext(), ElementoSeleccionado.class);
        //pasar valores a ElementoSeleccionado
        intent.putExtra("Nombre", Carrito.getInstance().getCarro().get(position).getNombre());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int position) {
        Carrito.getInstance().getCarro().remove(position);
        setTotal();
        adapter.notifyDataSetChanged();

    }
    private void setTotal(){
        String formattedString = String.format("%.02f", Carrito.getInstance().getFullPrice());
        total.setText("TOTAL: "+formattedString+"€");
    }


    private void removeAll(){
        while(Carrito.getInstance().getCarro().size()>0){
            Carrito.getInstance().getCarro().remove(Carrito.getInstance().getCarro().size()-1);
        }
        adapter.notifyDataSetChanged();
        setTotal();
    }
}
