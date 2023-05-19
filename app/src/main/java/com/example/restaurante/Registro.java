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
import java.util.Map;

public class Registro extends AppCompatActivity {

    Button registrar, cancelar;
    EditText email, pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);
        registrar = findViewById(R.id.btnRegistrar);
        cancelar = findViewById(R.id.btnCancelarReg);
        email = findViewById(R.id.edEmailReg);
        pass = findViewById(R.id.edPassReg);

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!email.getText().toString().isEmpty() && !pass.getText().toString().isEmpty()
                && pass.length()>=6){
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
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
    private void insertarUsuario(){
        String URL = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/imugica037/WEB/restaurante_php/insert_user.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(Registro.this,R.string.regCorrecto,Toast.LENGTH_SHORT).show();
                FirebaseAuth auth = FirebaseAuth.getInstance();
         //       DatabaseReference dataBase = auth.getReference();
                auth.createUserWithEmailAndPassword(email.getText().toString(),pass.getText().toString() )
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
           //                        DatabaseReference  dataBase= auth.
                                    FirebaseAuth auth = FirebaseAuth.getInstance();
                                    FirebaseUser user = auth.getCurrentUser();

                                    user.sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(Registro.this, "creada cuenta", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });
                                }
                            }
                        });

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
                parametros.put("pass",pass.getText().toString());
                return parametros;
            }
        };
        RequestQueue requestQue= Volley.newRequestQueue(this);
        requestQue.add(stringRequest);
    }
    private void existeUsuario(){
        String URL = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/imugica037/WEB/restaurante_php/get_user.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){    //comprueba que no haya ningún usuario con el correo insertado
                    Toast.makeText(Registro.this,"Ya existe un usuario con es correo.",Toast.LENGTH_SHORT).show();
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
