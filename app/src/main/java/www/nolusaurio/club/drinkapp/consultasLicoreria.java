package www.nolusaurio.club.drinkapp;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class consultasLicoreria extends AppCompatActivity {
    private RecyclerView recycler;
    private ArrayList<registroConsultas> registros;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adaptador;
    final DialogFragment loadingScreen = LoadingScreen.getInstance();



    String nombreLico = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultas_licoreria);
        Bundle bundle = getIntent().getExtras();
        recycler = (RecyclerView) findViewById(R.id.recyclerConsultas);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        registros = new ArrayList<registroConsultas>();


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        if (bundle != null) {
            nombreLico = bundle.getString("nombreLicoreria").trim();
            getData(nombreLico);

        }
    }

    private void getData(String nombreLico) {
        String URL = getString(R.string.URL);
        String url = URL + "/getCom.php?nombreLic=" + nombreLico;

        Log.w("CONSULTALICOS COMEN:", url);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        loadingScreen.show(getSupportFragmentManager(), "Espere...");


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String resultJSON = response.getString("estado");
                        Log.w("CONSULTAS RESULJO", resultJSON);
                        if (resultJSON.equals("1")) {
                            JSONArray registrosJSON = response.getJSONArray("mensaje");


                            if (registrosJSON.length() > 0) {
                                for (int i = 0; i < registrosJSON.length(); i++) {
                                    registros.add(new registroConsultas(registrosJSON.getJSONObject(i).getString("n0mbr3c0ns4ltant3"),
                                            registrosJSON.getJSONObject(i).getString("c0ns4lt4c0ns4lt4"),
                                            registrosJSON.getJSONObject(i).getString("r3sp43st4"),
                                            registrosJSON.getJSONObject(i).getString("n0mbr3l1c0")));
                                }
                                recycler.setLayoutManager(layoutManager);
                                adaptador = new Adaptador_registros(registros);
                                loadingScreen.dismiss();

                                recycler.setAdapter(adaptador);
                            } else {
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.custom_toast_sincomentarios,
                                        (ViewGroup) findViewById(R.id.custom_toast_sinco));

                                Toast toast = new Toast(getApplicationContext());
                                toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(layout);
                                toast.show();
                                loadingScreen.dismiss();
                            }
                        } else {
                            Toast.makeText(consultasLicoreria.this, "Error de red, consulte nuevamente", Toast.LENGTH_SHORT).show();
                            loadingScreen.dismiss();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        loadingScreen.dismiss();

                    }

                },
                error -> {
                    Log.e("ERROR=====>", error.toString());
                    loadingScreen.dismiss();

                });
        requestQueue.add(request);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cli, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateFade(consultasLicoreria.this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Dialog dialog = new Dialog(consultasLicoreria.this);
        dialog.setContentView(R.layout.custom_menu);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        TextView texto = (TextView) dialog.findViewById(R.id.aviso);
        int id = item.getItemId();


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


            case android.R.id.home:
                this.finish();
                Animatoo.animateFade(consultasLicoreria.this);
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

}
