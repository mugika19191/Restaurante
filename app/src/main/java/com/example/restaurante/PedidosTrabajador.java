package com.example.restaurante;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Map;

public class PedidosTrabajador extends AppCompatActivity {
    ArrayList<Recibo> pedidos = new ArrayList<>();
    int indice;
    String ID;
    TextView ReciboInfo,fecha,total,estado,counter;
    ImageView izquierda,derecha;

    Button completar;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedidos_trabajador);

        ReciboInfo = findViewById(R.id.ReciboInfo);
        total = findViewById(R.id.total);
        fecha = findViewById(R.id.fecha);
        estado = findViewById(R.id.estado);
        counter = findViewById(R.id.counter);
        izquierda =findViewById(R.id.izquierda);
        derecha = findViewById(R.id.derecha);
        completar= findViewById(R.id.completo);
        indice=1;

        izquierda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (indice>1){
                    indice--;
                    ponerInfo();
                }
            }
        });
        derecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (indice<pedidos.size()){
                    indice++;
                    ponerInfo();
                }
            }
        });
        completar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String URL = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/imugica037/WEB/restaurante_php/completar_pedido.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ponerInfo();
                        cargarDatos();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PedidosTrabajador.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        //añadir elementos para realizar la consulta
                        Map<String,String> parametros= new HashMap<String,String>();
                        parametros.put("id", ID);
                        return parametros;
                    }
                };
                RequestQueue requestQue = Volley.newRequestQueue(getApplicationContext());
                requestQue.add(stringRequest);
            }
        });
        ReciboInfo.setMovementMethod(new ScrollingMovementMethod());
        cargarDatos();
    }

    private void ponerInfo() {
        if(pedidos.size()!=0){
            Recibo recibo = pedidos.get(indice-1);
            counter.setText(indice+"/"+pedidos.size());
            ReciboInfo.setText(recibo.getElementos());
            total.setText(recibo.getPrecio()+"€");
            fecha.setText(recibo.getFecha());
            estado.setText(recibo.getEstado());
            ID=recibo.getId();
        }
    }

    private void cargarDatos() {
        pedidos.clear();
        //obtener la carta
        String URL = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/imugica037/WEB/restaurante_php/get_all_pedidos.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray arr = null;
                try {
                    arr = new JSONArray(response);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                for (int i = 0; i < arr.length(); i++) {
                    try {
                        crearPedidos(arr.getJSONObject(i));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    //list.add(arr.getJSONObject(i).getString("nombre"));
                    //Carta_adapter adapter = new Carta_adapter(getApplicationContext(),comidaCarta,this);
                   /* adapter.notifyDataSetChanged();
                    carta.setAdapter(adapter);
                    carta.setLayoutManager(new LinearLayoutManager(getApplicationContext()));*/
                }
                ponerInfo();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PedidosTrabajador.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQue = Volley.newRequestQueue(this);
        requestQue.add(stringRequest);
    }

    private void crearPedidos(JSONObject jsonObject) throws JSONException {
        Recibo recibo = new Recibo(jsonObject.getString("elementos"),jsonObject.getString("precio"),
                jsonObject.getString("fecha"),jsonObject.getString("estado")) ;
        recibo.putUser(jsonObject.getString("usuario"));
        recibo.putId(jsonObject.getString("id"));
        pedidos.add(recibo);
    }
}
