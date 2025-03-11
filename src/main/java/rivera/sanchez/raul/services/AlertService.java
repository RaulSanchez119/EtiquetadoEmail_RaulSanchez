package rivera.sanchez.raul.services;

import javafx.scene.control.Alert;

public class AlertService {

    public static void showAlert(Alert.AlertType tipoAlerta, String titulo, String mensaje) {
        Alert alert = new Alert(tipoAlerta);
        alert.setHeaderText(titulo);
        alert.setTitle(tipoAlerta.toString());
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
