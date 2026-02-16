package com.org.dnbndhu.ui.controller;

import java.time.LocalDateTime;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;

public class PlacementPortalController {

    @FXML private ListView<String> messagesList;
    @FXML private ListView<String> studentsList;
    @FXML private ListView<String> companiesList;

    @FXML private TextField messageField;
    @FXML private TextField studentSearchField;
    @FXML private TextField companySearchField;
    @FXML private Label studentCountLabel;

    private ObservableList<String> allStudents;
    private ObservableList<String> allCompanies;

    private FilteredList<String> filteredStudents;
private FilteredList<String> filteredCompanies;

@FXML
public void initialize() {

    // Enable multi-select
    studentsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    companiesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    // Master lists
    allStudents = FXCollections.observableArrayList(
        "Aarav Patel", "Sneha Gupta", "John Doe",
        "Priya Sharma", "Michael Chen", "Sarah Williams"
    );

    allCompanies = FXCollections.observableArrayList(
        "Big Bazaar", "DMart", "Reliance Fresh",
        "Spencer’s Retail", "Shoppers Stop"
    );

    // 🔥 Filtered lists (DO NOT reset items again)
    filteredStudents = new FilteredList<>(allStudents, s -> true);
    filteredCompanies = new FilteredList<>(allCompanies, c -> true);

    studentsList.setItems(filteredStudents);
    companiesList.setItems(filteredCompanies);

    studentCountLabel.setText(String.valueOf(allStudents.size()));

    // Live student search
    studentSearchField.textProperty().addListener((obs, old, val) -> {
        filteredStudents.setPredicate(s ->
            s.toLowerCase().contains(val.toLowerCase())
        );
    });

    // Live company search
    companySearchField.textProperty().addListener((obs, old, val) -> {
        filteredCompanies.setPredicate(c ->
            c.toLowerCase().contains(val.toLowerCase())
        );
    });
}


    // 📧 SEND MAIL (mock – SMTP later)
    @FXML
    private void sendMail() {

        List<String> selectedStudents =
            studentsList.getSelectionModel().getSelectedItems();

        List<String> selectedCompanies =
            companiesList.getSelectionModel().getSelectedItems();

        String body = messageField.getText().trim();

        if (selectedStudents.isEmpty() || selectedCompanies.isEmpty() || body.isEmpty()) {
            new Alert(Alert.AlertType.WARNING,
                "Select students, companies, and enter a message.")
                .showAndWait();
            return;
        }

        // 🔹 Simulate formatted mail
        String mailLog = String.format(
            "[%s] Sent to %d students | Companies: %s",
            LocalDateTime.now().toString(),
            selectedStudents.size(),
            String.join(", ", selectedCompanies)
        );

        messagesList.getItems().add(0, mailLog);
        messageField.clear();

        // 🔌 FUTURE:
        // sendEmail(studentEmails, subject, formattedBody);
    }
}
