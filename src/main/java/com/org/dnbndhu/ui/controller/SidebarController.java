package com.org.dnbndhu.ui.controller;

import com.org.dnbndhu.ui.MainApp;

import javafx.fxml.FXML;

public class SidebarController {

    @FXML
    private void goDashboard() {
        MainApp.setRoot("dashboard.fxml");
    }

    @FXML
    private void goStudents() {
        MainApp.setRoot("ViewMyStudents.fxml");
    }

    @FXML
    private void goAttendance() {
        MainApp.setRoot("attendance.fxml");
    }

    @FXML
    private void goEnroll() {
        MainApp.setRoot("EnrollNewStudent.fxml");
    }

    @FXML
    private void goAnalytics() {
        MainApp.setRoot("PlacementPortal.fxml");
    }

   
}
