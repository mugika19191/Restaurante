package com.example.restaurante;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DatosUsuario extends AppCompatActivity {
    TextView nombre;
    ImageView foto,editImagen;
    String encodedImage=null;
    private final int GALLERY_REQ_CODE=1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_usuario);

        nombre = findViewById(R.id.nombreUsuario);
        foto = findViewById(R.id.ImagenUsuario);
        editImagen = findViewById(R.id.editImage);


        nombre.setText(getIntent().getStringExtra("email"));
        loadImage();


        editImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
    }

    private void loadImage(){
        //obtener los datos correspondientes al elemento seleccionado
        String URL = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/imugica037/WEB/restaurante_php/get_user.php";
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
                Toast.makeText(DatosUsuario.this,"Error: " + error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //añadir elementos para realizar la consulta
                Map<String,String> parametros= new HashMap<String,String>();
                parametros.put("email",nombre.getText().toString());
                return parametros;
            }
        };
        RequestQueue requestQue= Volley.newRequestQueue(this);
        requestQue.add(stringRequest);
    }
    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // do your operation from here....
                    if (data != null
                            && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap = null;
                        try {
                            selectedImageBitmap
                                    = MediaStore.Images.Media.getBitmap(
                                    this.getContentResolver(),
                                    selectedImageUri);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        foto.setImageBitmap(
                                selectedImageBitmap);
                        guardarImagen();
                    }
                }
            });
    private void chooseImage(){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchSomeActivity.launch(i);
    }
    private void guardarImagen(){
        Bitmap bitmap = ((BitmapDrawable) foto.getDrawable()).getBitmap();

        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
        String URL = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/imugica037/WEB/restaurante_php/subir_foto_perfil.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DatosUsuario.this,"Error: " + error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //añadir elementos para realizar la consulta
                Map<String,String> parametros= new HashMap<String,String>();
                parametros.put("usuario",nombre.getText().toString());
                parametros.put("foto",encodedImage);
                return parametros;
            }
        };
        RequestQueue requestQue= Volley.newRequestQueue(this);
        requestQue.add(stringRequest);
    }

}
