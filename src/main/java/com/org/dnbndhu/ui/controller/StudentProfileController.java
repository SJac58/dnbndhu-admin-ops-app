package com.org.dnbndhu.ui.controller;

import com.org.dnbndhu.domain.model.Student;
import com.org.dnbndhu.repository.DocumentRepository;
import com.org.dnbndhu.repository.StudentRepository;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.util.Map;

import static jdk.jfr.consumer.EventStream.openFile;

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

    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Label addressLabel;
    @FXML private Label districtLabel;
    @FXML private Label taluqLabel;
    @FXML private Label villageLabel;
    @FXML private Label pinCodeLabel;

    @FXML private Label aadhaarLabel;
    @FXML private Label panLabel;

    @FXML private Label batchIdLabel;
    @FXML private Label enrollmentDateLabel;
    @FXML private Label referralLabel;
    @FXML private Label priorTrainingLabel;

    @FXML private ProgressBar attendanceBar;
    @FXML private Label attendanceText;

    @FXML private Button viewPhotoBtn;
    @FXML private Button viewAadhaarBtn;
    @FXML private Button viewPanBtn;
    @FXML private Button viewBankBtn;
    @FXML private Button viewEducationalCertBtn;
    @FXML private Button viewUdidBtn;
    @FXML private Button viewMedicalCertBtn;

    @FXML private StackPane photoBox;

    private final DocumentRepository documentRepository =
            new DocumentRepository();

    private final StudentRepository studentRepository =
            new StudentRepository();

    public void loadStudent(int studentId) {

        Student s = studentRepository.findById(studentId);
        Map<Integer, String> docs =
                documentRepository.findByStudentId(studentId);

// Based on your document_types table:
        bindDocument(viewPhotoBtn, docs.get(1));       // PHOTO
        bindDocument(viewEducationalCertBtn, docs.get(3)); // EDUCATION_CERTIFICATE
        bindDocument(viewUdidBtn, docs.get(4));        // UDID_CARD
        bindDocument(viewAadhaarBtn, docs.get(5));     // AADHAR_CARD
        bindDocument(viewPanBtn, docs.get(6));         // PAN_CARD
        bindDocument(viewBankBtn, docs.get(7));        // BANK_PASSBOOK
        bindDocument(viewMedicalCertBtn, docs.get(8));
        if (s == null) return;

        nameLabel.setText(s.getFullName());
        ageGenderLabel.setText(
                (s.getAge() != null ? s.getAge() : "") +
                        " | " + s.getGender()
        );
        studentIdLabel.setText("ID: " + s.getStudentId());

        dobLabel.setText(s.getDateOfBirth());
        disabilityLabel.setText(s.getDisabilityType());
        maritalStatusLabel.setText(s.getMaritalStatus());
        religionLabel.setText(s.getReligion());
        casteLabel.setText(s.getCaste());
        subCasteLabel.setText(s.getSubCaste());

        emailLabel.setText(s.getEmail());
        phoneLabel.setText(s.getPhone());
        addressLabel.setText(s.getAddress());
        districtLabel.setText(s.getDistrict());
        taluqLabel.setText(s.getTaluq());
        villageLabel.setText(s.getVillage());
        pinCodeLabel.setText(s.getPinCode());

        aadhaarLabel.setText(s.getAadhaarNo());
        panLabel.setText(s.getPanNo());

        batchIdLabel.setText(String.valueOf(s.getBatchId()));
        enrollmentDateLabel.setText(s.getEnrollmentDate());
        referralLabel.setText(s.getReferralSource());
        priorTrainingLabel.setText(
                s.getPriorTraining() != null ? "Yes" : "No"
        );

        double attendance =
                studentRepository.calculateAttendancePercentage(studentId);

        attendanceBar.setProgress(attendance / 100.0);
        attendanceText.setText("Attendance: " + attendance + "%");

        String photoPath = docs.get(1);

        if (photoPath != null) {
            ImageView imageView = new ImageView(
                    new Image(new File(photoPath).toURI().toString())
            );
            imageView.setFitWidth(120);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);

            photoBox.getChildren().clear();
            photoBox.getChildren().add(imageView);
        }
    }
    private void bindDocument(Button button, String path) {

        if (path == null) {
            button.setDisable(true);
            return;
        }

        button.setDisable(false);

        button.setOnAction(e -> openFile(path));
    }
    private void openFile(String path) {
        try {
            java.awt.Desktop.getDesktop()
                    .open(new java.io.File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
