module com.example.chatapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.chatapp to javafx.fxml;
    exports com.example.chatapp;
    exports com.example.chatapp.controller;
    opens com.example.chatapp.controller to javafx.fxml;
}