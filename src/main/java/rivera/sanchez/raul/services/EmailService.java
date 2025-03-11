package rivera.sanchez.raul.services;


import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import javafx.scene.control.Alert;
import rivera.sanchez.raul.models.Email;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class EmailService {
    private Folder[] carpetasTotales;
    private Folder[] carpetasFiltradas;

    private Session obtenerSesion() {
        Properties propiedades = new Properties();
        propiedades.setProperty("mail.store.protocol", "imap");
        propiedades.setProperty("mail.imap.host", "imap.gmail.com");
        propiedades.setProperty("mail.imap.port", "993");
        propiedades.setProperty("mail.imap.ssl.enable", "true");
        propiedades.setProperty("mail.imap.ssl.trust", "imap.gmail.com");
        return Session.getDefaultInstance(propiedades);
    }

    public List<Email> obtenerCorreos(String correo, String contrasena, boolean incluirEtiquetas) throws Exception {
        List<Email> listaCorreos = new ArrayList<>();
        Store conexion = obtenerSesion().getStore("imaps");
        conexion.connect("imap.gmail.com", 993, correo, contrasena);

        Folder bandejaEntrada = conexion.getDefaultFolder().getFolder("INBOX");
        bandejaEntrada.open(Folder.READ_ONLY);

        if(incluirEtiquetas) {
            carpetasTotales = conexion.getDefaultFolder().list("*");
            carpetasFiltradas = Arrays.stream(carpetasTotales)
                    .filter(c ->  c.getName().trim().equalsIgnoreCase("Task.Done")
                            || c.getName().trim().equalsIgnoreCase("Work.in.progress")
                            || c.getName().trim().equalsIgnoreCase("To.be.done")
                    ).toArray(Folder[]::new);
        }

        Message[] mensajes = bandejaEntrada.getMessages();
        int cantidad = Math.min(mensajes.length, 7);
        List<Message> listaMensajes = new ArrayList<>(Arrays.asList(mensajes).subList(0, cantidad));
        for (Message mensaje : listaMensajes) {
            String asunto = mensaje.getSubject();
            String contenido = "(Mensaje no disponible)";

            if (mensaje.getContent() instanceof MimeMultipart mimeMultipart) {
                if (mimeMultipart.getBodyPart(0).isMimeType("text/plain")) {
                    contenido = mimeMultipart.getBodyPart(0).getContent().toString();
                }
            }

            if(incluirEtiquetas) {
                try {
                    List<String> etiquetas = new ArrayList<>();
                    for (Folder carpeta : carpetasFiltradas) {
                        if(carpeta.exists() && carpeta.getMessageCount() > 0) {
                            if(estaMensajeEnCarpeta(mensaje, carpeta)) {
                                etiquetas.add(carpeta.getName());
                            }
                        }
                    }

                    listaCorreos.add(new Email(asunto, contenido, String.join(", ", etiquetas)));
                }
                catch (Exception e) {
                    AlertService.showAlert(Alert.AlertType.ERROR, "*** Error al leer las etiquetas ***", "Error: " + e.getMessage());
                }
            }
            else listaCorreos.add(new Email(asunto, contenido));
        }

        bandejaEntrada.close(false);
        conexion.close();
        return listaCorreos;
    }

    private boolean estaMensajeEnCarpeta(Message mensaje, Folder carpeta) {
        try {
            if(carpeta instanceof IMAPFolder carpetaIMAP) {
                carpetaIMAP.open(Folder.READ_ONLY);
                String uidMensaje = ((IMAPMessage) mensaje).getMessageID();
                Message[] mensajesCarpeta = carpetaIMAP.getMessages();
                for(Message mensajeCarpeta : mensajesCarpeta) {
                    String uidMensajeCarpeta = ((IMAPMessage) mensajeCarpeta).getMessageID();
                    if(uidMensaje.equals(uidMensajeCarpeta)) {
                        carpetaIMAP.close(false);
                        return true;
                    }
                }
                carpetaIMAP.close(false);
            }
        } catch (Exception e) {
            AlertService.showAlert(Alert.AlertType.ERROR, "*** Error al verificar las etiquetas de un correo ***", "Error: " + e.getMessage());
        }
        return false;
    }
}
