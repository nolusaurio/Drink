package www.nolusaurio.club.drinkapp;

import android.content.Context;
import android.content.SharedPreferences;

public class sharedPreferences {
    SharedPreferences preferences;
    public sharedPreferences(Context context){
        preferences = context.getSharedPreferences("licoreria", Context.MODE_PRIVATE);
    }

    public void guardarRegistroAPP(String codigo){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("registroAPP", codigo);
        editor.commit();

    }

    public String getRegistroAPP(){
        String band = preferences.getString("registroAPP", "0");
        return band;
    }

    public void guardarCodigo(String codigo){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("codigoUT", codigo);
        editor.commit();
    }

    public String getCodigo(){
        String band = preferences.getString("codigoUT", "0");
        return band;
    }

    public void guardarRegistroLic(String codigo){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("registroLicoreria", codigo);
        editor.commit();
    }

    public String getRegistroLic(){
        String band = preferences.getString("registroLicoreria", "0");
        return band;
    }


    public void guardarEstadoSwitch(String codigo){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("estadoSwitch", codigo);
        editor.commit();
    }

    public String getEstadoSwitch(){
        String band = preferences.getString("estadoSwitch", "-1");
        return band;
    }

    public void setRespuestaServidor(String codigo){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("respuestaServidor", codigo);
        editor.commit();
    }

    public String getRespuestaServidor(){
        String res = preferences.getString("respuestaServidor", "0");
        return res;
    }
}
