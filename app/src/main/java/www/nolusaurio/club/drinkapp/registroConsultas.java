package www.nolusaurio.club.drinkapp;

public class registroConsultas {
    private String nombre, comentario, respuesta, lico;

    public registroConsultas(String nombre, String comentario, String respuesta, String lico) {
        this.nombre = nombre;
        this.comentario = comentario;
        this.respuesta = respuesta;
        this.lico = lico;
    }

    public String getNombre() {
        return nombre;
    }

    public String getComentario() {
        return comentario;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getLico() {
        return lico;
    }

    public void setLico(String lico) {
        this.lico = lico;
    }
}
