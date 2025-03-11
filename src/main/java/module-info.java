module rivera.sanchez.raul.etiquetadoemail_raulsanchez {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.mail;
    requires java.desktop;

    opens rivera.sanchez.raul to javafx.fxml;

    exports rivera.sanchez.raul;

    exports rivera.sanchez.raul.models;
    opens rivera.sanchez.raul.models to javafx.fxml;
    exports rivera.sanchez.raul.controllers;
    opens rivera.sanchez.raul.controllers to javafx.fxml;
    exports rivera.sanchez.raul.services;
    opens rivera.sanchez.raul.services to javafx.fxml;
}
