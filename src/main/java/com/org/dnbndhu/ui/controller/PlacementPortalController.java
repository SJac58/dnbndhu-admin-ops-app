package com.org.dnbndhu.ui.controller;

import com.org.dnbndhu.domain.model.Student;
import com.org.dnbndhu.repository.CompanyRepository;
import com.org.dnbndhu.repository.PlacementRepository;
import com.org.dnbndhu.repository.StudentRepository;
import com.org.dnbndhu.service.notification.EmailNotificationService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.SelectionMode;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final StudentRepository studentRepository = new StudentRepository();
    private final CompanyRepository companyRepository = new CompanyRepository();
    private final EmailNotificationService notificationService = new EmailNotificationService();
    private final PlacementRepository placementRepository = new PlacementRepository();
    // 🔥 Store student objects in memory for fast lookup
    private final Map<String, Student> studentMap = new HashMap<>();

    @FXML
    public void initialize() {

        studentsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        companiesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        loadStudents();
        loadCompanies();

        studentSearchField.textProperty().addListener((obs, old, val) ->
                filteredStudents.setPredicate(s ->
                        s.toLowerCase().contains(val.toLowerCase()))
        );

        companySearchField.textProperty().addListener((obs, old, val) ->
                filteredCompanies.setPredicate(c ->
                        c.toLowerCase().contains(val.toLowerCase()))
        );
        loadRecentLogs();
    }

    private void loadStudents() {

        List<Student> students = studentRepository.findAll();

        allStudents = FXCollections.observableArrayList();

        for (Student s : students) {
            allStudents.add(s.getFullName());
            studentMap.put(s.getFullName(), s);
        }

        filteredStudents = new FilteredList<>(allStudents, s -> true);
        studentsList.setItems(filteredStudents);

        studentCountLabel.setText(String.valueOf(allStudents.size()));
    }

    private void loadCompanies() {

        List<String> companies = companyRepository.findAllNames();

        allCompanies = FXCollections.observableArrayList(companies);

        filteredCompanies = new FilteredList<>(allCompanies, c -> true);
        companiesList.setItems(filteredCompanies);
    }

    @FXML
    private void sendMail() {

        List<String> selectedStudents =
                studentsList.getSelectionModel().getSelectedItems();

        List<String> selectedCompanies =
                companiesList.getSelectionModel().getSelectedItems();

        String body = messageField.getText().trim();

        if (selectedStudents.isEmpty()
                || selectedCompanies.isEmpty()
                || body.isEmpty()) {

            new Alert(Alert.AlertType.WARNING,
                    "Select students, companies, and enter a message.")
                    .showAndWait();
            return;
        }

        String subject = "Notification for Placement Opportunity in "
                + String.join(", ", selectedCompanies);

        String formattedMessage =
                "Dear Candidate,\n\n"
                        + "Placement Opportunity with "
                        + String.join(", ", selectedCompanies)
                        + "\n\n"
                        + body
                        + "\n\nRegards,\nDEENABANDHU Administration";

        // 🔥 Background Task to avoid UI freeze
        Task<Void> emailTask = new Task<>() {
            @Override
            protected Void call() {

                for (String studentName : selectedStudents) {

                    Student student = studentMap.get(studentName);

                    if (student != null
                            && student.getEmail() != null
                            && !student.getEmail().isBlank()) {

                        notificationService.sendEmail(
                                student.getStudentId(),
                                student.getEmail(),
                                subject,
                                formattedMessage
                        );
                    }
                }

                return null;
            }
        };

        emailTask.setOnSucceeded(e -> {

            String mailLog = String.format(
                    "[%s] Sent to %d students | Companies: %s",
                    LocalDateTime.now(),
                    selectedStudents.size(),
                    String.join(", ", selectedCompanies)
            );

            loadRecentLogs();
            messageField.clear();

            new Alert(Alert.AlertType.INFORMATION,
                    "Placement notifications sent successfully.")
                    .showAndWait();
        });

        new Thread(emailTask).start();
    }
    private void loadRecentLogs() {

        messagesList.getItems().clear();

        List<String> logs = placementRepository.fetchRecentPlacementLogs(5);

        messagesList.getItems().addAll(logs);
    }
}