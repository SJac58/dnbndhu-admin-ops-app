package com.org.dnbndhu.ui.controller;


import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;


public class DashboardController {

    @FXML
    private PieChart demographicsChart;

    @FXML
    private BarChart<String, Number> participationChart;

    @FXML
    private LineChart<String, Number> enrollmentChart;

    @FXML
    private Label totalStudentsLabel;
    
    @FXML
    private VBox educationBox;

    @FXML
    private VBox communityBox;

    @FXML
    private VBox volunteerBox;


    @FXML
    public void initialize() {

        // ⚠️ guard against nulls during UI refactor
        if (demographicsChart != null) {
            demographicsChart.getData().addAll(
                new PieChart.Data("Female", 58),
                new PieChart.Data("Male", 32),
                new PieChart.Data("Other", 10)
            );
        }

        if (participationChart != null) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.getData().add(new XYChart.Data<>("Jan", 40));
            series.getData().add(new XYChart.Data<>("Feb", 60));
            series.getData().add(new XYChart.Data<>("Mar", 85));
            participationChart.getData().add(series);
        }

        if (enrollmentChart != null) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.getData().add(new XYChart.Data<>("Jan", 40));
            series.getData().add(new XYChart.Data<>("Feb", 55));
            series.getData().add(new XYChart.Data<>("Mar", 35));
            enrollmentChart.getData().add(series);
        }

        if (totalStudentsLabel != null) {
            totalStudentsLabel.setText("1248");
        }
    }
}
