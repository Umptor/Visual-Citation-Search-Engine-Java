module org.alp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires gs.core;
    requires java.net.http;
    requires com.google.gson;
    requires gs.ui.javafx;

    opens org.alp to javafx.fxml;

    opens org.alp.components.controllers to javafx.base, javafx.fxml;
    opens org.alp.models to javafx.base, javafx.fxml, com.google.gson;

    opens org.alp.models.crossrefApi to com.google.gson;
    opens org.alp.models.crossrefApi.getWorksResponse to com.google.gson;
    opens org.alp.models.crossrefApi.getMetaDataResponse to com.google.gson;
    opens org.alp.models.OpenCitationApi to com.google.gson;

    exports org.alp;
}
