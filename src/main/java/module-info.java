module com.st4s1k.leagueteamcomp {
    requires static lombok;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.google.gson;
    requires java.net.http;

    opens com.st4s1k.leagueteamcomp to javafx.fxml, com.google.gson;
    exports com.st4s1k.leagueteamcomp;
    opens com.st4s1k.leagueteamcomp.model to javafx.fxml, com.google.gson;
    exports com.st4s1k.leagueteamcomp.model;
    opens com.st4s1k.leagueteamcomp.service to javafx.fxml, com.google.gson;
    exports com.st4s1k.leagueteamcomp.service;
    opens com.st4s1k.leagueteamcomp.controller to javafx.fxml, com.google.gson;
    exports com.st4s1k.leagueteamcomp.controller;
    exports com.st4s1k.leagueteamcomp.model.champion;
    opens com.st4s1k.leagueteamcomp.model.champion to com.google.gson, javafx.fxml;
    exports com.st4s1k.leagueteamcomp.model.champion.enums;
    opens com.st4s1k.leagueteamcomp.model.champion.enums to com.google.gson, javafx.fxml;
}