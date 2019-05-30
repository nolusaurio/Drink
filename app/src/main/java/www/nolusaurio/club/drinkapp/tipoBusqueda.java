package www.nolusaurio.club.drinkapp;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class tipoBusqueda extends AppCompatActivity {

    private EditText busqueda;
    private Button buscar;
    private Button busquedaGeneral;
    ArrayList<Licorerias> listLicos;
    private RecyclerView recyclerVie;
    private RecyclerView.Adapter adapter;
    private GridLayoutManager lManager;
    final DialogFragment loadingScreen = LoadingScreen.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_busqueda);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        busqueda = (EditText)findViewById(R.id.palabraabuscar);
        buscar = (Button)findViewById(R.id.buscarporzona);
        busquedaGeneral = (Button)findViewById(R.id.busquedatodos);


        recyclerVie = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerVie.setHasFixedSize(true);
        lManager = new GridLayoutManager(this, 3);
        ((GridLayoutManager) lManager).setOrientation(GridLayoutManager.VERTICAL);
        recyclerVie.setLayoutManager(lManager);
        listLicos = new ArrayList<Licorerias>();


        busqueda.setOnKeyListener((v, keyCode, event) -> {
            if((event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_ENTER))){
                buscarZona(busqueda.getText().toString().trim());
                return true;
            }
            return false;
        });


        buscar.setOnClickListener(v -> buscarZona(busqueda.getText().toString().trim()));

        busquedaGeneral.setOnClickListener(v -> {
            Intent i = new Intent(tipoBusqueda.this, BuscarLicos.class);
            startActivity(i);
            Animatoo.animateShrink(tipoBusqueda.this);
        });

    }

    private void buscarZona(String zona){
        loadingScreen.show(getSupportFragmentManager(), "Espere...");



        if(!zona.equals("")) {

            String URL = getString(R.string.URL);
            String direccion = URL + "/buscarLugarLico.php?palabra=" + zona;
            final String BACKURL = direccion;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, direccion, (String response) -> {
                try {
                    clear();

                    JSONObject jsonObject = new JSONObject(response);
                    String estado = jsonObject.getString("estado");
                    if (estado.equals("1")) {

                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.custom_toast_success,
                                (ViewGroup) findViewById(R.id.custom_toast_success));

                        TextView men = (TextView) findViewById(R.id.mensaje);
                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();


                        String mensaje = jsonObject.getString("mensaje");
                        JSONArray licores = new JSONArray(mensaje);
                        for (int i = 0; i < licores.length(); i++) {
                            listLicos.add(new Licorerias(licores.getJSONObject(i).getInt("t3l3f0n0"),
                                    licores.getJSONObject(i).getString("n0mbr3L1c"),
                                    licores.getJSONObject(i).getString("d3scr1pc10n"),
                                    licores.getJSONObject(i).getString("ub1c4c10n"),
                                    licores.getJSONObject(i).getString("ub1c4c10nGPS"),
                                    licores.getJSONObject(i).getString("ruta_imagen"),
                                    licores.getJSONObject(i).getString("pr0m0c10n"),
                                    licores.getJSONObject(i).getString("est4d0")));

                        }
                        adapter = new RecyclerViewAdapter(getApplicationContext(), listLicos);
                        adapter.notifyDataSetChanged();
                        recyclerVie.setAdapter(adapter);

                    } else {

                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.custom_toast,
                                (ViewGroup) findViewById(R.id.custom_toast));

                        TextView men = (TextView) findViewById(R.id.mensaje);
                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();

                        adapter = new RecyclerViewAdapter(getApplicationContext(), listLicos);
                        adapter.notifyDataSetChanged();
                        recyclerVie.setAdapter(adapter);
                        clear();

                    }
                    loadingScreen.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingScreen.dismiss();
                }
            }, error -> {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast_error,
                        (ViewGroup) findViewById(R.id.custom_toast_error));

                TextView men = (TextView) findViewById(R.id.mensaje);
                men.setText("Error en servidores, vuelva a intentar");
                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();
                loadingScreen.dismiss();
            });
            SingletonVolley.getInstanciaVolley(getApplicationContext()).addToRequestQueue(stringRequest);

        } else {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast_error,
                    (ViewGroup) findViewById(R.id.custom_toast_error));

            TextView men = (TextView) layout.findViewById(R.id.mensaje);
            men.setText(R.string.ingresePalabra);
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
            loadingScreen.dismiss();
        }
    }

    private void clear(){
        listLicos.clear();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Dialog dialog = new Dialog(tipoBusqueda.this);
        dialog.setContentView(R.layout.custom_menu);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        TextView texto = (TextView)dialog.findViewById(R.id.aviso);
        int id = item.getItemId();


        switch (id){

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

            case android.R.id.home:
                this.finish();
                Animatoo.animateSwipeRight(tipoBusqueda.this);
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
}
