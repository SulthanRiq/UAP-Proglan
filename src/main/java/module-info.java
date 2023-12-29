module com.example.uap {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.junit.jupiter.api;


    opens com.example.uap to javafx.fxml;
    exports com.example.uap;
}