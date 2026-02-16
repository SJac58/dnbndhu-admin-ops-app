package com.org.dnbndhu.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;




public class AttendanceController {

    @FXML
    private VBox rowsBox;
    @FXML
private ToggleGroup batchGroup;
@FXML
private Label dateLabel;
@FXML
private TextField searchField;


@FXML
public void initialize() {

    // Set current date
    LocalDate today = LocalDate.now();
    DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
    dateLabel.setText(today.format(formatter));

    // Add students
    addRow("Aarav Sharma");
    addRow("Priya Patel");
    addRow("Arjun Kumar");
    addRow("Ananya Reddy");
    addRow("Rohan Gupta");
    addRow("Ishita Singh");
    addRow("Kabir Mehta");
}


    private void addRow(String name) {

        GridPane row = new GridPane();
        row.getStyleClass().add("student-row");
        row.setHgap(40);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // SAME columns as header
        row.getColumnConstraints().addAll(
                new ColumnConstraints(60),   // PHOTO
                new ColumnConstraints(300),  // NAME
                new ColumnConstraints(120),  // GAP
                new ColumnConstraints(80)    // ABSENT
        );

        Circle avatar = new Circle(16);
        avatar.setStyle("-fx-fill: #e5e7eb;");

        Label nameLabel = new Label(name);

        CheckBox absent = new CheckBox();

        row.add(avatar, 0, 0);
        row.add(nameLabel, 1, 0);
        row.add(absent, 3, 0);

        rowsBox.getChildren().add(row);
    }
      @FXML
private void markAllPresent() {

    // Mark everyone present
    for (Node node : rowsBox.getChildren()) {
        if (node instanceof GridPane) {
            GridPane row = (GridPane) node;

            for (Node child : row.getChildren()) {
                if (child instanceof CheckBox) {
                    ((CheckBox) child).setSelected(false);
                }
            }
        }
    }

    // Show alert
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Attendance Updated");
    alert.setHeaderText(null);
    alert.setContentText("All students have been marked present.");
    alert.showAndWait();
}
@FXML
private void saveAttendance() {

    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Success");
    alert.setHeaderText(null);
    alert.setContentText("Attendance saved successfully.");
    alert.showAndWait();
}

@FXML
private void filterStudents() {

    String searchText = searchField.getText().toLowerCase();

    for (Node node : rowsBox.getChildren()) {
        if (node instanceof GridPane) {
            GridPane row = (GridPane) node;

            // Student name is in column 1
            Label nameLabel = null;

            for (Node child : row.getChildren()) {
                if (GridPane.getColumnIndex(child) != null
                        && GridPane.getColumnIndex(child) == 1
                        && child instanceof Label) {
                    nameLabel = (Label) child;
                    break;
                }
            }

            if (nameLabel != null) {
                String studentName = nameLabel.getText().toLowerCase();
                row.setVisible(studentName.contains(searchText));
                row.setManaged(studentName.contains(searchText));
            }
        }
    }
    
}


}
