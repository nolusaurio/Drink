package www.nolusaurio.club.drinkapp;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;

import java.util.ArrayList;


public class Adaptador_registros extends RecyclerView.Adapter<Adaptador_registros.registrosViewHolder> implements
        View.OnClickListener {
    Context context;

    private ArrayList<registroConsultas> regigstros;
    private View.OnClickListener listener;


    public Adaptador_registros(ArrayList<registroConsultas> regigstros) {
        this.regigstros = regigstros;
    }

    @Override
    public registrosViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filas_registros, viewGroup, false);
        registrosViewHolder reg = new registrosViewHolder(v);
        v.setOnClickListener(this);
        context = viewGroup.getContext();
        return reg;
    }

    @Override
    public void onBindViewHolder(final registrosViewHolder registrosViewHolder, int i) {
        registrosViewHolder.nombre.setText(regigstros.get(i).getNombre());
        registrosViewHolder.comentario.setText(regigstros.get(i).getComentario());

        Log.w("ADAPTADOOOOOORRRRRRR", regigstros.get(i).getRespuesta());
        if (regigstros.get(i).getRespuesta().isEmpty() || regigstros.get(i).getRespuesta().equals("") ||
                regigstros.get(i).getRespuesta().equals(null) || regigstros.get(i).getRespuesta().equals("null")) {
            registrosViewHolder.respuesta.setText("Sin respuesta");
        } else {
            registrosViewHolder.respuesta.setText(regigstros.get(i).getRespuesta());
        }
        registrosViewHolder.lico.setText(regigstros.get(i).getLico());

    }

    @Override
    public int getItemCount() {
        return regigstros.size();
    }


    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    public class registrosViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, comentario, respuesta, lico;
        Button responder;

        public registrosViewHolder(View itemView) {
            super(itemView);
            nombre = (TextView) itemView.findViewById(R.id.consulta_nombre);
            comentario = (TextView) itemView.findViewById(R.id.consulta_consulta);
            respuesta = (TextView) itemView.findViewById(R.id.consulta_respuesta);
            lico = (TextView) itemView.findViewById(R.id.idLico);
            responder = (Button) itemView.findViewById(R.id.btn_responder);

            responder.setOnClickListener(v -> {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View promptView = layoutInflater.inflate(R.layout.dialogorespuestalicoreria, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setView(promptView);
                EditText respuesta = (EditText) promptView.findViewById(R.id.respuestaLicoreria);

                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("Enviar", (dialog, id) -> {


                            String answer = moderar(respuesta.getText().toString().trim());

                            if (answer.isEmpty() || answer.equals("") || answer.equals(null) || answer.equals("null"))
                                answer = "Sin respuesta";

                            aniadirComentario(nombre.getText().toString().trim(),
                                    answer,
                                    lico.getText().toString().trim(),
                                    comentario.getText().toString().trim());
                        })
                        .setNegativeButton("Cancelar",
                                (dialog, id) -> dialog.cancel());

                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            });

        }

        private String moderar(String palabra) {
            String ret = "";
            String moderacion = "puta PUTA puto PUTO put4 PUT4 put0 PUT0 " +
                    "zorra ZORRA z0rr4 Z0RR4 mierda MIERDA m13rd4 M13RD4 " +
                    "pendejo PENDEJO p3nd3jo P3ND3J0 perra PERRA p3rr4 P3RR4 " +
                    "pene PENE p3n3 P3N3";

            String[] mod = moderacion.split("\\s+");
            for (String pal : mod) {
                if (palabra.equals(pal)) {
                    ret = "palabra moderada por admin";
                    break;
                } else {
                    ret = palabra;
                }
            }
            return ret;
        }


        private void aniadirComentario(String nomCli, String answer, String lico, String com) {
            respuesta.setText(answer);


            String URL = context.getString(R.string.URL);
            String envio = URL + "/instResp.php?nombreLic=" + lico + "&respuesta=" + answer + "&nombreCli=" + nomCli +
                    "&comentario=" + com;
            envio = envio.replaceAll(" ", "%20");
            Log.w("ENVIANDO RES:", envio);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, envio, null, response -> {
                try {
                    String resulJSON = response.getString("estado");
                    Log.w("ENVIANDO COM JSON", resulJSON);
                    if (resulJSON.equals("1")) {
                        Toast.makeText(context, "Respuesta enviada", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Vuelva a intentar", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error a intentar", Toast.LENGTH_SHORT).show();

                }

            }, error -> {

            });
            SingletonVolley.getInstanciaVolley(context).addToRequestQueue(request);


        }


    }
}
