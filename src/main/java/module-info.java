module com.example.javamongodb {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;


    opens com.example.javamongodb to javafx.fxml;
    exports com.example.javamongodb;
}