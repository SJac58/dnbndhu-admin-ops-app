package com.org.dnbndhu.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.application.Platform;

import com.org.dnbndhu.infrastructure.db.SQLiteConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardController {

    @FXML private PieChart demographicsChart;
    @FXML private BarChart<Number, String> participationChart;
    @FXML private LineChart<String, Number> enrollmentChart;
    @FXML private LineChart<String, Number> programComparisonChart;
    @FXML private Label totalStudentsLabel;
    @FXML private VBox educationBox;

    @FXML
    public void initialize() {
        try {
            loadTotalStudents();
            loadProgramParticipation();
            loadGenderChart();
            loadAgeDistribution();
            loadEnrollmentTrend();
            loadProgramComparison();
        } catch (Exception e) {
            System.err.println("Dashboard Init Error: " + e.getMessage());
        }
    }

    // ================= TOTAL STUDENTS =================
    private void loadTotalStudents() throws Exception {
        Map<String, Integer> map = queryStudentsPerProgram();
        int total = map.values().stream().mapToInt(Integer::intValue).sum();
        totalStudentsLabel.setText(String.valueOf(total));
    }

    // ================= PROGRAM PARTICIPATION =================
    private void loadProgramParticipation() throws Exception {

        Map<String, Integer> programCounts = queryStudentsPerProgram();
        participationChart.getData().clear();

        for (Map.Entry<String, Integer> e : programCounts.entrySet()) {

            XYChart.Series<Number, String> series = new XYChart.Series<>();
            series.setName(e.getKey());
            series.getData().add(new XYChart.Data<>(e.getValue(), e.getKey()));

            participationChart.getData().add(series);
        }

        participationChart.setLegendVisible(false);
        participationChart.setAnimated(true);

        Platform.runLater(() -> {
            for (XYChart.Series<Number, String> s : participationChart.getData()) {
                for (XYChart.Data<Number, String> d : s.getData()) {
                    Tooltip.install(d.getNode(),
                            new Tooltip(d.getYValue() + " : " + d.getXValue()));
                }
            }
        });
    }

    // ================= GENDER PIE =================
    private void loadGenderChart() throws Exception {

        demographicsChart.getData().clear();
        Map<String, Integer> map = queryGenderCounts();

        for (Map.Entry<String, Integer> e : map.entrySet()) {
            demographicsChart.getData()
                    .add(new PieChart.Data(e.getKey(), e.getValue()));
        }

        demographicsChart.setLegendVisible(true);
        demographicsChart.setAnimated(true);

        Platform.runLater(() -> {
            for (PieChart.Data d : demographicsChart.getData()) {
                Tooltip.install(d.getNode(),
                        new Tooltip(d.getName() + " : " + (int) d.getPieValue()));
            }
        });
    }

    // ================= AGE DISTRIBUTION =================
    private void loadAgeDistribution() throws Exception {

        educationBox.getChildren().clear();

        Map<String, Integer> bins = queryAgeBins();

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Age Group");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Students");

        AreaChart<String, Number> ageChart =
                new AreaChart<>(xAxis, yAxis);

        ageChart.setLegendVisible(false);
        ageChart.setAnimated(true);
        ageChart.setCreateSymbols(false);
        ageChart.setPrefHeight(350);

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (Map.Entry<String, Integer> e : bins.entrySet()) {
            series.getData().add(
                    new XYChart.Data<>(e.getKey(), e.getValue()));
        }

        ageChart.getData().add(series);

        Platform.runLater(() -> {
            for (XYChart.Data<String, Number> d : series.getData()) {
                if (d.getNode() != null) {
                    Tooltip.install(d.getNode(),
                            new Tooltip(d.getXValue() + " : " + d.getYValue()));
                }
            }
        });

        educationBox.getChildren().add(ageChart);
    }

    // ================= ENROLLMENT TREND =================
    private void loadEnrollmentTrend() throws Exception {

        enrollmentChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Map<String, Integer> trend = queryEnrollmentTrend();

        for (Map.Entry<String, Integer> e : trend.entrySet()) {
            series.getData().add(
                    new XYChart.Data<>(e.getKey(), e.getValue()));
        }

        enrollmentChart.getData().add(series);

        enrollmentChart.setLegendVisible(false);
        enrollmentChart.setAnimated(true);
        enrollmentChart.setCreateSymbols(true);

        Platform.runLater(() -> {
            for (XYChart.Data<String, Number> d : series.getData()) {
                if (d.getNode() != null) {
                    Tooltip.install(d.getNode(),
                            new Tooltip(d.getXValue() + " : " + d.getYValue()));
                }
            }
        });
    }

    // ================= PROGRAM COMPARISON =================
    private void loadProgramComparison() throws Exception {

        programComparisonChart.getData().clear();

        String sql = """
            SELECT DATE(s.enrollment_timestamp) as dt,
                   p.program_name,
                   COUNT(*) as cnt
            FROM students s
            JOIN batches b ON s.batch_id=b.batch_id
            JOIN programs p ON b.program_id=p.program_id
            GROUP BY dt, p.program_name
            ORDER BY dt
        """;

        Map<String, XYChart.Series<String, Number>> map = new LinkedHashMap<>();

        try (Connection conn = SQLiteConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String date = rs.getString("dt");
                String program = rs.getString("program_name");
                int count = rs.getInt("cnt");

                map.putIfAbsent(program, new XYChart.Series<>());
                map.get(program).setName(program);
                map.get(program).getData()
                        .add(new XYChart.Data<>(date, count));
            }
        }

        programComparisonChart.getData().addAll(map.values());
        programComparisonChart.setAnimated(true);

        Platform.runLater(() -> {
            for (XYChart.Series<String, Number> s : programComparisonChart.getData()) {
                for (XYChart.Data<String, Number> d : s.getData()) {
                    if (d.getNode() != null) {
                        Tooltip.install(d.getNode(),
                                new Tooltip(s.getName() + " | "
                                        + d.getXValue() + " : "
                                        + d.getYValue()));
                    }
                }
            }
        });
    }

    // ================= DATABASE =================

    private Map<String, Integer> queryStudentsPerProgram() throws Exception {
        String sql = """
            SELECT p.program_name, COUNT(s.student_id) as cnt
            FROM students s
            JOIN batches b ON s.batch_id=b.batch_id
            JOIN programs p ON b.program_id=p.program_id
            GROUP BY p.program_id
            ORDER BY p.program_id
        """;

        Map<String, Integer> map = new LinkedHashMap<>();

        try (Connection conn = SQLiteConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getString("program_name"),
                        rs.getInt("cnt"));
            }
        }

        return map;
    }

    private Map<String, Integer> queryGenderCounts() throws Exception {
        String sql = """
            SELECT COALESCE(gender,'Other') as g,
                   COUNT(*) as cnt
            FROM students
            GROUP BY g
        """;

        Map<String, Integer> map = new LinkedHashMap<>();

        try (Connection conn = SQLiteConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getString("g"),
                        rs.getInt("cnt"));
            }
        }

        return map;
    }

    private Map<String, Integer> queryAgeBins() throws Exception {
        String sql = """
            SELECT
            CASE
                WHEN age<18 THEN '<18'
                WHEN age BETWEEN 18 AND 24 THEN '18-24'
                WHEN age BETWEEN 25 AND 34 THEN '25-34'
                WHEN age BETWEEN 35 AND 44 THEN '35-44'
                ELSE '45+'
            END as bin,
            COUNT(*) as cnt
            FROM students
            GROUP BY bin
            ORDER BY bin
        """;

        Map<String, Integer> map = new LinkedHashMap<>();

        try (Connection conn = SQLiteConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getString("bin"),
                        rs.getInt("cnt"));
            }
        }

        return map;
    }

    private Map<String, Integer> queryEnrollmentTrend() throws Exception {
        String sql = """
            SELECT DATE(enrollment_timestamp) as dt,
                   COUNT(*) as cnt
            FROM students
            GROUP BY dt
            ORDER BY dt
        """;

        Map<String, Integer> map = new LinkedHashMap<>();

        try (Connection conn = SQLiteConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getString("dt"),
                        rs.getInt("cnt"));
            }
        }

        return map;
    }
}