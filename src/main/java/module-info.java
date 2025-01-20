module com.hospital {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.sql;
    requires itextpdf;
    requires jakarta.mail;
    requires jakarta.activation;
    requires java.base;
    
    exports com.hospital;
    exports com.hospital.views;
    exports com.hospital.models;
    exports com.hospital.controllers;
    exports com.hospital.services;
    exports com.hospital.utils;
    exports com.hospital.dao;
} 