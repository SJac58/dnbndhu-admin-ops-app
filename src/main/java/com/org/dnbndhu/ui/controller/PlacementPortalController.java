package com.org.dnbndhu.ui.controller;

import com.org.dnbndhu.domain.model.Student;
import com.org.dnbndhu.repository.CompanyRepository;
import com.org.dnbndhu.repository.PlacementRepository;
import com.org.dnbndhu.repository.StudentRepository;
import com.org.dnbndhu.service.notification.EmailNotificationService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.SelectionMode;

import java.time.LocalDateTime;
import java.util.List;

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
    private final PlacementRepository placementRepository = new PlacementRepository();
    private final EmailNotificationService notificationService = new EmailNotificationService();

    @FXML
    public void initialize() {

        studentsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        companiesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        loadStudents();
        loadCompanies();

        // Student search
        studentSearchField.textProperty().addListener((obs, old, val) ->
                filteredStudents.setPredicate(s ->
                        s.toLowerCase().contains(val.toLowerCase()))
        );

        // Company search
        companySearchField.textProperty().addListener((obs, old, val) ->
                filteredCompanies.setPredicate(c ->
                        c.toLowerCase().contains(val.toLowerCase()))
        );
    }

    // ================= LOAD STUDENTS =================
    private void loadStudents() {

        List<Student> students = studentRepository.findByBatchId(1);

        allStudents = FXCollections.observableArrayList();

        for (Student s : students) {
            allStudents.add(s.getFullName());
        }

        filteredStudents = new FilteredList<>(allStudents, s -> true);
        studentsList.setItems(filteredStudents);

        studentCountLabel.setText(String.valueOf(allStudents.size()));
    }

    // ================= LOAD COMPANIES =================
    private void loadCompanies() {

        List<String> companies = companyRepository.findAllNames();

        allCompanies = FXCollections.observableArrayList(companies);

        filteredCompanies = new FilteredList<>(allCompanies, c -> true);
        companiesList.setItems(filteredCompanies);
    }

    // ================= SEND MAIL =================
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

        // 🔹 Send notification to each selected student
        for (String studentName : selectedStudents) {

            Student student = findStudentByName(studentName);

            if (student != null && student.getEmail() != null) {

                String formattedMessage =
                        "Placement Opportunity with "
                                + String.join(", ", selectedCompanies)
                                + "\n\n"
                                + body;

                notificationService.sendEmail(
                        student.getEmail(),
                        "New Placement Opportunity",
                        formattedMessage
                );
            }
        }

        // 🔹 Log UI message
        String mailLog = String.format(
                "[%s] Sent to %d students | Companies: %s",
                LocalDateTime.now(),
                selectedStudents.size(),
                String.join(", ", selectedCompanies)
        );

        messagesList.getItems().add(0, mailLog);
        messageField.clear();

        new Alert(Alert.AlertType.INFORMATION,
                "Placement notifications sent successfully.")
                .showAndWait();
    }

    // ================= HELPER =================
    private Student findStudentByName(String name) {

        List<Student> students = studentRepository.findByBatchId(1);

        for (Student s : students) {
            if (s.getFullName().equals(name)) {
                return s;
            }
        }

        return null;
    }
}
