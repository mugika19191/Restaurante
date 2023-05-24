package com.example.restaurante;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {

    Button inicio_sesion, registrar, invitado;
    EditText pass, email;
    Spinner idiomas;
    TextView tit1,tit2, forget;
    String idioma;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inicio_sesion = findViewById(R.id.btnLogin);
        registrar = findViewById(R.id.btnRegistro);
        invitado = findViewById(R.id.btnInvitado);
        pass = findViewById(R.id.edPass);
        email = findViewById(R.id.edEmailRec);
        idiomas= findViewById(R.id.spinner);
        tit1=findViewById(R.id.Texto1);
        tit2=findViewById(R.id.Texto2);
        forget=findViewById(R.id.TextRecupContr);
        idioma=Locale.getDefault().getDisplayLanguage();
        auth=FirebaseAuth.getInstance();

        inicio_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pass.getText().toString().isEmpty() && !email.getText().toString().isEmpty()){
                    iniciarSesion();
                }else{
                    Toast.makeText(MainActivity.this,R.string.vacio,Toast.LENGTH_SHORT).show();
                }
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Registro.class);
                intent.putExtra("language",idioma);
                startActivity(intent);
            }
        });

        invitado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Invitado.class);
                startActivity(intent);
            }
        });

        idiomas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {   //si ha escogido un idioma
                    idioma = adapterView.getItemAtPosition(i).toString();  //obtiene el valor escogido
                    cambiarIdioma();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,idioma,Toast.LENGTH_LONG).show();
                //Llamar a alerta personalizada, donde se introduce el correo
                AlertDialog.Builder al = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater in = getLayoutInflater();
                View vi = in.inflate(R.layout.recup_cont, null);
                al.setView(vi);

                Button recBut= (Button) vi.findViewById(R.id.btnRec);
                Button cancelarBut= (Button) vi.findViewById(R.id.btnCancelRec);

                AlertDialog dialogo= al.create();
                dialogo.show();
                recBut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText email=vi.findViewById(R.id.edEmailRec);
                        String email1=email.getText().toString().trim();
                        if (!email1.isEmpty()){
                            mandarContraseña(email.getText().toString().trim());
                        }
                        dialogo.cancel();
                    }
                });

                cancelarBut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogo.cancel();
                    }
                });


            }
        });
    }

    private void iniciarSesion(){
        FirebaseUser user = auth.getCurrentUser();
        auth.signInWithEmailAndPassword(email.getText().toString(),pass.getText().toString() )
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (user.isEmailVerified()){    //al tener el e-mail verificado puede iniciar sesión
                                Intent intent = new Intent(getApplicationContext(), Cliente.class);
                                intent.putExtra("email",email.getText().toString().trim());
                                startActivity(intent);
                            }else{
                                Toast.makeText(MainActivity.this, R.string.verif, Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(MainActivity.this, R.string.inicioErr, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void cambiarIdioma(){
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
        //actualiza todos los datos al idioma seleccionado
        inicio_sesion.setText(R.string.inicioBut);
        registrar.setText(R.string.registroBut);
        invitado.setText(R.string.invitadoBut);
        pass.setHint(R.string.contraseña);
        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(this, R.array.idiomas ,
                android.R.layout.simple_spinner_item);
        idiomas.setAdapter(adapter);
        tit1.setText(R.string.inicioTit);
        tit2.setText(R.string.sesionTit);
        forget.setText(R.string.recuperar);
    }

    private void mandarContraseña(String email){
        // recuperar la contraseña: se manda un correo al usuario con un link en el que puede modificarlo
        String URL = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/imugica037/WEB/restaurante_php/get_user.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){    //comprueba si existe un usuario con el correo adjuntado a la consulta
                    auth.setLanguageCode(idioma);
                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(MainActivity.this, R.string.correo,Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Error: " + error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Mete los parametros para el php
                Map<String,String> parametros= new HashMap<String,String>();
                parametros.put("email",email);
                return parametros;
            }
        };
        RequestQueue requestQue= Volley.newRequestQueue(this);
        requestQue.add(stringRequest);
    }

}