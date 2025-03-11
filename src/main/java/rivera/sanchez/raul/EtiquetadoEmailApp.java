package rivera.sanchez.raul;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import rivera.sanchez.raul.services.AlertService;

public class EtiquetadoEmailApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(EtiquetadoEmailApp.class.getResource("emails_view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Etiquetado Email");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            AlertService.showAlert(Alert.AlertType.ERROR, "*** Error al cargar la vista ***",
                    "Hubo un problema al cargar la interfaz de usuario. Por favor, revise los archivos de configuración o intente de nuevo.\nError: " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }
    public static void main(String[] args) {
        try {
            launch();
        }
        catch (Exception e) {
            AlertService.showAlert(Alert.AlertType.ERROR, "*** Error al iniciar la aplicación ***",
                    "La aplicación no pudo iniciarse correctamente. Verifique su configuración e intente nuevamente.\nError: " + e.getMessage());
        }
    }
}
