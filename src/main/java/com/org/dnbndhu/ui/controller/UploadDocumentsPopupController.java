package com.org.dnbndhu.ui.controller;

import com.org.dnbndhu.domain.dto.EnrollmentDraftDTO;
import com.org.dnbndhu.service.ocr.OCRFieldExtractorService;
import com.org.dnbndhu.service.ocr.OCRService;
import com.org.dnbndhu.ui.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Map;

public class UploadDocumentsPopupController {

    @FXML private VBox initialStep;
    @FXML private VBox uploadStep;
    @FXML private VBox uploadArea;

    @FXML private ToggleButton punkBtn;
    @FXML private ToggleButton thaBtn;

    private final EnrollmentDraftDTO draft = new EnrollmentDraftDTO();

    private final OCRService ocrService = new OCRService();
    private final OCRFieldExtractorService extractorService = new OCRFieldExtractorService();

    @FXML
    private void onStartUpload() {
        initialStep.setVisible(false);
        initialStep.setManaged(false);

        uploadStep.setVisible(true);
        uploadStep.setManaged(true);
    }

    @FXML
    private void initialize() {

        ToggleGroup group = new ToggleGroup();
        punkBtn.setToggleGroup(group);
        thaBtn.setToggleGroup(group);

        group.selectedToggleProperty().addListener((obs, old, selected) -> {

            uploadArea.getChildren().clear();

            if (selected == punkBtn) {
                addUploadButtons(false);
            } else if (selected == thaBtn) {
                addUploadButtons(true);
            }
        });
    }

    private void addUploadButtons(boolean isTha) {

        uploadArea.getChildren().add(buildUploadButton("PHOTO"));
        uploadArea.getChildren().add(buildUploadButton("LIVE_PHOTO"));
        uploadArea.getChildren().add(buildUploadButton("EDUCATION_CERTIFICATE"));
        uploadArea.getChildren().add(buildUploadButton("AADHAR_CARD"));
        uploadArea.getChildren().add(buildUploadButton("PAN_CARD"));
        uploadArea.getChildren().add(buildUploadButton("BANK_PASSBOOK"));

        if (isTha) {
            uploadArea.getChildren().add(buildUploadButton("UDID_CARD"));
            uploadArea.getChildren().add(buildUploadButton("MEDICAL_CERTIFICATE"));
        }
    }

    private javafx.scene.control.Button buildUploadButton(String documentType) {

        javafx.scene.control.Button btn =
                new javafx.scene.control.Button("Upload " + documentType.replace("_", " "));
        btn.setPrefWidth(260);

        btn.setOnAction(e -> {

            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.png", "*.jpg", "*.jpeg")
            );

            File file = chooser.showOpenDialog(btn.getScene().getWindow());

            if (file != null) {

                try {
                    // Store document metadata in draft
                    draft.addDocument(documentType, file.getAbsolutePath());

                    // Run OCR
                    if (documentType.equals("AADHAR_CARD") ||
                            documentType.equals("PAN_CARD") ||
                            documentType.equals("BANK_PASSBOOK") ||
                            documentType.equals("UDID_CARD")) {

                        String text = ocrService.extractText(String.valueOf(file));

                        Map<String, String> extracted =
                                extractorService.extractFields(documentType, text);

                        extracted.forEach(draft::putField);
                    }

                    btn.setText("✔ " + documentType.replace("_", " "));
                    btn.setDisable(true);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        return btn;
    }

    @FXML
    private void onSubmitDocuments() {

        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/ui/fxml/EnrollNewStudent.fxml"));

            Parent root = loader.load();

            EnrollNewStudentController controller =
                    loader.getController();

            controller.setDraft(draft);

            MainApp.setRootWithNode(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}