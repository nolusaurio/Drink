package www.nolusaurio.club.drinkapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.List;

public class MapaLicoreria extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {
    private MapView mapView;
    private MapboxMap map;
    CameraPosition cameraPosition;
    private static String TAG ="MAPALICORERIA";

    double[]n;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        Mapbox.getInstance(this, getString(R.string.token));



        setContentView(R.layout.activity_mapa_licoreria);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {
            String coordenadas = bundle.getString("gps");
            Log.w(TAG, coordenadas);



            double[] vect = coordenadasToDouble(coordenadas);
            camara(vect[0], vect[1]);

            mapView.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
                byte[] bytes = bundle.getByteArray("imagen");
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                bm = redimensionarImagen(bm, 100, 100);

                style.addImage("marker-icon-id", bm);

                GeoJsonSource geoJsonSource = new GeoJsonSource("source-id",
                        Feature.fromGeometry(
                                Point.fromLngLat( getLong(),getLat())
                        ));
                style.addSource(geoJsonSource);

                SymbolLayer symbolLayer = new SymbolLayer("layer-id",
                        "source-id");
                symbolLayer.withProperties(
                        PropertyFactory.iconImage("marker-icon-id"),
                        PropertyFactory.iconIgnorePlacement(true),
                        PropertyFactory.iconAllowOverlap(true)
                );
                style.addLayer(symbolLayer);

                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),1500);


            }));


        }else{
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast_success,
                    (ViewGroup) findViewById(R.id.custom_toast_success));

            TextView men = (TextView) findViewById(R.id.mensaje);
            men.setText("No existe lugar");
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }


    }

    private double[] coordenadasToDouble(String num){

        String[] numeros = num.split(",");
        n = new double[numeros.length];
        for(int i=0;i< n.length;i++){
            n[i] = Double.parseDouble(numeros[i]);
        }
        return n;
    }

    private double getLat(){
        return n[0];
    }

    private double getLong(){
        return n[1];
    }


    private CameraPosition camara(double lat, double lngg){
        cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(lat, lngg))
                .zoom(17)
                .tilt(20)
                .build();
        return cameraPosition;
    }


    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        map = mapboxMap;
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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            this.finish();
            Animatoo.animateSplit(MapaLicoreria.this);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSplit(MapaLicoreria.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {

    }
}
