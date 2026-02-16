package com.org.dnbndhu.ui.controller;

import com.org.dnbndhu.domain.model.Student;
import com.org.dnbndhu.repository.StudentRepository;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class StudentProfileController {

    @FXML private Label nameLabel;
    @FXML private Label ageGenderLabel;
    @FXML private Label studentIdLabel;

    @FXML private Label dobLabel;
    @FXML private Label disabilityLabel;
    @FXML private Label maritalStatusLabel;
    @FXML private Label religionLabel;
    @FXML private Label casteLabel;
    @FXML private Label subCasteLabel;

    @FXML private Label aadhaarLabel;
    @FXML private Label panLabel;

    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;

    @FXML private Label addressLabel;
    @FXML private Label districtLabel;
    @FXML private Label taluqLabel;
    @FXML private Label villageLabel;
    @FXML private Label pinCodeLabel;

    @FXML private Label referralLabel;
    @FXML private Label priorTrainingLabel;

    @FXML private Label enrollmentDateLabel;
    @FXML private Label batchIdLabel;

    @FXML private ProgressBar attendanceBar;
    @FXML private Label attendanceText;

    @FXML private ScrollPane profileScroll;
    @FXML private VBox documentsSection;

    private boolean scrollToDocuments = false;

    private final StudentRepository repository = new StudentRepository();

    // ================= LOAD STUDENT =================
    public void loadStudent(int studentId) {

        Student s = repository.findById(studentId);

        if (s == null) return;

        nameLabel.setText(s.getFullName());
        ageGenderLabel.setText(
                (s.getAge() != null ? s.getAge() : "") +
                        " yrs • " +
                        (s.getGender() != null ? s.getGender() : "")
        );

        studentIdLabel.setText("ID: " + s.getStudentId());

        dobLabel.setText(nullSafe(s.getDateOfBirth()));
        disabilityLabel.setText(
                nullSafe(s.getDisabilityType()) +
                        (s.getDisabilityPercentage() != null ?
                                " (" + s.getDisabilityPercentage() + "%)" : "")
        );

        maritalStatusLabel.setText(nullSafe(s.getMaritalStatus()));
        religionLabel.setText(nullSafe(s.getReligion()));
        casteLabel.setText(nullSafe(s.getCaste()));
        subCasteLabel.setText(nullSafe(s.getSubCaste()));

        aadhaarLabel.setText(nullSafe(s.getAadhaarNo()));
        panLabel.setText(nullSafe(s.getPanNo()));

        emailLabel.setText(nullSafe(s.getEmail()));
        phoneLabel.setText(nullSafe(s.getPhone()));

        addressLabel.setText(nullSafe(s.getAddress()));
        districtLabel.setText(nullSafe(s.getDistrict()));
        taluqLabel.setText(nullSafe(s.getTaluq()));
        villageLabel.setText(nullSafe(s.getVillage()));
        pinCodeLabel.setText(nullSafe(s.getPinCode()));

        referralLabel.setText(nullSafe(s.getReferralSource()));
        priorTrainingLabel.setText(
                s.getPriorTraining() != null && s.getPriorTraining() == 1
                        ? "Yes"
                        : "No"
        );

        enrollmentDateLabel.setText(nullSafe(s.getEnrollmentDate()));
        batchIdLabel.setText(String.valueOf(s.getBatchId()));

        // Attendance %
        double attendancePercent =
                repository.calculateAttendancePercentage(studentId);

        attendanceBar.setProgress(attendancePercent / 100.0);
        attendanceText.setText(String.format("%.0f%% attendance", attendancePercent));
    }

    // ================= SCROLL SUPPORT =================
    public void setScrollToDocuments(boolean value) {
        this.scrollToDocuments = value;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            if (scrollToDocuments) {
                scrollToNode(documentsSection);
            }
        });
    }

    private void scrollToNode(Node node) {
        double contentHeight =
                profileScroll.getContent().getBoundsInLocal().getHeight();
        double nodeY = node.getBoundsInParent().getMinY();
        profileScroll.setVvalue(nodeY / contentHeight);
    }

    // ================= UTILITY =================
    private String nullSafe(String value) {
        return value == null ? "-" : value;
    }
}
