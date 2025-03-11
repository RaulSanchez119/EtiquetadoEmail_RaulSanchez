package rivera.sanchez.raul.models;

public class Email {
    private final String asunto;
    private final String mensaje;
    private final String etiqueta;

    public Email(String asunto, String mensaje, String etiqueta) {
        this.asunto = asunto;
        this.mensaje = mensaje;
        this.etiqueta = etiqueta;
    }

    public Email(String asunto, String mensaje) {
        this(asunto, mensaje, "");
    }

    public String getAsunto() {
        return asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return String.format("Asunto: %s. Mensaje: %s. Etiquetas: %s.", getAsunto(), getMensaje(), getEtiqueta());
    }
}
