package www.nolusaurio.club.drinkapp;



public class Licorerias {

    private String nombreLico;
    private String descripcionLico;
    private String ubicacionLico;
    private String ubicacionGPSLico;
    private String promocion;
    private int telefono;
    private int thumbnail;
    private String ruta;
    private String state;



    public Licorerias() {
    }

    public Licorerias(String nombreLico, String descripcionLico, String ubicacionLico, String ubicacionGPSLico, int thumbnail, String promo) {
        this.nombreLico = nombreLico;
        this.descripcionLico = descripcionLico;
        this.ubicacionLico = ubicacionLico;
        this.ubicacionGPSLico = ubicacionGPSLico;
        this.thumbnail = thumbnail;
        this.promocion = promo;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public int getTelefono() {

        return telefono;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {

        return state;
    }

    public Licorerias(int telef, String nombreLico, String descripcionLico, String ubicacionLico,
                      String ubicacionGPSLico, String ruta, String promo, String state) {
        this.nombreLico = nombreLico;
        this.descripcionLico = descripcionLico;
        this.ubicacionLico = ubicacionLico;
        this.ubicacionGPSLico = ubicacionGPSLico;
        this.ruta = ruta;
        this.promocion = promo;
        this.telefono = telef;
        this.state = state;

    }

    public void setNombreLico(String nombreLico) {
        this.nombreLico = nombreLico;
    }

    public void setDescripcionLico(String descripcionLico) {
        this.descripcionLico = descripcionLico;
    }

    public void setUbicacionLico(String ubicacionLico) {
        this.ubicacionLico = ubicacionLico;
    }

    public void setUbicacionGPSLico(String ubicacionGPSLico) {
        this.ubicacionGPSLico = ubicacionGPSLico;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getNombreLico() {
        return nombreLico;
    }

    public String getDescripcionLico() {
        return descripcionLico;
    }

    public String getUbicacionLico() {
        return ubicacionLico;
    }

    public String getUbicacionGPSLico() {
        return ubicacionGPSLico;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getPromocion() {
        return promocion;
    }

    public void setPromocion(String promocion) {
        this.promocion = promocion;
    }
}

