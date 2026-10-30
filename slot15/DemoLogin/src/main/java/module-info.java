module com.fu.demologin {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;


    opens com.fu.demologin to javafx.fxml;
    exports com.fu.demologin;
}