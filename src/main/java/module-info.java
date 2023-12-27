module com.example.uap {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.uap to javafx.fxml;
    exports com.example.uap;
}