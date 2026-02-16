package com.org.dnbndhu.ui.controller;

import com.org.dnbndhu.ui.MainApp;
import com.org.dnbndhu.ui.MainApp;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class LoginController {

    @FXML
    private PasswordField pinField;

    @FXML

    /*public void handleLogin() {

        String enteredPin = pinField.getText();

        if (enteredPin == null || enteredPin.isBlank()) {
            showAlert("Empty PIN", "Please enter your PIN.");
            return;
        }

        if (enteredPin.equals("1234")) {
            Main.setRoot("dashboard.fxml");
        } else {
            showAlert("Invalid PIN", "Wrong PIN. Try again.");
            pinField.clear();
        }
        }*/

    public void handleLogin() {
        System.out.println("Login button clicked");

        String enteredPin = pinField.getText();
        System.out.println("Entered PIN: " + enteredPin);

        if (enteredPin.equals("1234")) {
            System.out.println("PIN correct, switching scene...");
            MainApp.setRoot("dashboard.fxml");
        } else {
            System.out.println("PIN wrong");
            showAlert("Invalid PIN", "Wrong PIN. Try again.");
            pinField.clear();
        }
    }


    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }


    @FXML private ImageView logoImage;
    
    @FXML
    public void initialize() {
    
        // Load image
        Image img = new Image(
            getClass().getResource("/images/deenabandhu_logo.png").toExternalForm()
        );
        logoImage.setImage(img);
    
        // Clip to circle
        Circle clip = new Circle(70, 70, 70);
        logoImage.setClip(clip);
    }



}
