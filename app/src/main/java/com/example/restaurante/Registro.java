package com.example.restaurante;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Registro extends AppCompatActivity {

    Button registrar, cancelar;
    EditText email, pass;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);
        registrar = findViewById(R.id.btnRegistrar);
        cancelar = findViewById(R.id.btnCancelarReg);
        email = findViewById(R.id.edEmailReg);
        pass = findViewById(R.id.edPassReg);
        auth= FirebaseAuth.getInstance();

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!email.getText().toString().isEmpty() && !pass.getText().toString().isEmpty()
                && pass.length()>=6){   //la contraseña debe tener al menos 6 caracteres
                    existeUsuario();
                }else{
                    if (pass.length()<6){
                        Toast.makeText(Registro.this,R.string.contr6,Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Registro.this,R.string.vacio,Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            //retrocede al MainActivity
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void mandarVerificacion(){

        FirebaseUser user = auth.getCurrentUser();
        //el usuario es registrado pero se le manda un correo de verificación, en el que debe aceptar sino no `podra iniciar sesión
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //email
                            Toast.makeText(Registro.this, R.string.correo, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Registro.this, "Error", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        auth.setLanguageCode(getIntent().getStringExtra("language"));
    }
    private void insertarUsuario(){
        auth.createUserWithEmailAndPassword(email.getText().toString(),pass.getText().toString() )
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //añade el email de usuario en phpAdmin y en Firebase el email y contraseña
                        String URL = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/imugica037/WEB/restaurante_php/insert_user.php";
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(Registro.this,R.string.regCorrecto,Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(Registro.this,"Error: " + error.toString(),Toast.LENGTH_SHORT).show();
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String,String> parametros= new HashMap<String,String>();
                                parametros.put("email",email.getText().toString());
                                parametros.put("nombre","");
                                parametros.put("apellido","");
                                return parametros;
                            }
                        };
                        RequestQueue requestQue= Volley.newRequestQueue(Registro.this);
                        requestQue.add(stringRequest);
                        mandarVerificacion();
                    }
                }
            });

    }


    private void existeUsuario(){
        //comprueba si existe ya un usuario con ese email
        String URL = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/imugica037/WEB/restaurante_php/get_user.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){    //comprueba que no haya ningún usuario con el correo insertado
                    Toast.makeText(Registro.this,R.string.usuRep,Toast.LENGTH_SHORT).show();
                }else{
                    insertarUsuario();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Registro.this,"Error: " + error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // inserta los parametros necesario para realizar la consulta
                Map<String,String> parametros= new HashMap<String,String>();
                parametros.put("email",email.getText().toString());
                return parametros;
            }
        };
        RequestQueue requestQue= Volley.newRequestQueue(this);
        requestQue.add(stringRequest);
    }
}
