package rivera.sanchez.raul.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import rivera.sanchez.raul.models.Email;
import rivera.sanchez.raul.services.EmailService;
import rivera.sanchez.raul.services.AlertService;  // Importa la clase AlertService

import java.util.List;

public class EmailController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ListView<String> emailListView;

    @FXML
    private TextArea textArea;

    @FXML
    private ProgressIndicator progressIndicator;

    private EmailService emailService = new EmailService();

    @FXML
    private void onMostrarCorreosEtiquetadosClick() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            AlertService.showAlert(Alert.AlertType.ERROR, "Error", "Por favor, ingrese su correo y contraseña.");
            return;
        }

        progressIndicator.setVisible(true);

        Task<List<Email>> cargarCorreosTask = new Task<List<Email>>() {
            @Override
            protected List<Email> call() throws Exception {
                return emailService.obtenerCorreos(email, password, true);
            }
        };

        cargarCorreosTask.setOnSucceeded(event -> {
            List<Email> emails = cargarCorreosTask.getValue();

            ObservableList<String> emailList = FXCollections.observableArrayList();
            for (Email mail : emails) {
                emailList.add(mail.getAsunto() + " | Etiqueta: " + mail.getEtiqueta());
            }
            emailListView.setItems(emailList);

            progressIndicator.setVisible(false);
        });

        cargarCorreosTask.setOnFailed(event -> {
            progressIndicator.setVisible(false);
            AlertService.showAlert(Alert.AlertType.ERROR, "Error", "No se pudieron obtener los correos. " + cargarCorreosTask.getException().getMessage());
        });

        new Thread(cargarCorreosTask).start();
    }

    @FXML
    private void onEmailSelected(MouseEvent event) {
        String selectedEmail = emailListView.getSelectionModel().getSelectedItem();

        if (selectedEmail != null) {
            String[] parts = selectedEmail.split(" - ");
            String subject = parts[0];

            String email = emailField.getText();
            String password = passwordField.getText();

            Task<Email> cargarContenidoTask = new Task<Email>() {
                @Override
                protected Email call() throws Exception {
                    List<Email> emails = emailService.obtenerCorreos(email, password, false);
                    for (Email mail : emails) {
                        System.out.println("Comparando: " + mail.getAsunto() + " con " + subject);  // Debugging
                        if (mail.getAsunto().equals(subject)) {
                            return mail;
                        }
                    }
                    return null;
                }
            };

            cargarContenidoTask.setOnSucceeded(event2 -> {
                Email correo = cargarContenidoTask.getValue();
                if (correo != null) {
                    System.out.println("Correo encontrado: " + correo.getAsunto());  // Debugging
                    textArea.setText(correo.getMensaje());
                } else {
                    AlertService.showAlert(Alert.AlertType.WARNING, "Correo no encontrado", "No se encontró el contenido del correo.");
                }
            });

            cargarContenidoTask.setOnFailed(event2 -> {
                AlertService.showAlert(Alert.AlertType.ERROR, "Error", "No se pudo obtener el contenido del correo.");
            });

            new Thread(cargarContenidoTask).start();
        }
    }
}
