package www.nolusaurio.club.drinkapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;


import java.io.ByteArrayOutputStream;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.myViewHolder> {

    private Context context;
    private List<Licorerias> mData;

    private static String TAG = "RECYCLERVIEW";


    public RecyclerViewAdapter(Context context, List<Licorerias> mData) {
        Log.e(TAG, "aca");
        this.context = context;
        this.mData = mData;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(context);
        view = mInflater.inflate(R.layout.cardview_item_lico, viewGroup, false);
        return new myViewHolder(view);
    }


    @Override
    public void onBindViewHolder(myViewHolder myViewHolder, int i) {


        String URL = context.getString(R.string.URL);
        String URL_IMAGEN = URL+"/";

        String BACK_URL = URL_IMAGEN;
        String nombre = "";
        String descripcion = "";
        String ubicacion = "";
        String promocion = "";
        String ubiGPS = "";
        int tel = 0;
        String state = "";

        myViewHolder.tv_licoreria_title.setText(mData.get(i).getNombreLico());
        Log.w(TAG, "Obteniendo valores sufigo i"+(i));

        nombre = mData.get(i).getNombreLico();
        descripcion = mData.get(i).getDescripcionLico();
        ubicacion = mData.get(i).getUbicacionLico();
        promocion = mData.get(i).getPromocion();
        ubiGPS = mData.get(i).getUbicacionGPSLico();
        tel = mData.get(i).getTelefono();
        state = mData.get(i).getState();

        final ByteArrayOutputStream bs = new ByteArrayOutputStream();

        Log.w(TAG, "Obteniendo valores");
        Log.w(TAG, nombre + "," + descripcion + "," + ubicacion + "," + promocion + "," + ubiGPS);
        final Intent ii = new Intent(context, VIstaLic.class);
        ii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ii.putExtra("nombre", nombre);
        ii.putExtra("descripcion", descripcion);
        ii.putExtra("ubicacion", ubicacion);
        ii.putExtra("promo", promocion);
        ii.putExtra("ubiGPS", ubiGPS);
        ii.putExtra("telefono", tel);
        ii.putExtra("estado", state);
        ii.putExtra("posicion",mData.get(i).getRuta());

        Log.e("RECYCLERVIEW=======>", promocion);
        URL_IMAGEN = URL_IMAGEN + mData.get(i).getRuta();
        Log.w(TAG, "IMAGEN:"+mData.get(i).getRuta());

        URL_IMAGEN = URL_IMAGEN.replace(" ", "%20").trim();
        ImageRequest imageRequest = new ImageRequest(URL_IMAGEN, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                myViewHolder.img_licoreria.setImageBitmap(response);
                response.compress(Bitmap.CompressFormat.JPEG, 50, bs);
                byte[] bytes = bs.toByteArray();
                ii.putExtra("img", bytes);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(context, "ERROR AL CARGAR", Toast.LENGTH_SHORT).show();
            }
        });
        SingletonVolley.getInstanciaVolley(context.getApplicationContext()).addToRequestQueue(imageRequest);


        URL_IMAGEN = BACK_URL;


        myViewHolder.cardView.setOnClickListener(v -> {

            Log.e(TAG, "onclick");
            context.startActivity(ii);
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {

        TextView tv_licoreria_title;
        ImageView img_licoreria;
        CardView cardView;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);


            tv_licoreria_title = (TextView) itemView.findViewById(R.id.nomLicoreria);
            img_licoreria = (ImageView) itemView.findViewById(R.id.fotoLicoreria);
            cardView = (CardView) itemView.findViewById(R.id.cardview_id);


        }
    }


}
