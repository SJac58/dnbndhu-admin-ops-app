package com.org.dnbndhu.ui.controller;

import com.org.dnbndhu.ui.MainApp;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;

public class LoginController {

    @FXML
    private PasswordField pinField;

    // ================= LOGIN =================
    @FXML
    public void handleLogin() {

        String enteredPin = pinField.getText();

        if (enteredPin == null || enteredPin.isBlank()) {
            showAlert("Empty PIN", "Please enter your PIN.");
            return;
        }

        if (enteredPin.equals("1234")) {
            MainApp.setRoot("dashboard.fxml");
        } else {
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

    @FXML
    public void initialize() {
        // Nothing needed here anymore.
        // Image is loaded directly from FXML.
    }
}
