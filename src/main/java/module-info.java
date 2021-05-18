module org.alp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires gs.core;
    requires java.net.http;

    opens org.alp to javafx.fxml;
    opens org.alp.components.controllers to javafx.fxml;
    exports org.alp;
}
