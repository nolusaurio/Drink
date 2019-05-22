package www.nolusaurio.club.drinkapp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import com.android.volley.toolbox.JsonObjectRequest;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;

import org.json.JSONException;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private ImageView registrarLic, buscarLic;

    //para evitar la copia de la aplicacion se obtiene el imei
    private String imei;

    //referencia a la clase sharedPreferences para indicar el uso de la app en el registro y d+
    sharedPreferences sharedPreferences;

    final DialogFragment loadingScreen = LoadingScreen.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        registrarLic = (ImageView) findViewById(R.id.registroLicorerias);
        buscarLic = (ImageView) findViewById(R.id.buscarLicorerias);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        if (Permisos()) {
            Log.w("Permisos", "true");
            botones();
        } else {
            Log.w("Permisos", "false");
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast_permisos,
                    (ViewGroup) findViewById(R.id.custom_toast_permisos));

            TextView men = (TextView) layout.findViewById(R.id.mensaje);
            men.setText(R.string.habilitarPermisos);
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
            //Toast.makeText(this, R.string.habilitarPermisos, Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("MissingPermission")
    private void botones() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei = telephonyManager.getDeviceId();
        registrarLic.setOnClickListener(v -> checkCode());

        buscarLic.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, tipoBusqueda.class);
            startActivity(i);
            Animatoo.animateSwipeLeft(MainActivity.this);

        });
    }

    private void checkCode() {
        //primero se verificara que la aplicacion ya estaba en uso de acuerdo a una bandera de sharedPreferences
        String banderaUSO = getBandera();

        String cod = "";
        Log.w("MAINACtivi Code Band==>", banderaUSO);

        if (banderaUSO.equals("1")) { //significa que la aplicacion ya fue utilizada para el registro
            //se procede a obtener el codigo utilizado y validar que el mismo codigo, estado e imei (si siguen activod)
            cod = getCodigo();
            Log.w("MAINACtivi Code code==>", cod);
            verificarCodigoActivo(banderaUSO, cod);
        } else {
            registrarCodigo();
        }
    }

    private void verificarCodigoActivo(String bandera, String cod) {
        Log.w("MAINACtiv verificarCod>", "aca");

        loadingScreen.show(getSupportFragmentManager(), "Espere...");


        String URL = getApplication().getString(R.string.URL);
        String URLVerificarCampos = URL + "/drinkapp/comCampos.php?codigo=";
        String BACKURL = URLVerificarCampos;
        URLVerificarCampos = URLVerificarCampos + cod + "&imei=" + imei + "&estado=" + bandera;

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_success,
                (ViewGroup) findViewById(R.id.custom_toast_success));

        TextView men = (TextView) layout.findViewById(R.id.mensaje);
        men.setText(R.string.verificacionDatos);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();


        Log.w("veriCodActivo:", "codigo:" + URLVerificarCampos);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URLVerificarCampos, null,
                response -> {
                    try {
                        String resulJSON = response.getString("estado");
                        Log.e("MAINACTIVITY===>", resulJSON);
                        switch (resulJSON) {
                            case "1":
                                LayoutInflater inflater1 = getLayoutInflater();
                                View layout1 = inflater1.inflate(R.layout.custom_toast_error,
                                        (ViewGroup) findViewById(R.id.custom_toast_error));

                                TextView men1 = (TextView) layout1.findViewById(R.id.mensaje);
                                men1.setText(R.string.imeiDistinto);
                                Toast toast1 = new Toast(getApplicationContext());
                                toast1.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast1.setDuration(Toast.LENGTH_SHORT);
                                toast1.setView(layout1);
                                toast1.show();
                                //Toast.makeText(getApplicationContext(), R.string.imeiDistinto, Toast.LENGTH_SHORT).show();
                                registrarCodigo();
                                break;
                            case "2":
                                LayoutInflater inflater2 = getLayoutInflater();
                                View layout2 = inflater2.inflate(R.layout.custom_toast_error,
                                        (ViewGroup) findViewById(R.id.custom_toast_error));

                                TextView men2 = (TextView) layout2.findViewById(R.id.mensaje);
                                men2.setText(R.string.estadoInactivo);
                                Toast toast2 = new Toast(getApplicationContext());
                                toast2.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast2.setDuration(Toast.LENGTH_SHORT);
                                toast2.setView(layout2);
                                toast2.show();
                                //toast2.makeText(getApplicationContext(), R.string.estadoInactivo, Toast.LENGTH_SHORT).show();
                                registrarCodigo();
                                break;
                            case "3":
                                String registroLicoreria = getRegigstroLic();
                                if (registroLicoreria.equals("1")) {
                                    Intent i = new Intent(MainActivity.this, pantallaPrincipalLicoreria.class);
                                    startActivity(i);
                                    Animatoo.animateFade(MainActivity.this);

                                } else {
                                    Intent i = new Intent(MainActivity.this, registroLicoreria.class);
                                    startActivity(i);
                                }
                                break;
                            case "4":
                                LayoutInflater inflater4 = getLayoutInflater();
                                View layout4 = inflater4.inflate(R.layout.custom_toast_error,
                                        (ViewGroup) findViewById(R.id.custom_toast_error));

                                TextView men4 = (TextView) layout4.findViewById(R.id.mensaje);
                                men4.setText(R.string.codigoInactivo);
                                Toast toast4 = new Toast(getApplicationContext());
                                toast4.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast4.setDuration(Toast.LENGTH_SHORT);
                                toast4.setView(layout4);
                                toast4.show();
                                //Toast.makeText(getApplicationContext(), R.string.codigoInactivo, Toast.LENGTH_SHORT).show();
                                registrarCodigo();
                                break;
                            case "5":
                                LayoutInflater inflater5 = getLayoutInflater();
                                View layout5 = inflater5.inflate(R.layout.custom_toast_error,
                                        (ViewGroup) findViewById(R.id.custom_toast_error));

                                TextView men5 = (TextView) layout5.findViewById(R.id.mensaje);
                                men5.setText(R.string.errorDesconocido);
                                Toast toast5 = new Toast(getApplicationContext());
                                toast5.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast5.setDuration(Toast.LENGTH_SHORT);
                                toast5.setView(layout5);
                                toast5.show();
                                //Toast.makeText(getApplicationContext(), R.string.errorDesconocido, Toast.LENGTH_SHORT).show();
                                break;
                        }
                        loadingScreen.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        loadingScreen.dismiss();
                    }
                },
                error -> {
                    LayoutInflater inflater5 = getLayoutInflater();
                    View layout5 = inflater5.inflate(R.layout.custom_toast_error,
                            (ViewGroup) findViewById(R.id.custom_toast_error));

                    TextView men5 = (TextView) layout5.findViewById(R.id.mensaje);
                    men5.setText(R.string.errorDesconexion);
                    Toast toast5 = new Toast(getApplicationContext());
                    toast5.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast5.setDuration(Toast.LENGTH_SHORT);
                    toast5.setView(layout5);
                    toast5.show();
                    //Toast.makeText(getApplicationContext(), R.string.errorDesconexion, Toast.LENGTH_SHORT).show();
                    loadingScreen.dismiss();
                });
        URLVerificarCampos = BACKURL;
        SingletonVolley.getInstanciaVolley(getApplicationContext()).addToRequestQueue(request);

    }


    private void registrarCodigo() {
        Log.w("MAIN registrarCodigo>", "aca");

        //setear a cero sharedpreferences
        sharedPreferences.guardarCodigo("");
        sharedPreferences.guardarRegistroLic("0");
        sharedPreferences.guardarEstadoSwitch("0");

        //

        String URL = getApplication().getString(R.string.URL);
        String URLVerificacionCodigo = URL + "/drinkapp/verificarCodigo2.php?codigo=";
        final String BACKURL = URLVerificacionCodigo;


        LayoutInflater inflater55 = getLayoutInflater();
        View layout55 = inflater55.inflate(R.layout.custom_toast_success,
                (ViewGroup) findViewById(R.id.custom_toast_success));

        TextView men55 = (TextView) layout55.findViewById(R.id.mensaje);
        men55.setText(R.string.advertenciaSMS);
        Toast toast55 = new Toast(getApplicationContext());
        toast55.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast55.setDuration(Toast.LENGTH_SHORT);
        toast55.setView(layout55);
        toast55.show();


        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.ingresocodigo, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(promptView);


        final EditText codigo = (EditText) promptView.findViewById(R.id.codigoVerificacion);
        sharedPreferences.guardarCodigo("");
        sharedPreferences.guardarRegistroLic("0");
        sharedPreferences.guardarEstadoSwitch("0");
        sharedPreferences.guardarRegistroAPP("0");
        Log.e("MAIN=====>", sharedPreferences.getRegistroAPP());

        builder.setCancelable(false).setPositiveButton("OK", (dialog, which) -> {

            loadingScreen.show(getSupportFragmentManager(), "Espere...");

            String URLVerificacionCodigo2 = URLVerificacionCodigo + codigo.getText().toString().trim() + "&imei=" + imei.trim();
            Log.w("MainActividy CODIGO==+>", URLVerificacionCodigo2);

            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URLVerificacionCodigo2, null,
                    response -> {

                        try {
                            String resulJSON = response.getString("estado");
                            //si es 2 es que el codigo esta activo
                            Log.e("MAINACTIVITY=======>", resulJSON);
                            if (resulJSON.equals("1")) {
                                Intent i = new Intent(MainActivity.this, registroLicoreria.class);
                                startActivity(i);
                                sharedPreferences = new sharedPreferences(getApplicationContext());
                                sharedPreferences.guardarRegistroAPP("1");
                                sharedPreferences.guardarCodigo(codigo.getText().toString().trim());
                            } else if (resulJSON.equals("2")) {
                                sharedPreferences = new sharedPreferences(getApplicationContext());
                                sharedPreferences.guardarRegistroAPP("0");

                                //setear a cero sharedpreferences
                                sharedPreferences.guardarCodigo("");
                                sharedPreferences.guardarRegistroLic("0");
                                sharedPreferences.guardarEstadoSwitch("0");

                                LayoutInflater inflater5 = getLayoutInflater();
                                View layout5 = inflater5.inflate(R.layout.custom_toast_error,
                                        (ViewGroup) findViewById(R.id.custom_toast_error));

                                TextView men5 = (TextView) layout5.findViewById(R.id.mensaje);
                                men5.setText("Codigo en uso, ingrese uno nuevo");
                                Toast toast5 = new Toast(getApplicationContext());
                                toast5.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast5.setDuration(Toast.LENGTH_SHORT);
                                toast5.setView(layout5);
                                toast5.show();
                            } else if (resulJSON.equals("3")) {
                                sharedPreferences = new sharedPreferences(getApplicationContext());
                                sharedPreferences.guardarRegistroAPP("0");

                                //setear a cero sharedpreferences
                                sharedPreferences.guardarCodigo("");
                                sharedPreferences.guardarRegistroLic("0");
                                sharedPreferences.guardarEstadoSwitch("0");

                                LayoutInflater inflater5 = getLayoutInflater();
                                View layout5 = inflater5.inflate(R.layout.custom_toast_error,
                                        (ViewGroup) findViewById(R.id.custom_toast_error));

                                TextView men5 = (TextView) layout5.findViewById(R.id.mensaje);
                                men5.setText("Codigo en uso, suscribase");
                                Toast toast5 = new Toast(getApplicationContext());
                                toast5.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast5.setDuration(Toast.LENGTH_SHORT);
                                toast5.setView(layout5);
                                toast5.show();
                            } else if (resulJSON.equals("4")) {
                                sharedPreferences = new sharedPreferences(getApplicationContext());
                                sharedPreferences.guardarRegistroAPP("0");

                                //setear a cero sharedpreferences
                                sharedPreferences.guardarCodigo("");
                                sharedPreferences.guardarRegistroLic("0");
                                sharedPreferences.guardarEstadoSwitch("0");

                                LayoutInflater inflater5 = getLayoutInflater();
                                View layout5 = inflater5.inflate(R.layout.custom_toast_error,
                                        (ViewGroup) findViewById(R.id.custom_toast_error));

                                TextView men5 = (TextView) layout5.findViewById(R.id.mensaje);
                                men5.setText("Error de red, intente nuevamente");
                                Toast toast5 = new Toast(getApplicationContext());
                                toast5.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast5.setDuration(Toast.LENGTH_SHORT);
                                toast5.setView(layout5);
                                toast5.show();
                            } else if (resulJSON.equals("5")) {
                                Intent i = new Intent(MainActivity.this, pantallaPrincipalLicoreria.class);
                                startActivity(i);
                                sharedPreferences = new sharedPreferences(getApplicationContext());
                                sharedPreferences.guardarRegistroAPP("1");
                                sharedPreferences.guardarRegistroLic("1");
                                sharedPreferences.guardarCodigo(codigo.getText().toString().trim());

                            } else if (resulJSON.equals("6")) {
                                sharedPreferences = new sharedPreferences(getApplicationContext());
                                sharedPreferences.guardarRegistroAPP("0");

                                //setear a cero sharedpreferences
                                sharedPreferences.guardarCodigo("");
                                sharedPreferences.guardarRegistroLic("0");
                                sharedPreferences.guardarEstadoSwitch("0");

                                LayoutInflater inflater5 = getLayoutInflater();
                                View layout5 = inflater5.inflate(R.layout.custom_toast_error,
                                        (ViewGroup) findViewById(R.id.custom_toast_error));

                                TextView men5 = (TextView) layout5.findViewById(R.id.mensaje);
                                men5.setText("No autorizado");
                                Toast toast5 = new Toast(getApplicationContext());
                                toast5.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast5.setDuration(Toast.LENGTH_SHORT);
                                toast5.setView(layout5);
                                toast5.show();
                            } else {
                                sharedPreferences = new sharedPreferences(getApplicationContext());
                                sharedPreferences.guardarRegistroAPP("0");

                                //setear a cero sharedpreferences
                                sharedPreferences.guardarCodigo("");
                                sharedPreferences.guardarRegistroLic("0");
                                sharedPreferences.guardarEstadoSwitch("0");

                                //


                                LayoutInflater inflater5 = getLayoutInflater();
                                View layout5 = inflater5.inflate(R.layout.custom_toast_error,
                                        (ViewGroup) findViewById(R.id.custom_toast_error));

                                TextView men5 = (TextView) layout5.findViewById(R.id.mensaje);
                                men5.setText("Error, intente nuevamente");
                                Toast toast5 = new Toast(getApplicationContext());
                                toast5.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast5.setDuration(Toast.LENGTH_SHORT);
                                toast5.setView(layout5);
                                toast5.show();
                            }


                            loadingScreen.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            loadingScreen.dismiss();

                        }
                    },
                    error -> {
                        error.getMessage();
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.custom_toast_error,
                                (ViewGroup) findViewById(R.id.custom_toast_error));

                        TextView men = (TextView) layout.findViewById(R.id.mensaje);
                        men.setText("Error en servidores");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                        //Toast.makeText(getApplicationContext(),"Error en servidores", Toast.LENGTH_SHORT).show();

                        loadingScreen.dismiss();

                    });
            URLVerificacionCodigo2 = BACKURL;
            SingletonVolley.getInstanciaVolley(getApplicationContext()).addToRequestQueue(getRequest);


        }).

                setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        AlertDialog alert = builder.create();

        alert.show();

    }


    //++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++
    //obtencion de codigos de sharedpreferences
    private String getBandera() {
        sharedPreferences = new sharedPreferences(getApplicationContext());
        String bandera = sharedPreferences.getRegistroAPP();
        Log.w("MAINactivity regApp==>", bandera);
        return bandera;
    }

    private String getCodigo() {
        sharedPreferences = new sharedPreferences(getApplicationContext());
        String bandera = sharedPreferences.getCodigo();
        Log.w("MAINactivity getCod==>", bandera);
        return bandera;
    }

    private String getRegigstroLic() {
        sharedPreferences = new sharedPreferences(getApplicationContext());
        String registroLic = sharedPreferences.getRegistroLic();
        Log.w("MAINact getRegLic==>", registroLic);
        return registroLic;
    }

    //++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++
    //permisos para versiones de android superiores a la 6
    @TargetApi(Build.VERSION_CODES.M)
    private boolean Permisos() {
        Log.e("Permisos", "permisooooooo");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.w("retorno", "1");
            return true;
        }

        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                && (checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                && (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED)
                && (checkSelfPermission(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            Log.w("retorno", "2");

            return true;
        }

        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) ||
                (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) ||
                (shouldShowRequestPermissionRationale(CAMERA)) ||
                (shouldShowRequestPermissionRationale(READ_PHONE_STATE))
                ) {
            cargarDialogo();
        } else {
            requestPermissions(
                    new String[]{ACCESS_FINE_LOCATION,
                            WRITE_EXTERNAL_STORAGE,
                            READ_EXTERNAL_STORAGE,
                            CAMERA,
                            READ_PHONE_STATE
                    }, 1000);
            Log.w("retorno", "3");
        }

        return false;
    }

    private void cargarDialogo() {
        Log.w("cargarDialogo", "cargarDialogooooooo");

        AlertDialog.Builder dialogo = new AlertDialog.Builder(MainActivity.this);
        dialogo.setTitle(R.string.dialogoPermisosTitulo);
        dialogo.setMessage(R.string.dialogoPermisosMensajes);

        dialogo.setPositiveButton("Aceptar", (dialog, which) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{ACCESS_FINE_LOCATION,
                                WRITE_EXTERNAL_STORAGE,
                                READ_EXTERNAL_STORAGE,
                                CAMERA,
                                READ_PHONE_STATE
                        }, 1000);
            }
        });
        dialogo.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults.length == 5 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[3] == PackageManager.PERMISSION_GRANTED
                    && grantResults[4] == PackageManager.PERMISSION_GRANTED
                    ) {

                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast_error,
                        (ViewGroup) findViewById(R.id.custom_toast_error));

                TextView men = (TextView) layout.findViewById(R.id.mensaje);
                men.setText(R.string.dialogoPermisosGracias);
                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();

                // Toast.makeText(MainActivity.this, R.string.dialogoPermisosGracias, Toast.LENGTH_SHORT).show();
                botones();
            } else {
                Log.e("MainActiviti=>", "onRequestPermissionsResult");
                solicitarPermisosManual();
            }
        }
    }

    private void solicitarPermisosManual() {
        Log.e("MainActiviti=>", "solicitarPermisos Manual");
        final CharSequence[] opciones = {"si", "no"};
        final AlertDialog.Builder alertaOpciones = new AlertDialog.Builder(MainActivity.this);
        alertaOpciones.setTitle("Seleccione una opción");
        alertaOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (opciones[which].equals("si")) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } else {
                    dialog.dismiss();
                }
            }
        });
        alertaOpciones.show();

    }
}
