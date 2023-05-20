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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class ElementoSeleccionado extends AppCompatActivity {
    TextView nombre,desc;
    ImageView foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_elemento);

        nombre = findViewById(R.id.NombreElemento);
        foto = findViewById(R.id.ImagenElemento);
        desc = findViewById(R.id.DescElemento);
        nombre.setText(getIntent().getStringExtra("Nombre"));

        loadImage();
        /*
        foto.setImageBitmap(decodedByte);*/
    }
    private void loadImage(){
        //obtener los datos correspondientes al elemento seleccionado
        String URL = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/imugica037/WEB/restaurante_php/get_element.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()) {
                    JSONObject obj;
                    try {
                        obj = new JSONObject(response);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    byte[] decodedString = new byte[0];//String-->Image
                    try {
                    desc.setText(obj.getString("ingre"));
                        decodedString = Base64.decode(obj.getString("foto"), Base64.DEFAULT);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    foto.setImageBitmap(decodedByte);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ElementoSeleccionado.this,"Error: " + error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //añadir elementos para realizar la consulta
                Map<String,String> parametros= new HashMap<String,String>();
                parametros.put("nombre",nombre.getText().toString());
                return parametros;
            }
        };
        RequestQueue requestQue= Volley.newRequestQueue(this);
        requestQue.add(stringRequest);
    }
}
