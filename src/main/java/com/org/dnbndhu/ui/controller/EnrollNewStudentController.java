package com.org.dnbndhu.ui.controller;

import com.org.dnbndhu.domain.dto.EnrollmentDraftDTO;
import com.org.dnbndhu.domain.model.FamilyDetails;
import com.org.dnbndhu.domain.model.Qualification;
import com.org.dnbndhu.domain.model.Student;
import com.org.dnbndhu.service.enrollment.EnrollmentService;
import com.org.dnbndhu.ui.MainApp;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    @FXML private ComboBox<String> programCombo;

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

        //setIfPresent(ageField, fields.get("age"));

        if (fields.get("dateOfBirth") != null) {
            try {
                // OCR produces dd/MM/yyyy — try that first
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                dobPicker.setValue(LocalDate.parse(fields.get("dateOfBirth"), fmt));
            } catch (Exception e1) {
                try {
                    // Fallback: try ISO (yyyy-MM-dd)
                    dobPicker.setValue(LocalDate.parse(fields.get("dateOfBirth")));
                } catch (Exception ignored) {}
            }
        }
    }

    private void setIfPresent(TextInputControl field, String value) {
        if (value != null && !value.isBlank()) {
            field.setText(value);
        }
    }

    @FXML
    public void initialize() {
        setupQualificationHeaders();
        setupFamilyHeaders();
        dobPicker.valueProperty().addListener((obs, oldVal, newVal) -> {

            if (newVal != null) {

                int age = java.time.Period
                        .between(newVal, java.time.LocalDate.now())
                        .getYears();

                ageField.setText(String.valueOf(age));
            }
        });
    }

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
        // Set batch ID from selected program in the ComboBox (e.g. "1-PUNK" -> batchId = 1)
        // Parse leading digits from programCombo value (e.g. "1-PUNK" or "1 PUNK")
        try {
            String prog = programCombo != null ? programCombo.getValue() : null;
            if (prog != null && !prog.isBlank()) {
                Pattern p = Pattern.compile("^\\s*(\\d+)");
                Matcher m = p.matcher(prog);
                if (m.find()) {
                    s.setBatchId(Integer.parseInt(m.group(1)));
                } else {
                    s.setBatchId(1);
                }
            } else {
                s.setBatchId(1);
            }
        } catch (Exception e) {
            s.setBatchId(1);
        }

        return s;
    }

    private List<Qualification> buildQualificationsFromUI() {

        List<Qualification> list = new ArrayList<>();

        for (int row = 1; row < qualificationTable.getRowCount(); row++) {

            TextField education = getTextField(qualificationTable, row, 0);
            TextField institution = getTextField(qualificationTable, row, 1);
            TextField board = getTextField(qualificationTable, row, 2);
            TextField year = getTextField(qualificationTable, row, 3);

            if (education != null && !education.getText().isBlank()) {

                Qualification q = new Qualification();
                q.setEducationLevel(education.getText());
                q.setInstitution(institution != null ? institution.getText() : null);
                q.setBoardUniversity(board != null ? board.getText() : null);
                q.setYearOfPassing(parseInteger(year != null ? year.getText() : null));

                list.add(q);
            }
        }

        return list;
    }

    private List<FamilyDetails> buildFamilyDetailsFromUI() {

        List<FamilyDetails> list = new ArrayList<>();

        for (int row = 1; row < familyTable.getRowCount(); row++) {

            TextField name = getTextField(familyTable, row, 0);
            TextField relation = getTextField(familyTable, row, 1);
            TextField income = getTextField(familyTable, row, 2);
            TextField phone = getTextField(familyTable, row, 3);

            if (name != null && !name.getText().isBlank()) {

                FamilyDetails f = new FamilyDetails();
                f.setMemberName(name.getText());
                f.setRelationship(relation != null ? relation.getText() : null);
                f.setIncome(parseDouble(income != null ? income.getText() : null));
                f.setPhone(phone != null ? phone.getText() : null);

                list.add(f);
            }
        }

        return list;
    }

    private TextField getTextField(GridPane grid, int row, int col) {

        for (Node node : grid.getChildren()) {

            Integer r = GridPane.getRowIndex(node);
            Integer c = GridPane.getColumnIndex(node);

            if (r != null && c != null && r == row && c == col) {
                return (TextField) node;
            }
        }
        return null;
    }

    private Integer parseInteger(String value) {
        try {
            return value == null || value.isBlank() ? null : Integer.parseInt(value);
        } catch (Exception e) { return null; }
    }

    private Double parseDouble(String value) {
        try {
            return value == null || value.isBlank() ? null : Double.parseDouble(value);
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
