package com.org.dnbndhu.ui.controller;

import com.org.dnbndhu.application.AcceptedDocument;
import com.org.dnbndhu.application.EnrollmentWorkflow;
import com.org.dnbndhu.domain.model.FamilyDetails;
import com.org.dnbndhu.domain.model.Qualification;
import com.org.dnbndhu.domain.model.Student;
import com.org.dnbndhu.service.enrollment.DocumentProcessingService;
import com.org.dnbndhu.service.ocr.OCRFieldExtractorService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.util.*;

public class EnrollNewStudentController {

    @FXML private BorderPane rootPane;

    @FXML private TextField nameField;
    @FXML private TextField ageField;
    @FXML private TextField disabilityTypeField;
    @FXML private TextField disabilityPercentField;
    @FXML private TextArea addressField;

    @FXML private DatePicker applicationDatePicker;
    @FXML private RadioButton maleRadio;
    @FXML private RadioButton femaleRadio;

    @FXML private RadioButton marriedRadio;
    @FXML private RadioButton unmarriedRadio;
    @FXML private RadioButton divorcedRadio;

    private final Map<String, File> uploadedDocuments = new HashMap<>();

    private final DocumentProcessingService processingService =
            new DocumentProcessingService();

    private final OCRFieldExtractorService extractor =
            new OCRFieldExtractorService();

    private final EnrollmentWorkflow workflow =
            new EnrollmentWorkflow();

    // ==============================
    // SUBMIT → SAVE TO DB
    // ==============================
    @FXML
    private void onSubmit() {

        try {

            if (nameField.getText().isBlank()) {
                showError("Validation Error", "Name is required");
                return;
            }

            Student student = buildStudentFromForm();

            List<AcceptedDocument> docs =
                    buildAcceptedDocuments();

            // Build qualifications and family details from UI
            List<Qualification> qualifications = buildQualificationsFromUI();
            List<FamilyDetails> familyDetails = buildFamilyDetailsFromUI();

            // Call the workflow with full set of parameters
            workflow.commitEnrollment(student, docs, qualifications, familyDetails);

            showInfo("Success", "Student enrolled successfully");

            clearForm();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to enroll student");
        }
    }

    // ==============================
    // BUILD STUDENT ENTITY
    // ==============================
    private Student buildStudentFromForm() {

        Student s = new Student();

        s.setFullName(nameField.getText());
        s.setAge(parseInteger(ageField.getText()));
        s.setDisabilityType(disabilityTypeField.getText());
        s.setDisabilityPercentage(parseInteger(disabilityPercentField.getText()));
        s.setAddress(addressField.getText());

        if (maleRadio.isSelected()) s.setGender("Male");
        else if (femaleRadio.isSelected()) s.setGender("Female");

        if (marriedRadio.isSelected()) s.setMaritalStatus("Married");
        else if (unmarriedRadio.isSelected()) s.setMaritalStatus("Unmarried");
        else if (divorcedRadio.isSelected()) s.setMaritalStatus("Divorced");

        s.setEnrollmentDate(LocalDate.now().toString());
        s.setBatchId(1); // adjust later dynamically

        return s;
    }

    // ==============================
    // BUILD DOCUMENT LIST
    // ==============================
    private List<AcceptedDocument> buildAcceptedDocuments() {

        List<AcceptedDocument> list = new ArrayList<>();

        for (Map.Entry<String, File> entry : uploadedDocuments.entrySet()) {

            int documentTypeId =
                    mapDocumentNameToId(entry.getKey());

            list.add(new AcceptedDocument(
                    documentTypeId,
                    entry.getValue().getAbsolutePath(),
                    1.0
            ));
        }

        return list;
    }
    private List<Qualification> buildQualificationsFromUI() {
        List<Qualification> list = new ArrayList<>();

        // loop through GridPane rows
        // read textfields
        // create Qualification objects
        // add to list

        return list;
    }

    // Build family details from UI (placeholder - parse your UI controls here)
    private List<FamilyDetails> buildFamilyDetailsFromUI() {
        List<FamilyDetails> list = new ArrayList<>();

        // loop through family details UI rows
        // read textfields (name, relation, age, occupation, etc.)
        // create FamilyDetails objects and add to list

        return list;
    }

    private int mapDocumentNameToId(String name) {

        return switch (name.toUpperCase()) {
            case "AADHAR_CARD" -> 1;
            case "PAN_CARD" -> 2;
            case "EDUCATION_CERTIFICATE" -> 3;
            case "PHOTO" -> 4;
            case "MEDICAL_CERTIFICATE" -> 5;
            case "UDID_CARD" -> 6;
            case "BANK_PASSBOOK" -> 7;
            default -> 1;
        };
    }

    // ==============================
    // FILE UPLOAD + OCR + AUTOFILL
    // ==============================
    private Button buildUploadButton(String label, String documentType) {

        Button btn = new Button(label);

        btn.setOnAction(e -> {

            FileChooser chooser = new FileChooser();
            chooser.setTitle(label);

            chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter(
                            "Documents", "*.pdf", "*.png", "*.jpg", "*.jpeg")
            );

            File selectedFile =
                    chooser.showOpenDialog(rootPane.getScene().getWindow());

            if (selectedFile == null) return;

            try {

                // 1️⃣ Quality check + OCR
                String extractedText =
                        processingService.processAndExtractText(selectedFile);

                // 2️⃣ Extract fields
                Map<String, String> fields =
                        extractor.extractFields(documentType, extractedText);

                // 3️⃣ Auto-fill safely
                autoFillForm(fields);

                // 4️⃣ Save for final commit
                uploadedDocuments.put(documentType, selectedFile);

                btn.setText("✔ " + label);
                btn.setDisable(true);

            } catch (Exception ex) {
                showError("OCR / Quality Error", ex.getMessage());
            }
        });

        return btn;
    }

    // ==============================
    // AUTO-FILL SAFE
    // ==============================
    private void autoFillForm(Map<String, String> fields) {

        if (fields.containsKey("fullName")
                && nameField.getText().isBlank()) {
            nameField.setText(fields.get("fullName"));
        }

        if (fields.containsKey("address")
                && addressField.getText().isBlank()) {
            addressField.setText(fields.get("address"));
        }

        if (fields.containsKey("disabilityType")
                && disabilityTypeField.getText().isBlank()) {
            disabilityTypeField.setText(fields.get("disabilityType"));
        }

        if (fields.containsKey("disabilityPercentage")
                && disabilityPercentField.getText().isBlank()) {
            disabilityPercentField.setText(fields.get("disabilityPercentage"));
        }

        if (fields.containsKey("pinCode")) {
            System.out.println("Detected Pincode: " + fields.get("pinCode"));
        }

        if (fields.containsKey("panNo")) {
            System.out.println("Detected PAN: " + fields.get("panNo"));
        }

        if (fields.containsKey("aadhaarNo")) {
            System.out.println("Detected Aadhaar: " + fields.get("aadhaarNo"));
        }
    }

    // ==============================
    // HELPERS
    // ==============================
    private Integer parseInteger(String value) {
        try {
            return value == null || value.isBlank()
                    ? null
                    : Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
    }

    private void clearForm() {

    }

    private void showInfo(String title, String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    private void showError(String title, String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    public void addQualificationRow(ActionEvent actionEvent) {
    }
}
