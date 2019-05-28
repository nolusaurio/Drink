package www.nolusaurio.club.drinkapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class VIstaLic extends AppCompatActivity {

    private TextView nombre, descripcion, ubicacion, promo, apertura, tel, comentario;
    private String ubicGPS, nombreLicoreria;
    private ImageView im;
    private Button ir, consulta;

    private FusedLocationProviderClient client;
    LocationRequest locationRequest;

    final DialogFragment loadingScreen = LoadingScreen.getInstance();


    Bitmap bm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_lic);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        nombre = (TextView) findViewById(R.id.nombreLico);
        descripcion = (TextView) findViewById(R.id.descripcionLico);
        ubicacion = (TextView) findViewById(R.id.ubiLic);
        promo = (TextView) findViewById(R.id.promociones);
        apertura = (TextView) findViewById(R.id.onoff);
        comentario = (TextView)findViewById(R.id.comentarios);
        tel = (TextView) findViewById(R.id.telefono);
        im = (ImageView) findViewById(R.id.imagen);
        ir = (Button) findViewById(R.id.irA);
        consulta = (Button) findViewById(R.id.consultaduenio);

        Bundle bundle = getIntent().getExtras();
        client = LocationServices.getFusedLocationProviderClient(this);


        if (bundle != null) {
            nombreLicoreria = bundle.getString("nombre").trim();
            cargarComentarios(nombreLicoreria); //cargar comentarios en pantalla principal
            String d = bundle.getString("descripcion");
            String u = bundle.getString("ubicacion");
            String p = bundle.getString("promo");
            int t = bundle.getInt("telefono");
            ubicGPS = bundle.getString("ubiGPS");
            String state = bundle.getString("estado");
            byte[] bytes = bundle.getByteArray("img");
            String pp = bundle.getString("posicion");


            Animation animation = new AlphaAnimation(0.0f, 1.0f);
            animation.setDuration(50);
            animation.setStartOffset(300);
            animation.setRepeatMode(Animation.REVERSE);
            animation.setRepeatCount(Animation.INFINITE);
            if(state.equals("1")){
                apertura.setText("Abierto");
            } else {
                apertura.setText("Cerrado");
            }

            apertura.startAnimation(animation);





            if(bytes==null){
                String URL = getString(R.string.URL);
                String URL_IMAGEN = URL+"/";
                URL_IMAGEN = URL_IMAGEN + pp;

                ImageRequest imageRequest = new ImageRequest(URL_IMAGEN, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        im.setImageBitmap(response);
                        ir.setOnClickListener(v -> {
                            Intent i = new Intent(VIstaLic.this, MapaLicoreria.class);
                            ByteArrayOutputStream bs = new ByteArrayOutputStream();
                            response.compress(Bitmap.CompressFormat.JPEG, 100, bs);
                            byte[] bytesArray = bs.toByteArray();

                            i.putExtra("gps", ubicGPS);
                            i.putExtra("imagen", bytesArray);
                            startActivity(i);

                            Animatoo.animateSplit(VIstaLic.this);

                        });
                    }
                }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        Toast.makeText(getApplicationContext(), "ERROR AL CARGAR", Toast.LENGTH_SHORT).show();
                    }
                });
                SingletonVolley.getInstanciaVolley(getApplicationContext()).addToRequestQueue(imageRequest);
            } else {

                bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                im.setImageBitmap(bm);
                ir.setOnClickListener(v -> {
                    Intent i = new Intent(VIstaLic.this, MapaLicoreria.class);
                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, bs);
                    byte[] bytesArray = bs.toByteArray();

                    i.putExtra("gps", ubicGPS);
                    i.putExtra("imagen", bytesArray);
                    startActivity(i);

                    Animatoo.animateSplit(VIstaLic.this);

                });

            }



            nombre.setText(nombreLicoreria);
            descripcion.setText(d);
            ubicacion.setText(u);
            if(p.isEmpty() || p.equals("") || p.equals(null) || p.equals("null")){
                promo.setText("Sin promociones");
            } else {
                promo.setText(p);
            }
            tel.setText(t + "");



            consulta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater layoutInflater = LayoutInflater.from(VIstaLic.this);
                    View promptView = layoutInflater.inflate(R.layout.dialogomensajelicoreria, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VIstaLic.this);
                    alertDialogBuilder.setView(promptView);

                    EditText editText = (EditText) promptView.findViewById(R.id.consulta);
                    EditText nom = (EditText) promptView.findViewById(R.id.nombreConsul);
                    alertDialogBuilder.setCancelable(false)
                            .setPositiveButton("Enviar", (dialog, id) -> {


                                String nombre = moderar(nom.getText().toString().trim());
                                String comentario = moderar(editText.getText().toString().trim());


                                aniadirComentario(nombre, comentario);
                            })
                            .setNegativeButton("Cancelar",
                                    (dialog, id) -> dialog.cancel());

                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();
                }
            });

        }

    }


    private void aniadirComentario(String nom, String com){
        String coment = comentario.getText().toString();
        String nombr = getString(R.string.nombreCon);
        String dudas = getString(R.string.conCon);
        coment = coment+nombr+nom+"\n"+dudas+com+"\n\n";
        comentario.setText(coment);

        String URL = getString(R.string.URL);
        String envio = "";
        if(nombreLicoreria.equals(null)||nombreLicoreria.equals("")){
            String temporal = nombre.getText().toString().trim();
            envio = URL + "/insComent.php?nombreLic="+temporal+"&nombreCons="+nom+"&consCons="+com;
        } else {
            envio = URL + "/insComent.php?nombreLic="+nombreLicoreria+"&nombreCons="+nom+"&consCons="+com;
        }

        envio = envio.replaceAll(" ","%20");

        loadingScreen.show(getSupportFragmentManager(), "Espere...");



        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, envio, null, response -> {
            try {
                String resulJSON = response.getString("estado");
                if(resulJSON.equals("1")){
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.custom_toast_registroexitoso,
                            (ViewGroup) findViewById(R.id.custom_toast_registroexitoso));

                    TextView men = (TextView) layout.findViewById(R.id.mensaje);
                    men.setText("Duda/sugerencia enviada.");
                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                } else if(resulJSON.equals("2")){
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.custom_toast_error,
                            (ViewGroup) findViewById(R.id.custom_toast_error));

                    TextView men = (TextView) layout.findViewById(R.id.mensaje);
                    men.setText("Error de envío, vuelva a intentar.");
                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                } else if(resulJSON.equals("3")){
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.custom_toast_error,
                            (ViewGroup) findViewById(R.id.custom_toast_error));

                    TextView men = (TextView) layout.findViewById(R.id.mensaje);
                    men.setText("Error de red, vuelva a intentar.");
                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                }

                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast_registroexitoso,
                        (ViewGroup) findViewById(R.id.custom_toast_registroexitoso));

                TextView men = (TextView) layout.findViewById(R.id.mensaje);
                men.setText("Las consultas/respuestas se borraran a las cero (0) horas de inicio de día.");
                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();


                loadingScreen.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                loadingScreen.dismiss();
            }

        }, error -> {
            loadingScreen.dismiss();

        });
        SingletonVolley.getInstanciaVolley(getApplicationContext()).addToRequestQueue(request);

    }



    private void dibujarRuta(String latt, String longg) {
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++conexion para ubicacion +++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void getGPS() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(100);
    }


    public static Boolean isLocationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            LocationManager lm = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else {
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return (mode != Settings.Secure.LOCATION_MODE_OFF);
        }
    }
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++fin de conexion para ubicacion +++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Dialog dialog = new Dialog(VIstaLic.this);
        dialog.setContentView(R.layout.custom_menu);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        TextView texto = (TextView) dialog.findViewById(R.id.aviso);
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            Animatoo.animateSlideRight(VIstaLic.this);
        }


        switch (id) {

            case R.id.itemdos:
                dialog.setTitle(R.string.conexionRed);
                texto.setText(R.string.problemas_internet);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

                break;

            case R.id.itemtres:
                dialog.setTitle(R.string.contactoSoporte);
                texto.setText(R.string.problemas_soporte);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;


            case R.id.itemdoss:
                dialog.setTitle(R.string.conexionRed);
                texto.setText(R.string.problemas_internet);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;

            case R.id.itemtress:
                dialog.setTitle(R.string.contactoSoporte);
                texto.setText(R.string.problemas_soporte);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cli, menu);
        return true;
    }



    private String moderar(String palabra){
        String ret ="";

        String moderacion = "puta PUTA puto PUTO put4 PUT4 put0 PUT0 " +
                "zorra ZORRA z0rr4 Z0RR4 mierda MIERDA m13rd4 M13RD4 " +
                "pendejo PENDEJO p3nd3jo P3ND3J0 perra PERRA p3rr4 P3RR4 " +
                "pene PENE p3n3 P3N3";

        String[]mod = moderacion.split("\\s+");
        for(String pal : mod){

            if(palabra.contains(pal)){
                ret = "palabra moderada por admin";
                break;
            } else if(palabra.equals(pal)){
                ret = "palabra moderada por admin";
                break;
            } else {
                ret = palabra;
            }
        }
        return ret;
    }


    private void cargarComentarios(String name){

        if(name.equals(null) || name.equals("")){
            name = nombre.getText().toString().trim();
        }

        String URL = getString(R.string.URL);
        String envio = URL + "/getCom.php?nombreLic="+name;
        loadingScreen.show(getSupportFragmentManager(), "Espere...");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, envio, null, response -> {
            try {
                if(response.getString("estado").equals("1")){

                    JSONArray cadenas = response.getJSONArray("mensaje");
                    for(int i=0;i<cadenas.length();i++){
                        JSONObject resulJSON = cadenas.getJSONObject(i);
                        String nombreConsultante = resulJSON.getString("n0mbr3c0ns4ltant3");
                        String consultaConsultante = resulJSON.getString("c0ns4lt4c0ns4lt4");
                        String respuestaConsulta = resulJSON.getString("r3sp43st4");

                        String coment = comentario.getText().toString();
                        String nombr = getString(R.string.nombreCon);
                        String dudas = getString(R.string.conCon);
                        String respu = getString(R.string.conRes);
                        coment = coment+nombr+nombreConsultante+"\n"+
                                dudas+consultaConsultante+"\n"+
                                respu+respuestaConsulta+"\n\n";
                        comentario.setText(coment);
                    }


                } else {
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.custom_toast_error,
                            (ViewGroup) findViewById(R.id.custom_toast_error));

                    TextView men = (TextView) layout.findViewById(R.id.mensaje);
                    men.setText("Error de envío, vuelva a intentar.");
                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                }
                loadingScreen.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                loadingScreen.dismiss();
            }

        }, error -> {
            loadingScreen.dismiss();

        });
        SingletonVolley.getInstanciaVolley(getApplicationContext()).addToRequestQueue(request);


    }

}
