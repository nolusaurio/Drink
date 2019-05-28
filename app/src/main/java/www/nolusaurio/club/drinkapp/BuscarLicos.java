package www.nolusaurio.club.drinkapp;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import com.android.volley.toolbox.StringRequest;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BuscarLicos extends AppCompatActivity {

    ArrayList<Licorerias> listLicos;
    private RecyclerView recyclerVie;
    private RecyclerView.Adapter adapter;

    private GridLayoutManager lManager;
    private static String TAG = "BuscarLicos";

    private Button upd;

    final DialogFragment loadingScreen = LoadingScreen.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_licos);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerVie = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerVie.setHasFixedSize(true);
        lManager = new GridLayoutManager(this, 3);
        ((GridLayoutManager) lManager).setOrientation(GridLayoutManager.VERTICAL);
        recyclerVie.setLayoutManager(lManager);
        upd = (Button) findViewById(R.id.update);


        cargarLicos();

        upd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarLicos();
            }
        });

    }

    private void cargarLicos() {

        loadingScreen.show(getSupportFragmentManager(), "Espere...");

        String URL = getString(R.string.URL);
        String URL_GET = URL + "/getLicorerias.php?estado=1";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GET, response -> {
            listLicos = new ArrayList<Licorerias>();
            try {
                JSONObject jsonObject = new JSONObject(response);
                String estado = jsonObject.getString("estado");
                if (estado.equals("1")) {

                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.custom_toast_success,
                            (ViewGroup) findViewById(R.id.custom_toast_success));

                    TextView men = (TextView) findViewById(R.id.mensaje);
                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
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
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
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
            Toast.makeText(getApplicationContext(), R.string.errorDesconexion, Toast.LENGTH_SHORT).show();
            loadingScreen.dismiss();

        });
        SingletonVolley.getInstanciaVolley(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    private void clear() {
        listLicos.clear();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            Animatoo.animateShrink(BuscarLicos.this);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateShrink(BuscarLicos.this);
    }
}
