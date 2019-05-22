package www.nolusaurio.club.drinkapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.gson.JsonObject;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class registroLicoreria extends AppCompatActivity {

    private FusedLocationProviderClient client;
    LocationRequest locationRequest;

    //datos del boton que obtiene la localizacion
    private Button loc;
    private EditText gpss;

    //datos comunes
    private EditText nombreDueño, apellidoDueño, nombreLic, descripcion, ubicacion, tele; //gpss textviews
    private ImageView captura;
    private Button registrar, reiniciar, cancelar, foto, cargarFoto; //loc


    //datos para tomar foto
    private static final String CARPETA_PRINCIPAL = "drinkapp/"; //directorio principal
    private static final String CARPETA_IMAGEN = "imagenes"; //carpeta donde se guardaran las imagenes
    private static final String DIRECTORIO_IMAGEN = CARPETA_PRINCIPAL + CARPETA_IMAGEN; //ruta carpeta de directorio
    private String path; //almacena la ruta de la imagen
    File fileImagen;
    Bitmap bitmap;
    private static final int COD_FOTO = 20;


    //contador de campos llenos o vacios para pasar a registro
    private int conteoInverso = 1;


    //para codigo de registro se obtiene la clase sharedPreferences
    sharedPreferences sharedPreferences;

    //progres
    final DialogFragment loadingScreen = LoadingScreen.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_licoreria);
        client = LocationServices.getFusedLocationProviderClient(getApplicationContext());


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //referenciados todos los datos
        loc = (Button) findViewById(R.id.ubicacionGPS);
        gpss = (EditText) findViewById(R.id.licoreriaUbicacionGPS);

        nombreDueño = (EditText) findViewById(R.id.nombreDueno);
        apellidoDueño = (EditText) findViewById(R.id.apellidoDueño);
        nombreLic = (EditText) findViewById(R.id.licoreriaNombre);
        tele = (EditText) findViewById(R.id.telefono);
        descripcion = (EditText) findViewById(R.id.licoreriaDescripcion);
        ubicacion = (EditText) findViewById(R.id.licoreriaUbicacion);
        registrar = (Button) findViewById(R.id.btn_registrar);
        reiniciar = (Button) findViewById(R.id.btn_reiniciar);
        cancelar = (Button) findViewById(R.id.btn_cancelar);
        cargarFoto = (Button) findViewById(R.id.cargar);
        foto = (Button) findViewById(R.id.foto);
        captura = (ImageView) findViewById(R.id.fotografia);


        loc.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                if (isLocationEnabled(getApplicationContext())) {
                    Log.w("REGISTRO:", "aca");
                    getGPS();

                    client.requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            Log.w("REGISTRO2:", "" + locationResult.getLastLocation().getLatitude() + "," + locationResult.getLastLocation().getLongitude());

                            gpss.setText(locationResult.getLastLocation().getLatitude() + "," + locationResult.getLastLocation().getLongitude());
                            client.removeLocationUpdates(this);
                        }
                    }, getMainLooper());
                    Log.w("REGISTRO3:", "aca");

                } else {
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.custom_toast_error,
                            (ViewGroup) findViewById(R.id.custom_toast_error));

                    TextView men = (TextView) layout.findViewById(R.id.mensaje);
                    men.setText(R.string.gpsEstadoInactivo);
                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                    //Toast.makeText(getApplicationContext(), R.string.gpsEstadoInactivo, Toast.LENGTH_SHORT).show();
                }

            }
        });


        reiniciar.setOnClickListener(v -> {
            nombreDueño.getText().clear();
            apellidoDueño.getText().clear();
            nombreLic.getText().clear();
            descripcion.getText().clear();
            ubicacion.getText().clear();
            gpss.getText().clear();
            tele.getText().clear();
            conteoInverso = 1;
            captura.setImageBitmap(null);
        });

        cancelar.setOnClickListener(v -> finish());

        registrar.setOnClickListener(v -> verificar());

        foto.setOnClickListener(v -> camara());

        cargarFoto.setOnClickListener(v -> cargarFotografia());
    }


    private void verificar() {
        if (!nombreDueño.getText().toString().trim().equals("") &&
                !apellidoDueño.getText().toString().trim().equals("") &&
                !nombreLic.getText().toString().trim().equals("") &&
                !descripcion.getText().toString().trim().equals("") &&
                !ubicacion.getText().toString().trim().equals("") &&
                !gpss.getText().toString().trim().equals("") &&
                !tele.getText().toString().trim().equals("") &&
                conteoInverso == 0) {


            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast_enviandodatos,
                    (ViewGroup) findViewById(R.id.custom_toast_enviandodatos));

            TextView men = (TextView) layout.findViewById(R.id.mensaje);
            men.setText("Enviando datos");
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();

            //Toast.makeText(getApplicationContext(), "Enviando datos", Toast.LENGTH_SHORT).show();
            registrarLicoreria();

        } else {

            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast_error,
                    (ViewGroup) findViewById(R.id.custom_toast_error));

            TextView men = (TextView) layout.findViewById(R.id.mensaje);
            men.setText(R.string.advertenciaLicoreriaVacio);
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
            // Toast.makeText(getApplicationContext(), R.string.advertenciaLicoreriaVacio, Toast.LENGTH_LONG).show();
        }
    }

    private void registrarLicoreria() {
        String URL = getString(R.string.URL);
        String url = URL + "/drinkapp/insertarRegistro.php";

        loadingScreen.show(getSupportFragmentManager(), "Espere...");

        bitmap = redimensionarImagen(bitmap, 500, 500);
        String img = convertirImgString(bitmap);


        //verificando el nombre de la licoreria
        String verificacion = URL + "/drinkapp/verificarNombre.php?nombre=";
        String verificarNombreDoble = verificacion + nombreLic.getText().toString().trim();
        Log.w("REGISTROLICORERIA:", verificarNombreDoble);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, verificarNombreDoble, null, response -> {
            try {
                String resulJSON = response.getString("estado");
                Log.w("REGISTROLICORERIA:", "ResultJson:"+resulJSON);
                if (resulJSON.equals("1")) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("nombre", nombreDueño.getText().toString().trim());
                        jsonObject.put("apellido", apellidoDueño.getText().toString().trim());
                        jsonObject.put("telefono", tele.getText().toString().trim());
                        jsonObject.put("nombreLic", nombreLic.getText().toString().trim());
                        jsonObject.put("descripcionLic", descripcion.getText().toString().trim());
                        jsonObject.put("ubicacionLic", ubicacion.getText().toString().trim());
                        jsonObject.put("ubicacionGPSLic", gpss.getText().toString().trim());
                        jsonObject.put("codigoregistro", getCodigo());
                        jsonObject.put("imagen", img + "\\}");
                        final String requestBody = jsonObject.toString();
                        Log.w("pantPrin=========>", url);
                        Log.w("String===>", requestBody.toString().trim());

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, responsse -> {
                            Log.w("Respuesta===>", responsse.toString());
                            if (responsse.equals("200")) {
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.custom_toast_registroexitoso,
                                        (ViewGroup) findViewById(R.id.custom_toast_registroexitoso));

                                TextView men = (TextView) layout.findViewById(R.id.mensaje);
                                men.setText(R.string.registroLicoreriaExitoso);
                                Toast toast = new Toast(getApplicationContext());
                                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.setView(layout);
                                toast.show();

                                sharedPreferences = new sharedPreferences(getApplicationContext());
                                sharedPreferences.guardarRegistroLic("1");
                                Intent i = new Intent(registroLicoreria.this, pantallaPrincipalLicoreria.class);
                                startActivity(i);
                                finish();

                            } else {
                                sharedPreferences = new sharedPreferences(getApplicationContext());
                                sharedPreferences.guardarRegistroLic("-1");
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
                            loadingScreen.dismiss();


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
                            loadingScreen.dismiss();
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
                        loadingScreen.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        loadingScreen.dismiss();
                    }

                } else if (resulJSON.equals("2")) {

                    Toast.makeText(getApplicationContext(), "error el nombre existe", Toast.LENGTH_SHORT).show();
                    loadingScreen.dismiss();

                } else if (resulJSON.equals("3")) {
                    Toast.makeText(getApplicationContext(), "error en servidores, intente nuevamente", Toast.LENGTH_SHORT).show();
                    loadingScreen.dismiss();
                } else if (resulJSON.equals("4")){
                    String valor = response.getString("mensaje");

                    Toast.makeText(getApplicationContext(), "error des:"+valor, Toast.LENGTH_SHORT).show();
                    loadingScreen.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                loadingScreen.dismiss();
            }
        }, error -> {
        });
        SingletonVolley.getInstanciaVolley(getApplicationContext()).addToRequestQueue(request);



    }


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

        Log.w("CAMARA=======>", "aqui");
        File miFile = new File(Environment.getExternalStorageDirectory(), DIRECTORIO_IMAGEN);
        boolean isCreated = miFile.exists();

        if (isCreated == false) {
            Log.w("CAMARA=======>", "false");
            isCreated = miFile.mkdirs();
        }

        if (isCreated == true) {
            Log.w("CAMARA=======>", "true");
            Long consecutivo = System.currentTimeMillis() / 1000;
            String nombre = consecutivo.toString() + ".jpg";
            path = Environment.getExternalStorageDirectory() + File.separator + DIRECTORIO_IMAGEN
                    + File.separator + nombre; //ruta de almacenamiento
            //path = FileProvider.getUriForFile(registroLicoreria.this, )

            fileImagen = new File(path);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            Uri uri = FileProvider.getUriForFile(registroLicoreria.this,
                    "www.nolusaurio.club.drinkapp.provider", fileImagen);

            //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            startActivityForResult(intent, COD_FOTO);
            conteoInverso = 0;
            Log.w("CAMARA=======>", "aqui salida con:" + conteoInverso);

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
                    captura.setImageURI(path);
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "error conversion bitmap 1", Toast.LENGTH_SHORT).show();
                }

            } else {
                if (resultCode == RESULT_OK) {

                    Log.w("CARG 0====>", "foto");
                    MediaScannerConnection.scanFile(this, new String[]{path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("PATH", "" + path);
                                }
                            });
                    bitmap = BitmapFactory.decodeFile(path);
                    captura.setImageBitmap(bitmap);
                }
            }
        }

        switch (requestCode) {
            case COD_FOTO:
                Log.w("CARG====>", "foto");
                MediaScannerConnection.scanFile(this, new String[]{path}, null,
                        (path, uri) -> Log.i("PATH", "" + path));
                bitmap = BitmapFactory.decodeFile(path);

                //aca va

                //aca fin

                if (bitmap == null) {
                    Log.w("CARG BIT===>", "null");
                    conteoInverso = 1;
                } else {
                    Log.w("CARG BIT===>", "no null");

                }

                captura.setImageBitmap(bitmap);
                break;

        }

    }


    private void cargarFotografia() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/");
        startActivityForResult(i.createChooser(i, "Seleccione la aplicación"), 100);
        conteoInverso = 0;
    }
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //+++++++++++++++fin de camara y fotografia+++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++conexion para ubicacion +++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void getGPS() {
        Log.w("registroLicoreroGPS", "aca");
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(100);
    }


    public static Boolean isLocationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            LocationManager lm = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
            Log.w("registroLicoreroa", "trueeeeee");
            return lm.isLocationEnabled();
        } else {
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            Log.w("registroLicoreroa", "false");

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
    //+++++++++++++++++ obtener codigo utiliza +++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private String getCodigo() {
        sharedPreferences = new sharedPreferences(getApplicationContext());
        String bandera = sharedPreferences.getCodigo();
        Log.w("registrLic getCod==>", bandera);
        return bandera;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Dialog dialog = new Dialog(registroLicoreria.this);
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

            case R.id.home:
                this.finish();
                Animatoo.animateFade(registroLicoreria.this);
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateFade(registroLicoreria.this);
    }


    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++ options in the bar +++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menulico, menu);
        return true;
    }


}
