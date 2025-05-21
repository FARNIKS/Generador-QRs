module com.farniks.qrgeneratorscanner {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.google.zxing.javase;
    requires com.google.zxing;


    opens com.farniks.qrgeneratorscanner to javafx.fxml;
    exports com.farniks.qrgeneratorscanner;
}