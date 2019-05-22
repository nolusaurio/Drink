package www.nolusaurio.club.drinkapp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class SingletonVolley {

    private static SingletonVolley instanciaVolley;
    private RequestQueue request;
    private static Context context;

    private SingletonVolley(Context con){
        context = con;
        request = getRequestQueue();
    }


    public static synchronized SingletonVolley getInstanciaVolley(Context context){
        if(instanciaVolley == null){
            instanciaVolley = new SingletonVolley(context);
        }
        return instanciaVolley;
    }

    public RequestQueue getRequestQueue(){
        if(request == null){
            request = Volley.newRequestQueue(context.getApplicationContext());
        }
        return request;
    }

    public <T> void addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }

}
