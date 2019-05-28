package www.nolusaurio.club.drinkapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class pantallaPrincipalLicoreria extends AppCompatActivity {

    private FusedLocationProviderClient client;
    LocationRequest locationRequest;

    private ProgressDialog progressDialog;

    sharedPreferences sharedPreferences;
    private String cod = "";
    private String URL = "";
    String nombreLicoreriaComentarios = "";

    private TextView nombreLicoreria, descripcionLicoreria, ubii, ubiiGPS, estSw, telephone;
    private EditText promo;
    private ImageView fotografia;
    private Switch est;
    private Button actFoto, actPromo, actNombre, actDescripcion, actUbicacion, actUbicacionGPS, guardarFoto, actTele, consultas;

    final DialogFragment loadingScreen = LoadingScreen.getInstance();

    private static final String CARPETA_PRINCIPAL = "drinkapp/"; //directorio principal
    private static final String CARPETA_IMAGEN = "imagenes"; //carpeta donde se guardaran las imagenes
    private static final String DIRECTORIO_IMAGEN = CARPETA_PRINCIPAL + CARPETA_IMAGEN; //ruta carpeta de directorio
    private String path; //almacena la ruta de la imagen
    File fileImagen;
    Bitmap bitmap;
    private static final int COD_FOTO = 20;


    private int bandera = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal_licoreria);
        client = LocationServices.getFusedLocationProviderClient(this);


        progressDialog = new ProgressDialog(this);
        cod = getCodigo();
        nombreLicoreria = (TextView) findViewById(R.id.nombreLico);
        descripcionLicoreria = (TextView) findViewById(R.id.descripcionLico);
        ubii = (TextView) findViewById(R.id.ubiLic);
        ubiiGPS = (TextView) findViewById(R.id.ubiLicGPS);
        telephone = (TextView) findViewById(R.id.telefono);
        fotografia = (ImageView) findViewById(R.id.fotoLico);
        promo = (EditText) findViewById(R.id.promociones);
        est = (Switch) findViewById(R.id.estado);
        actPromo = (Button) findViewById(R.id.guardarPromo);
        estSw = (TextView) findViewById(R.id.estadoswitch);

        actFoto = (Button) findViewById(R.id.actualizarFoto);
        guardarFoto = (Button) findViewById(R.id.registrarFoto);
        actNombre = (Button) findViewById(R.id.actualizarNombre);
        actDescripcion = (Button) findViewById(R.id.actualizarDescripcion);
        actUbicacion = (Button) findViewById(R.id.actualizarUbicacion);
        actUbicacionGPS = (Button) findViewById(R.id.actualizarGps);
        actTele = (Button) findViewById(R.id.actualizarTelefono);
        consultas = (Button) findViewById(R.id.revisarConsultar);


        URL = getString(R.string.URL);

        obtenerEstado(cod); //estado del switch

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        est.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!cod.equals("0")) {
                if (isChecked) {
                    datosWeb("1", cod);
                    setEstSwitch("1");
                    estSw.setText("Si");
                    estSw.setTextColor(Color.parseColor("#64dd17"));
                    estSw.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    datosWeb("0", cod);
                    setEstSwitch("0");
                    estSw.setText("No");
                    estSw.setTextColor(Color.parseColor("#000000"));
                    estSw.setTypeface(Typeface.DEFAULT_BOLD);
                }
            }
        });

        actPromo.setOnClickListener(v -> {
            if (promo.length() > 250) {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast_general,
                        (ViewGroup) findViewById(R.id.custom_toast_gen));

                TextView men = (TextView) layout.findViewById(R.id.mensaje);
                men.setText(R.string.tamanioPromo);
                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();
            } else {
                actualizarPromocion(promo.getText().toString().trim(), cod);
            }
        });

        actNombre.setOnClickListener(v -> actualizarNombre(cod));

        actDescripcion.setOnClickListener(v -> actualizarDescripcion(cod));

        actUbicacion.setOnClickListener(v -> actualizarUbicacion(cod));


        actUbicacionGPS.setOnClickListener(v -> actualizarUbicacionGPS(cod));


        actFoto.setOnClickListener(v -> actualizarFoto());

        guardarFoto.setOnClickListener(v -> registrarFoto(nombreLicoreria.getText().toString().trim(), cod));

        actTele.setOnClickListener(v -> actualizarTelefono(cod));

        consultas.setOnClickListener(v -> {
            Intent i = new Intent(pantallaPrincipalLicoreria.this, consultasLicoreria.class);
            i.putExtra("nombreLicoreria", nombreLicoreriaComentarios);
            startActivity(i);
        });


    }


    //********************************************************
    //********************************************************
    //****************wactualizaciones ***********************
    //********************************************************
    //********************************************************
    //********************************************************
    private void actualizarTelefono(String cod) {
        String URL = getString(R.string.URL);
        String URL_GET = URL + "/actualizarTeleph.php?teleph=";
        final String BACKURL = URL_GET;

        LayoutInflater layoutInflater = LayoutInflater.from(pantallaPrincipalLicoreria.this);
        View promptView = layoutInflater.inflate(R.layout.acttel, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(pantallaPrincipalLicoreria.this);
        builder.setView(promptView);

        EditText update = (EditText) promptView.findViewById(R.id.actualizaftelefono);

        builder.setCancelable(false).setPositiveButton("OK", (dialog, which) -> {

            loadingScreen.show(getSupportFragmentManager(), "Espere...");
            String actualizar = URL_GET + update.getText().toString().trim() + "&codigo=" + cod;
            actualizar = actualizar.replaceAll(" ", "%20");

            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, actualizar, null,
                    response -> {
                        try {
                            String resulJSON = response.getString("estado");
                            if (resulJSON.equals("1")) {
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.custom_toast_success,
                                        (ViewGroup) findViewById(R.id.custom_toast_success));

                                TextView men = (TextView) layout.findViewById(R.id.mensaje);
                                men.setText(R.string.actualizacionTelefono);
                                Toast toast = new Toast(getApplicationContext());
                                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.setView(layout);
                                toast.show();

                                telephone.setText(update.getText().toString().trim());
                            } else {

                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.custom_toast_error,
                                        (ViewGroup) findViewById(R.id.custom_toast_error));

                                TextView men = (TextView) layout.findViewById(R.id.mensaje);
                                men.setText(R.string.actualizacionError);
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
                    },
                    error -> {
                        progressDialog.hide();
                    }
            );
            SingletonVolley.getInstanciaVolley(getApplicationContext()).addToRequestQueue(getRequest);
            actualizar = BACKURL;

        }).setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        AlertDialog alert = builder.create();

        alert.show();
    }


    private void actualizarNombre(String cod) {
        String URL = getString(R.string.URL);
        String URL_GET = URL + "/actualizarNombre.php?nombre=";
        final String BACKURL = URL_GET;


        LayoutInflater layoutInflater = LayoutInflater.from(pantallaPrincipalLicoreria.this);
        View promptView = layoutInflater.inflate(R.layout.actnom, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(pantallaPrincipalLicoreria.this);
        builder.setView(promptView);

        EditText actnombre = (EditText) promptView.findViewById(R.id.actualizarnombr);

        builder.setCancelable(false).setPositiveButton("OK", (dialog, which) -> {

            loadingScreen.show(getSupportFragmentManager(), "Espere...");
            String actualizar = URL_GET + actnombre.getText().toString().trim() + "&codigo=" + cod;
            actualizar = actualizar.replaceAll(" ", "%20");

            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, actualizar, null,
                    response -> {
                        try {
                            String resulJSON = response.getString("estado");
                            if (resulJSON.equals("1")) {

                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.custom_toast_nombre,
                                        (ViewGroup) findViewById(R.id.custom_toast_nombre));

                                TextView men = (TextView) layout.findViewById(R.id.mensaje);
                                men.setText(R.string.actualizacionNombre);
                                Toast toast = new Toast(getApplicationContext());
                                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.setView(layout);
                                toast.show();
                                nombreLicoreria.setText(actnombre.getText().toString().trim());
                            } else {
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.custom_toast_error,
                                        (ViewGroup) findViewById(R.id.custom_toast_error));

                                TextView men = (TextView) layout.findViewById(R.id.mensaje);
                                men.setText(R.string.actualizacionError);
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
                    },
                    error -> {
                        progressDialog.hide();
                    }
            );
            SingletonVolley.getInstanciaVolley(getApplicationContext()).addToRequestQueue(getRequest);
            actualizar = BACKURL;

        }).setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        AlertDialog alert = builder.create();

        alert.show();
    }


    private void actualizarDescripcion(String cod) {
        String URL = getString(R.string.URL);
        String URL_GET = URL + "/actualizarDescripcion.php?descripcion=";
        final String BACKURL = URL_GET;

        LayoutInflater layoutInflater = LayoutInflater.from(pantallaPrincipalLicoreria.this);
        View promptView = layoutInflater.inflate(R.layout.actdes, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(pantallaPrincipalLicoreria.this);
        builder.setView(promptView);

        EditText update = (EditText) promptView.findViewById(R.id.actualizardesc);

        builder.setCancelable(false).setPositiveButton("OK", (dialog, which) -> {

            loadingScreen.show(getSupportFragmentManager(), "Espere...");
            String actualizar = URL_GET + update.getText().toString().trim() + "&codigo=" + cod;
            actualizar = actualizar.replaceAll(" ", "%20");


            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, actualizar, null,
                    response -> {
                        try {
                            String resulJSON = response.getString("estado");
                            if (resulJSON.equals("1")) {
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.custom_toast_descripcion,
                                        (ViewGroup) findViewById(R.id.custom_toast_descripcion));

                                TextView men = (TextView) layout.findViewById(R.id.mensaje);
                                men.setText(R.string.actualizacionDescripcion);
                                Toast toast = new Toast(getApplicationContext());
                                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.setView(layout);
                                toast.show();

                                descripcionLicoreria.setText(update.getText().toString().trim());
                            } else {

                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.custom_toast_error,
                                        (ViewGroup) findViewById(R.id.custom_toast_error));

                                TextView men = (TextView) layout.findViewById(R.id.mensaje);
                                men.setText(R.string.actualizacionError);
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
                    },
                    error -> {
                        progressDialog.hide();
                    }
            );
            SingletonVolley.getInstanciaVolley(getApplicationContext()).addToRequestQueue(getRequest);
            actualizar = BACKURL;

        }).setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        AlertDialog alert = builder.create();

        alert.show();
    }


    private void actualizarUbicacion(String cod) {
        String URL = getString(R.string.URL);
        String URL_GET = URL + "/actualizarUbicacion.php?ubicacion=";
        final String BACKURL = URL_GET;

        LayoutInflater layoutInflater = LayoutInflater.from(pantallaPrincipalLicoreria.this);
        View promptView = layoutInflater.inflate(R.layout.actubi, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(pantallaPrincipalLicoreria.this);
        builder.setView(promptView);

        EditText update = (EditText) promptView.findViewById(R.id.actualizarubicacio);

        builder.setCancelable(false).setPositiveButton("OK", (dialog, which) -> {

            loadingScreen.show(getSupportFragmentManager(), "Espere...");
            String actualizar = URL_GET + update.getText().toString().trim() + "&codigo=" + cod;
            actualizar = actualizar.replaceAll(" ", "%20");


            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, actualizar, null,
                    response -> {
                        try {
                            String resulJSON = response.getString("estado");
                            if (resulJSON.equals("1")) {
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.custom_toast_ubicacion,
                                        (ViewGroup) findViewById(R.id.custom_toast_ubicacion));

                                TextView men = (TextView) layout.findViewById(R.id.mensaje);
                                men.setText(R.string.actualizacionUbicacion);
                                Toast toast = new Toast(getApplicationContext());
                                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.setView(layout);
                                toast.show();
                                ubii.setText(update.getText().toString().trim());
                            } else {
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.custom_toast_error,
                                        (ViewGroup) findViewById(R.id.custom_toast_error));

                                TextView men = (TextView) layout.findViewById(R.id.mensaje);
                                men.setText(R.string.actualizacionError);
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
                    },
                    error -> {
                        progressDialog.hide();
                    }
            );
            SingletonVolley.getInstanciaVolley(getApplicationContext()).addToRequestQueue(getRequest);
            actualizar = BACKURL;

        }).setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        AlertDialog alert = builder.create();

        alert.show();
    }


    @SuppressLint("MissingPermission")
    private void actualizarUbicacionGPS(String cod) {
        String URL = getString(R.string.URL);
        String URL_GET = URL + "/actualizarUbicacionGPS.php?ubicaciongps=";
        final String BACKURL = URL_GET;

        LayoutInflater layoutInflater = LayoutInflater.from(pantallaPrincipalLicoreria.this);
        View promptView = layoutInflater.inflate(R.layout.actubigps, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(pantallaPrincipalLicoreria.this);
        builder.setView(promptView);

        final EditText update = (EditText) promptView.findViewById(R.id.actualizarubicaciogps);

        if (isLocationEnabled(getApplicationContext())) {

            getGPS();


            builder.setCancelable(false).setPositiveButton("OK", (dialog, which) -> {

                loadingScreen.show(getSupportFragmentManager(), "Espere...");

                client.requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        String direccion = (locationResult.getLastLocation().getLatitude() + "," + locationResult.getLastLocation().getLongitude());
                        client.removeLocationUpdates(this);

                        final String actualizar = URL_GET + direccion.trim() + "&codigo=" + cod;


                        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, actualizar, null,
                                response -> {
                                    try {
                                        String resulJSON = response.getString("estado");
                                        if (resulJSON.equals("1")) {
                                            LayoutInflater inflater = getLayoutInflater();
                                            View layout = inflater.inflate(R.layout.custom_toast_ubicaciongps,
                                                    (ViewGroup) findViewById(R.id.custom_toast_ubicaciongps));

                                            TextView men = (TextView) layout.findViewById(R.id.mensaje);
                                            men.setText(R.string.actualizacionUbicacionGPS);
                                            Toast toast = new Toast(getApplicationContext());
                                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                            toast.setDuration(Toast.LENGTH_SHORT);
                                            toast.setView(layout);
                                            toast.show();
                                            ubiiGPS.setText(direccion);
                                        } else {
                                            LayoutInflater inflater = getLayoutInflater();
                                            View layout = inflater.inflate(R.layout.custom_toast_error,
                                                    (ViewGroup) findViewById(R.id.custom_toast_error));

                                            TextView men = (TextView) layout.findViewById(R.id.mensaje);
                                            men.setText(R.string.actualizacionError);
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
                                },
                                error -> {
                                    progressDialog.hide();
                                }
                        );
                        SingletonVolley.getInstanciaVolley(getApplicationContext()).addToRequestQueue(getRequest);

                    }
                }, getMainLooper());


            }).setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            AlertDialog alert = builder.create();

            alert.show();


        } else {
            Toast.makeText(getApplicationContext(), R.string.gpsEstadoInactivo, Toast.LENGTH_SHORT).show();
        }
    }


    private void actualizarPromocion(String pro, String cod) {
        String URL = getString(R.string.URL);
        String URL_GET = URL + "/actualizarPromocion.php?promocion=";
        final String BACKURL = URL_GET;


        loadingScreen.show(getSupportFragmentManager(), "Espere...");
        String actualizar = URL_GET + pro.toString().trim() + "&codigo=" + cod;
        actualizar = actualizar.replaceAll(" ", "%20");


        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, actualizar, null,
                response -> {
                    try {
                        String resulJSON = response.getString("estado");
                        if (resulJSON.equals("1")) {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.custom_toast_success,
                                    (ViewGroup) findViewById(R.id.custom_toast_success));

                            TextView men = (TextView) layout.findViewById(R.id.mensaje);
                            men.setText(R.string.actualizacionPromocion);
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                            promo.setText(promo.getText().toString().trim());
                        } else {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.custom_toast_error,
                                    (ViewGroup) findViewById(R.id.custom_toast_error));

                            TextView men = (TextView) layout.findViewById(R.id.mensaje);
                            men.setText(R.string.actualizacionError);
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
                },
                error -> {
                    progressDialog.hide();
                }
        );
        SingletonVolley.getInstanciaVolley(getApplicationContext()).addToRequestQueue(getRequest);
        actualizar = BACKURL;

    }


    private void actualizarFoto() {

        LayoutInflater layoutInflater = LayoutInflater.from(pantallaPrincipalLicoreria.this);
        View promptView = layoutInflater.inflate(R.layout.actfoto, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(pantallaPrincipalLicoreria.this, R.style.alertdialog);
        builder.setView(promptView);


        builder.setPositiveButton("Cargar Foto", (dialog, which) -> {

            cargarFotografia();


        }).setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        AlertDialog alert = builder.create();

        alert.show();

    }


    private void registrarFoto(String nombre, String cod) {
        String URL = getString(R.string.URL);
        String url = URL + "/actualizarFoto.php";

        if (bandera == 1) {

            if (bitmap != null) {

                bitmap = redimensionarImagen(bitmap, 500, 500);
                String img = convertirImgString(bitmap);

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("nombrelic", nombre);
                    jsonObject.put("codigo", getCodigo());
                    jsonObject.put("imagen", img + "\"\\}");

                    final String requestBody = jsonObject.toString();

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                        if (response.equals("200")) {
                            fotografia.setImageBitmap(bitmap);
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.custom_toast_foto,
                                    (ViewGroup) findViewById(R.id.custom_toast_gen));

                            TextView men = (TextView) layout.findViewById(R.id.mensaje);
                            men.setText(R.string.actualizacionFoto);
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();

                        } else {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.custom_toast_error,
                                    (ViewGroup) findViewById(R.id.custom_toast_error));

                            TextView men = (TextView) layout.findViewById(R.id.mensaje);
                            men.setText(R.string.registroLicoreriaFallido);
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                        }

                    }, error -> {
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.custom_toast_error,
                                (ViewGroup) findViewById(R.id.custom_toast_error));

                        TextView men = (TextView) layout.findViewById(R.id.mensaje);
                        men.setText(R.string.erroConexionVolley);
                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();

                    }) {
                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }

                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            try {
                                return requestBody == null ? null : requestBody.getBytes("utf-8");
                            } catch (UnsupportedEncodingException e) {
                                VolleyLog.wtf("UnsupportedEncodingException", requestBody, "utf-8");
                                return null;
                            }
                        }

                        @Override
                        protected Response<String> parseNetworkResponse(NetworkResponse response) {
                            String responseString = "";
                            if (response != null) {
                                responseString = String.valueOf(response.statusCode);
                            }
                            return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                        }
                    };
                    SingletonVolley.getInstanciaVolley(getApplicationContext()).addToRequestQueue(stringRequest);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast_error_tipoimagen,
                        (ViewGroup) findViewById(R.id.custom_toast_error_tipoimagen));

                TextView men = (TextView) layout.findViewById(R.id.errorimagen);
                men.setText(R.string.errorimagen);
                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();
            }
        } else {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast_error,
                    (ViewGroup) findViewById(R.id.custom_toast_error));

            TextView men = (TextView) layout.findViewById(R.id.mensaje);
            men.setText(R.string.actualizarFotoAviso);
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }

    }

    //********************************************************
    //********************************************************
    //****************web service ***********************
    //********************************************************
    //********************************************************
    //********************************************************
    private void obtenerEstado(String cod) {
        String URL = getString(R.string.URL);
        String URL_GET = URL + "/getData.php?codigo=";
        final String BACKURL = URL_GET;
        URL_GET = URL_GET + cod;


        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL_GET, null,
                response -> {
                    try {
                        String resulJSON = response.getString("estado");
                        if (resulJSON.equals("1")) {

                            JSONObject object = response.getJSONObject("mensaje");
                            String estate = object.getString("est4d0");

                            if (estate.equals("1")) {
                                est.setChecked(true);
                                obtenerDatos();
                            } else {
                                est.setChecked(false);
                                obtenerDatos();
                            }

                        } else {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.custom_toast_error,
                                    (ViewGroup) findViewById(R.id.custom_toast_error));

                            TextView men = (TextView) layout.findViewById(R.id.mensaje);
                            men.setText(R.string.errorDesconexion);
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    progressDialog.hide();


                }
        );
        SingletonVolley.getInstanciaVolley(getApplicationContext()).addToRequestQueue(getRequest);
        URL_GET = BACKURL;
    }


    private void datosWeb(String arg, String cod) {
        String URL = getString(R.string.URL);
        String URL_ACTUALIZAR = URL + "/actualizarRegsitro.php";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("c0dig0r3g1str0", cod);
            jsonObject.put("est4d0", arg);

            final String requestBody = jsonObject.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ACTUALIZAR, response -> {
                if (response.equals("200")) {
                } else {
                }

            }, error -> {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast_error,
                        (ViewGroup) findViewById(R.id.custom_toast_error));

                TextView men = (TextView) layout.findViewById(R.id.mensaje);
                men.setText(R.string.erroConexionVolley);
                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();
                error.toString();
                error.getMessage();
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException e) {
                        VolleyLog.wtf("UnsupportedEncodingException", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            SingletonVolley.getInstanciaVolley(getApplicationContext()).addToRequestQueue(stringRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private void obtenerDatos() {
        String URL = getString(R.string.URL);
        String URL_GET = URL + "/getData.php?codigo=";
        String URL_IMAGEN = URL + "/";

        final String BACKURL = URL_GET;
        URL_GET = URL_GET + cod;


        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL_GET, null,
                response -> {
                    try {
                        if (response.getString("estado").equals("1")) {

                            JSONObject resulJSON = response.getJSONObject("mensaje");
                            String nom = resulJSON.getString("n0mbr3L1c");
                            nombreLicoreriaComentarios = nom;
                            String tell = resulJSON.getString("t3l3f0n0");
                            String des = resulJSON.getString("d3scr1pc10n");
                            String ruta = resulJSON.getString("ruta_imagen");
                            String ubi = resulJSON.getString("ub1c4c10n");
                            String ubiGPS = resulJSON.getString("ub1c4c10nGPS");
                            String pro = resulJSON.getString("pr0m0c10n");
                            String URL_IMAGEN2 = URL_IMAGEN + ruta;
                            cargarWebServiceImagen(URL_IMAGEN2);
                            nombreLicoreria.setText(nom);
                            descripcionLicoreria.setText(des);
                            ubii.setText(ubi);
                            ubiiGPS.setText(ubiGPS);
                            if (pro.isEmpty() || pro.equals("") || pro.equals(null) || pro.equals("null")) {
                                promo.setText("Sin promociones");
                            } else {
                                promo.setText(pro);
                            }
                            telephone.setText(tell + "");

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialog.hide();


                },
                error -> {
                    progressDialog.hide();
                }
        );
        SingletonVolley.getInstanciaVolley(getApplicationContext()).addToRequestQueue(getRequest);

    }


    private void cargarWebServiceImagen(String url) {
        url = url.replace(" ", "%20");
        ImageRequest imageRequest = new ImageRequest(url, response -> fotografia.setImageBitmap(response), 0, 0, ImageView.ScaleType.CENTER_INSIDE, null, error -> Toast.makeText(getApplicationContext(), "ERROR AL CARGAR", Toast.LENGTH_SHORT).show());

        SingletonVolley.getInstanciaVolley(getApplicationContext()).addToRequestQueue(imageRequest);

    }
    //********************************************************
    //********************************************************
    //****************fin web service ************************
    //********************************************************
    //********************************************************
    //********************************************************


    private String getCodigo() {
        sharedPreferences = new sharedPreferences(getApplicationContext());
        String bandera = sharedPreferences.getCodigo();
        return bandera;
    }

    private void setEstSwitch(String band) {
        sharedPreferences = new sharedPreferences(getApplicationContext());
        sharedPreferences.guardarEstadoSwitch(band);
    }

    private String getEstSwitch() {
        sharedPreferences = new sharedPreferences(getApplicationContext());
        String bandera = sharedPreferences.getEstadoSwitch();
        return bandera;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateFade(pantallaPrincipalLicoreria.this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Dialog dialog = new Dialog(pantallaPrincipalLicoreria.this);
        dialog.setContentView(R.layout.custom_menu);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        TextView texto = (TextView) dialog.findViewById(R.id.aviso);
        int id = item.getItemId();


        switch (id) {
            case R.id.itemuno:
                dialog.setTitle(R.string.orientacionFoto);
                texto.setText(R.string.problemas_camara);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;

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

            case R.id.itemcuatro:
                dialog.setTitle(R.string.gpsproblemas);
                texto.setText(R.string.problemas_gps);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;

            case android.R.id.home:
                this.finish();
                Animatoo.animateFade(pantallaPrincipalLicoreria.this);
                break;

            case R.id.itemunoo:
                dialog.setTitle(R.string.orientacionFoto);
                texto.setText(R.string.problemas_camara);
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

            case R.id.itemcuatroo:
                dialog.setTitle(R.string.gpsproblemas);
                texto.setText(R.string.problemas_gps);
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

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //+++++++++++++++cama y fotografia++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private String convertirImgString(Bitmap bitmap) {
        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, array);
        byte[] imgByte = array.toByteArray();
        String imgString = Base64.encodeToString(imgByte, Base64.DEFAULT);
        return imgString;
    }

    private Bitmap redimensionarImagen(Bitmap bitmap, float anchoNuevo, float altoNuevo) {


        int ancho = bitmap.getWidth();
        int alto = bitmap.getHeight();

        if (ancho > anchoNuevo || alto > altoNuevo) {
            float scalaAncho = anchoNuevo / ancho;
            float scalaAlto = altoNuevo / alto;

            Matrix matrix = new Matrix();
            matrix.postScale(scalaAncho, scalaAlto);
            return Bitmap.createBitmap(bitmap, 0, 0, ancho, alto, matrix, false);
        } else {
            return bitmap;
        }

    }

    private void camara() {

        File miFile = new File(Environment.getExternalStorageDirectory(), DIRECTORIO_IMAGEN);
        boolean isCreated = miFile.exists();

        if (isCreated == false) {
            isCreated = miFile.mkdirs();
        }

        if (isCreated == true) {
            Long consecutivo = System.currentTimeMillis() / 1000;
            String nombre = consecutivo.toString() + ".jpg";
            path = Environment.getExternalStorageDirectory() + File.separator + DIRECTORIO_IMAGEN
                    + File.separator + nombre; //ruta de almacenamiento

            fileImagen = new File(path);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


            startActivityForResult(intent, COD_FOTO);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (data != null) {


            if (requestCode == 100) {
                Uri path = data.getData();

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                    fotografia.setImageURI(path);
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "error conversion bitmap 1", Toast.LENGTH_SHORT).show();
                }

            } else {
                if (resultCode == RESULT_OK) {

                    MediaScannerConnection.scanFile(this, new String[]{path}, null,
                            (path, uri) -> {

                            });
                    bitmap = BitmapFactory.decodeFile(path);
                    fotografia.setImageBitmap(bitmap);
                }
            }
        }

        switch (requestCode) {
            case COD_FOTO:
                MediaScannerConnection.scanFile(this, new String[]{path}, null,
                        (path, uri) -> {
                        });
                bitmap = BitmapFactory.decodeFile(path);
                fotografia.setImageBitmap(bitmap);
                break;

        }

    }


    private void cargarFotografia() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/jpg");

        bandera = 1;
        startActivityForResult(i.createChooser(i, "Seleccione la aplicaciòn"), 100);
    }
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //+++++++++++++++fin de camara y fotografia+++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menulico, menu);
        return true;
    }
}
