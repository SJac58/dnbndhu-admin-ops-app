package com.org.dnbndhu.ui.controller;

import com.org.dnbndhu.domain.dto.AttendanceDTO;
import com.org.dnbndhu.domain.model.Student;
import com.org.dnbndhu.repository.StudentRepository;
import com.org.dnbndhu.service.attendance.AttendanceService;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AttendanceController {

    @FXML private VBox rowsBox;
    @FXML private Label dateLabel;
    @FXML private TextField searchField;
    @FXML private ToggleButton punkToggle;
    @FXML private ToggleButton thaToggle;
    @FXML private ToggleGroup batchGroup;

    private final List<AttendanceDTO> attendanceList = new ArrayList<>();

    private final StudentRepository studentRepository = new StudentRepository();
    private final AttendanceService attendanceService = new AttendanceService();

    private LocalDate selectedDate;

    @FXML
    public void initialize() {

        selectedDate = LocalDate.now();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        dateLabel.setText(selectedDate.format(formatter));

        // Determine batch id from the toggle (PUNK -> 1, THA -> 2)
        int initialBatch = (punkToggle != null && punkToggle.isSelected()) ? 1 : 2;
        loadStudents(initialBatch);

        // Reload students when the toggle selection changes
        if (batchGroup != null) {
            batchGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
                int batchId = (punkToggle != null && punkToggle.isSelected()) ? 1 : 2;
                loadStudents(batchId);
            });
        }
    }

    private void loadStudents(int batchId) {

        rowsBox.getChildren().clear();
        attendanceList.clear();

        List<Student> students = studentRepository.findByBatchId(batchId);

        for (Student s : students) {
            AttendanceDTO dto =
                    new AttendanceDTO(s.getStudentId(), s.getFullName());
            attendanceList.add(dto);
            addRow(dto);
        }
    }

    private void addRow(AttendanceDTO dto) {

        GridPane row = new GridPane();
        row.getStyleClass().add("student-row");
        row.setHgap(40);

        row.getColumnConstraints().addAll(
                new ColumnConstraints(60),
                new ColumnConstraints(300),
                new ColumnConstraints(80)
        );

        Circle avatar = new Circle(16);
        avatar.setStyle("-fx-fill: #e5e7eb;");

        Label nameLabel = new Label(dto.getFullName());

        CheckBox absent = new CheckBox();
        absent.selectedProperty().addListener((obs, old, selected) ->
                dto.setAbsent(selected)
        );

        row.add(avatar, 0, 0);
        row.add(nameLabel, 1, 0);
        row.add(absent, 2, 0);

        rowsBox.getChildren().add(row);
    }

    // ================= MARK ALL PRESENT =================
    @FXML
    private void markAllPresent() {

        for (AttendanceDTO dto : attendanceList) {
            dto.setAbsent(false);
        }

        // Update UI checkboxes visually
        for (Node node : rowsBox.getChildren()) {
            if (node instanceof GridPane row) {
                CheckBox cb = (CheckBox) row.getChildren().get(2);
                cb.setSelected(false);
            }
        }
    }

    // ================= SAVE =================
    @FXML
    private void saveAttendance() {

        Map<Integer, Boolean> attendanceMap = new HashMap<>();

        for (AttendanceDTO dto : attendanceList) {
            attendanceMap.put(dto.getStudentId(), dto.isAbsent());
        }

        attendanceService.saveAttendance(attendanceMap, selectedDate);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Attendance saved successfully.");
        alert.showAndWait();
    }

    // ================= FILTER =================
    @FXML
    private void filterStudents() {

        String searchText = searchField.getText().toLowerCase();

        for (Node node : rowsBox.getChildren()) {
            if (node instanceof GridPane row) {

                Label nameLabel = (Label) row.getChildren().get(1);

                boolean visible =
                        nameLabel.getText().toLowerCase().contains(searchText);

                row.setVisible(visible);
                row.setManaged(visible);
            }
        }
    }
}
