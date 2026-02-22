package com.org.dnbndhu.ui.controller;

import com.org.dnbndhu.domain.dto.EnrollmentDraftDTO;
import com.org.dnbndhu.domain.model.FamilyDetails;
import com.org.dnbndhu.domain.model.Qualification;
import com.org.dnbndhu.domain.model.Student;
import com.org.dnbndhu.service.enrollment.EnrollmentService;
import com.org.dnbndhu.ui.MainApp;

import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnrollNewStudentController {

    @FXML private BorderPane rootPane;
    @FXML private DatePicker dobPicker;
    @FXML private TextField nameField;
    @FXML private TextField ageField;
    @FXML private TextField disabilityTypeField;
    @FXML private TextField disabilityPercentField;
    @FXML private TextArea addressField;

    @FXML private TextField districtField;
    @FXML private TextField taluqField;
    @FXML private TextField villageField;
    @FXML private TextField pinField;

    @FXML private TextField aadhaarField;
    @FXML private TextField panField;
    @FXML private TextField religionField;
    @FXML private TextField casteField;
    @FXML private TextField subCasteField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;

    @FXML private DatePicker applicationDatePicker;

    @FXML private RadioButton maleRadio;
    @FXML private RadioButton femaleRadio;
    @FXML private RadioButton marriedRadio;
    @FXML private RadioButton unmarriedRadio;
    @FXML private RadioButton divorcedRadio;

    @FXML private GridPane qualificationTable;
    @FXML private GridPane familyTable;

    private EnrollmentDraftDTO draft;

    private final EnrollmentService enrollmentService =
            new EnrollmentService();

    // ================= RECEIVE OCR DRAFT =================
    public void setDraft(EnrollmentDraftDTO draft) {
        this.draft = draft;
        applyDraftToUI();
    }

    // ================= APPLY OCR DATA =================
    private void applyDraftToUI() {

        if (draft == null) return;

        Map<String, String> fields = draft.getAllFields();

        setIfPresent(nameField, fields.get("fullName"));
        setIfPresent(aadhaarField, fields.get("aadhaarNo"));
        setIfPresent(panField, fields.get("panNo"));
        setIfPresent(addressField, fields.get("address"));
        setIfPresent(disabilityTypeField, fields.get("disabilityType"));
        setIfPresent(disabilityPercentField, fields.get("disabilityPercentage"));
        setIfPresent(pinField, fields.get("pinCode"));
        if (fields.get("dateOfBirth") != null) {
            try {
                dobPicker.setValue(LocalDate.parse(fields.get("dateOfBirth"),
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            } catch (Exception ignored) {}
        }
    }

    private void setIfPresent(TextInputControl field, String value) {
        if (value != null && !value.isBlank()) {
            field.setText(value);
        }
    }

    // ================= INITIALIZE =================
    @FXML
    public void initialize() {
        setupQualificationHeaders();
        setupFamilyHeaders();
    }

    // ================= HEADERS =================
    private void setupQualificationHeaders() {

        qualificationTable.getChildren().clear();

        qualificationTable.add(new Label("Education Level"), 0, 0);
        qualificationTable.add(new Label("Institution"), 1, 0);
        qualificationTable.add(new Label("Board/University"), 2, 0);
        qualificationTable.add(new Label("Year"), 3, 0);
    }

    private void setupFamilyHeaders() {

        familyTable.getChildren().clear();

        familyTable.add(new Label("Name"), 0, 0);
        familyTable.add(new Label("Relationship"), 1, 0);
        familyTable.add(new Label("Income"), 2, 0);
        familyTable.add(new Label("Phone"), 3, 0);
    }

    // ================= ADD ROWS =================
    @FXML
    private void addQualificationRow() {

        int row = qualificationTable.getRowCount();

        qualificationTable.add(new TextField(), 0, row);
        qualificationTable.add(new TextField(), 1, row);
        qualificationTable.add(new TextField(), 2, row);
        qualificationTable.add(new TextField(), 3, row);
    }

    @FXML
    private void addFamilyRow() {

        int row = familyTable.getRowCount();

        familyTable.add(new TextField(), 0, row);
        familyTable.add(new TextField(), 1, row);
        familyTable.add(new TextField(), 2, row);
        familyTable.add(new TextField(), 3, row);
    }

    // ================= SUBMIT =================
    @FXML
    private void onSubmit() {

        try {

            if (nameField.getText().isBlank()) {
                showError("Validation Error", "Name is required");
                return;
            }

            Student student = buildStudentFromForm();
            List<Qualification> qualifications = buildQualificationsFromUI();
            List<FamilyDetails> familyDetails = buildFamilyDetailsFromUI();

            int studentId = enrollmentService.enrollStudent(
                    student,
                    qualifications,
                    familyDetails,
                    draft
            );

            showInfo("Success", "Student enrolled successfully. ID: " + studentId);
            clearForm();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", e.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        MainApp.setRoot("dashboard.fxml");
    }

    private Student buildStudentFromForm() {

        Student s = new Student();

        s.setFullName(nameField.getText());
        if (dobPicker.getValue() != null) {
            s.setDateOfBirth(dobPicker.getValue().toString());
        }
        s.setAge(parseInteger(ageField.getText()));
        s.setDisabilityType(disabilityTypeField.getText());
        s.setDisabilityPercentage(parseInteger(disabilityPercentField.getText()));
        s.setAddress(addressField.getText());
        s.setDistrict(districtField.getText());
        s.setTaluq(taluqField.getText());
        s.setVillage(villageField.getText());
        s.setPinCode(pinField.getText());
        s.setAadhaarNo(aadhaarField.getText());
        s.setPanNo(panField.getText());
        s.setReligion(religionField.getText());
        s.setCaste(casteField.getText());
        s.setSubCaste(subCasteField.getText());
        s.setEmail(emailField.getText());
        s.setPhone(phoneField.getText());

        if (maleRadio.isSelected()) s.setGender("Male");
        else if (femaleRadio.isSelected()) s.setGender("Female");

        if (marriedRadio.isSelected()) s.setMaritalStatus("Married");
        else if (unmarriedRadio.isSelected()) s.setMaritalStatus("Unmarried");
        else if (divorcedRadio.isSelected()) s.setMaritalStatus("Divorced");

        s.setEnrollmentDate(LocalDate.now().toString());
        s.setBatchId(1);

        return s;
    }

    private List<Qualification> buildQualificationsFromUI() {
        return new ArrayList<>();
    }

    private List<FamilyDetails> buildFamilyDetailsFromUI() {
        return new ArrayList<>();
    }

    private Integer parseInteger(String value) {
        try {
            return value == null || value.isBlank() ? null : Integer.parseInt(value);
        } catch (Exception e) { return null; }
    }

    private void clearForm() {
        nameField.clear();
        addressField.clear();
    }

    private void showInfo(String title, String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    private void showError(String title, String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}