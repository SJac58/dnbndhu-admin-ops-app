package com.org.dnbndhu.ui.controller;

import java.util.ArrayList;
import java.util.List;

import com.org.dnbndhu.domain.dto.StudentDTO;
import com.org.dnbndhu.repository.StudentRepository;
import com.org.dnbndhu.ui.MainApp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class ViewMyStudentsController {

    @FXML private VBox rowsBox;
    @FXML private GridPane headerGrid;
    @FXML private ToggleButton punkToggle;
    @FXML private ToggleButton thaToggle;
    @FXML private TextField searchField;

    private final List<StudentDTO> allStudents = new ArrayList<>();

    // ================= INITIALIZE =================
    @FXML
    public void initialize() {

        ToggleGroup batchGroup = new ToggleGroup();
        punkToggle.setToggleGroup(batchGroup);
        thaToggle.setToggleGroup(batchGroup);

        punkToggle.setSelected(true);
        loadPunkStudents();

        batchGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == punkToggle) loadPunkStudents();
            else if (newToggle == thaToggle) loadThaStudents();
        });

        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterStudents(newVal));
    }

    // ================= FILTER =================
    private void filterStudents(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            showStudents(allStudents);
            return;
        }

        String k = keyword.toLowerCase();
        List<StudentDTO> filtered = new ArrayList<>();

        for (StudentDTO s : allStudents) {
            if (s.getFullName().toLowerCase().contains(k)) {
                filtered.add(s);
            }
        }

        showStudents(filtered);
    }

    // ================= UI BUILD =================
    private void setupColumns(GridPane grid) {
        grid.getColumnConstraints().setAll(
                new ColumnConstraints(280),
                new ColumnConstraints(260),
                new ColumnConstraints()
        );
    }

    private void addRow(StudentDTO student) {

        GridPane row = new GridPane();
        row.setHgap(20);
        setupColumns(row);

        Circle avatar = new Circle(16);
        avatar.setStyle("-fx-fill: #E5E7EB;");

        Label nameLabel = new Label(student.getFullName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-cursor: hand;");
        nameLabel.setOnMouseClicked(e -> openStudentProfile(student, false));

        Label idLabel = new Label("ID: " + student.getStudentId());
        idLabel.setStyle("-fx-text-fill: #6B7280;");

        VBox nameBox = new VBox(3, nameLabel, idLabel);
        HBox studentBox = new HBox(10, avatar, nameBox);

        double attendancePercent = student.getAttendance();

        Label percent = new Label(String.format("%.0f%%", attendancePercent));
        ProgressBar bar = new ProgressBar(attendancePercent / 100.0);
        bar.setPrefWidth(180);
        setAttendanceBarColor(bar, attendancePercent);

        HBox attendanceBox = new HBox(10, percent, bar);
        attendanceBox.setAlignment(Pos.CENTER_LEFT);

        Label docsLabel = new Label(student.getDocsUploaded());

        Label viewFiles = new Label("View files");
        viewFiles.setStyle("-fx-text-fill: #7C3AED; -fx-underline: true;");
        viewFiles.setOnMouseClicked(e -> openStudentProfile(student, true));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox docsBox = new HBox(8, docsLabel, spacer, viewFiles);
        docsBox.setAlignment(Pos.CENTER_LEFT);

        row.add(studentBox, 0, 0);
        row.add(attendanceBox, 1, 0);
        row.add(docsBox, 2, 0);

        rowsBox.getChildren().add(row);
    }

    // ================= DATA =================
    private void loadPunkStudents() {

        allStudents.clear();

        StudentRepository repository = new StudentRepository();
        List<StudentDTO> studentsFromDb = repository.findAllWithStats(1);

        allStudents.addAll(studentsFromDb);

        showStudents(allStudents);
    }

    private void loadThaStudents() {

        allStudents.clear();

        StudentRepository repository = new StudentRepository();
        List<StudentDTO> studentsFromDb = repository.findAllWithStats(2);

        allStudents.addAll(studentsFromDb);

        showStudents(allStudents);
    }

    private void showStudents(List<StudentDTO> list) {
        rowsBox.getChildren().clear();
        list.forEach(this::addRow);
    }

    // ================= NAVIGATION =================
    private void openStudentProfile(StudentDTO student, boolean goToDocuments) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ui/fxml/studentProfile.fxml")
            );

            Parent root = loader.load();

            StudentProfileController controller = loader.getController();

            // FIX: Pass studentId, not DTO
            controller.loadStudent(student.getStudentId());
            //controller.setScrollToDocuments(goToDocuments);

            MainApp.setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= STYLE =================
    private void setAttendanceBarColor(ProgressBar bar, double percent) {

        // Apply consistent purple theme color to all bars
        String colorStyle = "-fx-accent: #7C3AED;";

        bar.setStyle(colorStyle);

        // Fallback: also apply to .bar pseudo-element after scene is ready
        Platform.runLater(() -> {
            javafx.scene.Node barNode = bar.lookup(".bar");
            if (barNode != null) {
                barNode.setStyle("-fx-background-color: #7C3AED;");
            }
        });

        /* OLD CODE - kept for reference
        String colorStyle;
        if (percent >= 75) {
            colorStyle = "-fx-accent: #22c55e;";
        } else if (percent >= 50) {
            colorStyle = "-fx-accent: #f59e0b;";
        } else {
            colorStyle = "-fx-accent: #ef4444;";
        }

        bar.setStyle(colorStyle);

        Platform.runLater(() -> {
            javafx.scene.Node barNode = bar.lookup(".bar");
            if (barNode != null) {
                if (percent >= 75)
                    barNode.setStyle("-fx-background-color: #22c55e;");
                else if (percent >= 50)
                    barNode.setStyle("-fx-background-color: #f59e0b;");
                else
                    barNode.setStyle("-fx-background-color: #ef4444;");
            }
        });
        */
    }
}
