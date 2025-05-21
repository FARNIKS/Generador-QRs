module com.farniks.qrgeneratorscanner {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javase;


    opens com.farniks.qrgeneratorscanner to javafx.fxml;
    exports com.farniks.qrgeneratorscanner;
}