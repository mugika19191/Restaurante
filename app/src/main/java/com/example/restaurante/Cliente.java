package com.example.restaurante;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Base64;
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
import androidx.core.app.ActivityCompat;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.gms.location.LocationListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Cliente extends AppCompatActivity implements RecycleviewInterface, NavigationView.OnNavigationItemSelectedListener {
    Carta_adapter adapter;
    Button logout;
    RecyclerView carta;
    ArrayList<Comida> comidaCarta;
    ArrayList<String> carroTemp;
    Carrito pedido;
    Spinner idiomas;
    DrawerLayout drawerLayout;
    View headerView;
    NavigationView navigationView;
    ImageView iconoMen,pedir;
    TextView tit,count;

    FusedLocationProviderClient fusedLocationProviderClient;

    LatLng userPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cliente);

        comidaCarta = new ArrayList<>();
        carroTemp = new ArrayList<>();
        pedido = Carrito.getInstance();

        pedir = findViewById(R.id.iconoCarro);
        count = findViewById(R.id.count);
        count.setText(""+pedido.getCarro().size());
        carta = findViewById(R.id.carta);
        iconoMen = findViewById(R.id.iconoMenu);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
        adapter = new Carta_adapter(this, comidaCarta, this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        cargarCarta();
        tit = findViewById(R.id.tvTitCliente);
        loadMenuData();
        pedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CarritoActivity.class);
                intent.putExtra("email", getIntent().getStringExtra("email"));
                startActivity(intent);
            }
        });

        iconoMen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        /*idiomas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        });*/

        navigationView.setNavigationItemSelectedListener(this);
        if (ActivityCompat.checkSelfPermission(Cliente.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        } else {
            ActivityCompat.requestPermissions(Cliente.this
                    , new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        loadCarrito();
    }

    @Override
    public void onResume() {
        super.onResume();
        //here...
        loadMenuData();
        count.setText(""+pedido.getCarro().size());
    }

    private void cargarCarta() {
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
                for (int i = 0; i < arr.length(); i++) {
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
                Toast.makeText(Cliente.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQue = Volley.newRequestQueue(this);
        requestQue.add(stringRequest);
    }

    private void crearComidas(JSONObject elemento) throws JSONException {
        // crea los elementos (comida) y los adjunta en un ArrayList
        String CNombre = elemento.getString("nombre");
        String CImagen = elemento.getString("foto");
        float precio = Float.parseFloat(elemento.getString("precio"));
        //carta
        comidaCarta.add(new Comida(CNombre, CImagen, precio));
    }
    private void crearComidasCarro(String elemento) throws JSONException {
        // crea los elementos (comida) y los adjunta en un ArrayList

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
                    try {
                        float precio = Float.parseFloat(obj.getString("precio"));
                        Carrito.getInstance().getCarro().add(new Comida( obj.getString("nombre"),obj.getString("foto"),precio));
                        count.setText(""+Carrito.getInstance().getCarro().size());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Cliente.this,"Error: " + error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //añadir elementos para realizar la consulta
                Map<String,String> parametros= new HashMap<String,String>();
                parametros.put("nombre",elemento);
                return parametros;
            }
        };
        RequestQueue requestQue= Volley.newRequestQueue(this);
        requestQue.add(stringRequest);

    }
    private void loadCarrito(){
        String URL = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/imugica037/WEB/restaurante_php/get_carro.php";
        Carrito.getInstance().getCarro().clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()){
                    JSONArray arr = null;
                    try {
                        arr = new JSONArray(response);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    for (int i = 0; i < arr.length(); i++) {
                        try {
                            carroTemp.add(arr.getJSONObject(i).getString("elemento"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        //list.add(arr.getJSONObject(i).getString("nombre"));
                        //Carta_adapter adapter = new Carta_adapter(getApplicationContext(),comidaCarta,this);
                    }
                    for (int i=0;i<carroTemp.size();i++){
                        try {
                            crearComidasCarro(carroTemp.get(i));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Cliente.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //añadir elementos para realizar la consulta
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("usuario",getIntent().getStringExtra("email") );
                return parametros;
            }
        };
        RequestQueue requestQue = Volley.newRequestQueue(this);
        requestQue.add(stringRequest);
    }
    @Override
    public void onItemClick(int position) {
        /*Intent intent = new Intent(getApplicationContext(), ElementoSeleccionado.class);

        intent.putExtra("Nombre","Se va ha editar");

        startActivity(intent);*/
        //////HAY QUE PONER UN ACTIVITY PARA EDITAR EL ELEMENTO SELECCIONADO/////////

        //actividad que realizará la carta seleccionada

            pedido.getCarro().add(comidaCarta.get(position));
            count.setText(""+pedido.getCarro().size());
            Toast.makeText(Cliente.this, "Se ha añadido.", Toast.LENGTH_SHORT).show();

        String URL = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/imugica037/WEB/restaurante_php/subir_elemento_carro.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Cliente.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //añadir elementos para realizar la consulta
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("usuario",getIntent().getStringExtra("email") );
                parametros.put("elemento",comidaCarta.get(position).getNombre());
                return parametros;
            }
        };
        RequestQueue requestQue = Volley.newRequestQueue(this);
        requestQue.add(stringRequest);


        //Toast.makeText(Cliente.this,pedido.size(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(int position) {
        Intent intent = new Intent(getApplicationContext(), ElementoSeleccionado.class);
        //pasar valores a ElementoSeleccionado
        intent.putExtra("Nombre", comidaCarta.get(position).getNombre());
        intent.putExtra("User", "nombreDeUsuario");//para más adelante
        startActivity(intent);
    }

    private void cambiarIdioma(String idioma) {
        Locale nuevaloc = new Locale(idioma);
        Locale.setDefault(nuevaloc);
        Resources resources = getBaseContext().getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(nuevaloc);
        configuration.setLayoutDirection(nuevaloc);
        Context context = getBaseContext().createConfigurationContext(configuration);
        resources.updateConfiguration(configuration, context.getResources().getDisplayMetrics());
        actualizar();
    }

    private void actualizar() {
        count.setText( ""+pedido.getCarro().size());
        logout.setText(R.string.salir);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.idiomas,
                android.R.layout.simple_spinner_item);
        idiomas.setAdapter(adapter);
        tit.setText(R.string.cartaTit);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.logOut:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;
            case R.id.menuProfile:
                intent = new Intent(getApplicationContext(), DatosUsuario.class);
                intent.putExtra("email", getIntent().getStringExtra("email"));
                startActivity(intent);
                break;
            case R.id.mapa:
                intent = new Intent(getApplicationContext(), Mapa.class);
                intent.putExtra("pos", new String(userPos.latitude+" ,"+userPos.longitude));
                startActivity(intent);
                break;
            case R.id.pedidos:
                intent = new Intent(getApplicationContext(), Pedidos.class);
                intent.putExtra("email", getIntent().getStringExtra("email"));
                startActivity(intent);
                break;
        }
        return true;
    }

    private void loadMenuData() {
        headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.nombreUsuario);
        navUsername.setText(getIntent().getStringExtra("email"));
        ImageView imageView = (ImageView) headerView.findViewById(R.id.fotoPerfil);

        String URL = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/imugica037/WEB/restaurante_php/get_user.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()) {
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
                    imageView.setImageBitmap(decodedByte);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Cliente.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //añadir elementos para realizar la consulta
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("email", navUsername.getText().toString());
                return parametros;
            }
        };
        RequestQueue requestQue = Volley.newRequestQueue(this);
        requestQue.add(stringRequest);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
       LocationManager locationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE
        );
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                ||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @SuppressLint("MissingPermission")
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location!=null){
                        userPos = new LatLng(location.getLatitude(),location.getLongitude());
                    }
                    else{
                        com.google.android.gms.location.LocationRequest locationRequest = new com.google.android.gms.location.LocationRequest().
                                setPriority(LocationRequest.QUALITY_HIGH_ACCURACY)
                                .setInterval(1000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);
                        LocationCallback locationCallback = new LocationCallback(){
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                userPos = new LatLng(locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude());
                            }
                        };
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,Looper.myLooper());
                    }
                }
            });
        }else{
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String [] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100 && grantResults.length> 0 && (grantResults[0] + grantResults[1]
                == PackageManager.PERMISSION_GRANTED)){
            getLastLocation();
        }else {

        }
    }
}
