package com.org.dnbndhu.ui.controller;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;


public class EnrollNewStudentController implements Initializable {

    // === ROOT ===
    @FXML private BorderPane rootPane;

    // === FORM FIELDS ===
    @FXML private TextField nameField;
    @FXML private TextField ageField;
    @FXML private TextField disabilityTypeField;
    @FXML private TextField disabilityPercentField;
    @FXML private TextArea addressField;

    // === GROUPS ===
    @FXML private VBox photoBox;
    @FXML private VBox remainingFieldsBox;
    @FXML private HBox dobBox;
    @FXML private HBox genderBox;
    @FXML private FlowPane checklistPane;
    // === MODAL OVERLAY ===
    private StackPane modalOverlay;
// === UPLOADED DOCUMENTS (for OCR later) ===
private final Map<String, File> uploadedDocuments = new HashMap<>();
@FXML
private DatePicker applicationDatePicker;

@FXML private RadioButton maleRadio;
@FXML private RadioButton femaleRadio;

@FXML private RadioButton marriedRadio;
@FXML private RadioButton unmarriedRadio;
@FXML private RadioButton divorcedRadio;

@FXML private GridPane qualificationTable;
@FXML private GridPane familyTable;

@FXML private StackPane candidateNameBox;
@FXML private StackPane candidateSignBox;

@FXML private StackPane guardianNameBox;
@FXML private StackPane guardianSignBox;

@FXML private DatePicker startDatePicker;
@FXML private DatePicker endDatePicker;


private int qualificationRowCount = 0;
private int familyRowCount = 0;


    // ==============================
    // CANCEL → CLEAR EVERYTHING
    // ==============================
    @FXML
    private void onCancel() {
        clearAllInputs(rootPane);
        photoBox.getChildren().setAll(new Label("Upload Photo"));
    }

    // ==============================
    // SUBMIT → SAVE FILE // change later to ensure all docs are uploaded and all fields are filled
    // ==============================
    @FXML
    private void onSubmit() {
        try {
            if (nameField.getText().isBlank()) {
                showError("Validation Error", "Name is required");
                return;
            }

            Path saveDir = Paths.get("C:/ServiceLearning/Enrollments");
            Files.createDirectories(saveDir);

            Path file = saveDir.resolve(nameField.getText().replace(" ", "_") + ".txt");

            String content = """
                    NAME: %s
                    AGE: %s
                    DISABILITY TYPE: %s
                    DISABILITY %%: %s
                    ADDRESS: %s
                    """.formatted(
                    nameField.getText(),
                    ageField.getText(),
                    disabilityTypeField.getText(),
                    disabilityPercentField.getText(),
                    addressField.getText()
            );

            Files.writeString(file, content);
            showInfo("Success", "Enrollment saved successfully");

         

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to save enrollment");
        }
    }

    // ==============================
    // CLEAR LOGIC (RECURSIVE)
    // ==============================
    private void clearAllInputs(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof TextField tf) tf.clear();
            else if (node instanceof TextArea ta) ta.clear();
            else if (node instanceof CheckBox cb) cb.setSelected(false);
            else if (node instanceof RadioButton rb) rb.setSelected(false);
            else if (node instanceof Parent p) clearAllInputs(p);
        }
    }

    // ==============================
    // PHOTO LOADING
    // ==============================
    public void loadPhoto(Path imagePath) {
        ImageView iv = new ImageView(new Image(imagePath.toUri().toString()));
        iv.setFitWidth(140);
        iv.setPreserveRatio(true);
        photoBox.getChildren().setAll(iv);
    }

    // ==============================
    // ALERTS
    // ==============================
    private void showInfo(String title, String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    private void showError(String title, String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

private void showUploadDocumentsPopup() {

    // Root overlay
    modalOverlay = new StackPane();
    modalOverlay.setStyle("""
        -fx-background-color: rgba(255, 255, 255, 0.75);
    """);

    modalOverlay.setPickOnBounds(true); // block clicks behind

    // ================= MODAL CARD =================
    VBox modalCard = new VBox(25);
    modalCard.setAlignment(javafx.geometry.Pos.CENTER);
    modalCard.setMaxWidth(520);
    modalCard.setMaxHeight(600);
    modalCard.setStyle("""
        -fx-background-color: white;
        -fx-background-radius: 18;
        -fx-padding: 30;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0, 0, 8);
    """);

    Label title = new Label("Upload Documents");
    title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

    Button startBtn = new Button("Upload Documents");
    startBtn.getStyleClass().add("primary-btn");

    modalCard.getChildren().addAll(title, startBtn);
    modalOverlay.getChildren().add(modalCard);

    // Replace center with overlay
    Node originalCenter = rootPane.getCenter();
    rootPane.setCenter(new StackPane(originalCenter, modalOverlay));

    startBtn.setOnAction(e -> {
        modalCard.getChildren().setAll(
            buildFindDocumentsPane(() -> {
                // Remove overlay ONLY on submit
                rootPane.setCenter(originalCenter);
            })
        );
    });
}


private VBox buildFindDocumentsPane(Runnable onSubmitComplete){


    VBox root = new VBox(20);
    root.setStyle("-fx-padding: 25;");
    root.setAlignment(javafx.geometry.Pos.CENTER);

    Label header = new Label("Find Student Documents");
    header.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

    // Toggle buttons (PUNK / THA)
    ToggleGroup group = new ToggleGroup();
    ToggleButton punkBtn = new ToggleButton("PUNK");
    ToggleButton thaBtn = new ToggleButton("THA");

    punkBtn.setToggleGroup(group);
    thaBtn.setToggleGroup(group);

// === SLIDING SEGMENTED TOGGLE ===
StackPane toggleWrapper = new StackPane();
toggleWrapper.getStyleClass().add("segmented-toggle");

HBox buttonRow = new HBox();
buttonRow.setAlignment(javafx.geometry.Pos.CENTER);


punkBtn.setToggleGroup(group);
thaBtn.setToggleGroup(group);

punkBtn.getStyleClass().add("segment-btn");
thaBtn.getStyleClass().add("segment-btn");

buttonRow.getChildren().addAll(punkBtn, thaBtn);

// Sliding indicator
Region slider = new Region();
slider.getStyleClass().add("segment-slider");

toggleWrapper.getChildren().addAll(slider, buttonRow);



    VBox uploadArea = new VBox(12);
    uploadArea.setAlignment(javafx.geometry.Pos.CENTER);

    Button submitBtn = new Button("Submit");
    submitBtn.getStyleClass().add("primary-btn");

submitBtn.setOnAction(e -> {
    // 🔌 OCR integration hook (backend later)
    onSubmitComplete.run();
});


group.selectedToggleProperty().addListener((obs, old, selected) -> {
    uploadArea.getChildren().clear();

    if (selected == punkBtn) {
        uploadArea.getChildren().addAll(
            buildUploadButton("Upload Aadhar"),
            buildUploadButton("Upload PAN"),
            buildUploadButton("Upload Education"),
            buildUploadButton("Upload Photo")
        );
    } else if (selected == thaBtn) {
        uploadArea.getChildren().addAll(
            buildUploadButton("Upload Aadhar"),
            buildUploadButton("Upload PAN"),
            buildUploadButton("Upload Education"),
            buildUploadButton("Upload Photo"),
            buildUploadButton("Upload Medical Certificate"),
            buildUploadButton("Upload UDID")
        );
    }

 HBox submitRow = new HBox(submitBtn);
submitRow.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
submitRow.setMaxWidth(300); // controls how far right it goes
submitRow.setStyle("-fx-padding: 10 0 0 0;"); // little down spacing

uploadArea.getChildren().add(submitRow);
submitBtn.setDisable(false);

});

root.getChildren().addAll(header, toggleWrapper, uploadArea);
return root;
}


private Button buildUploadButton(String text) {
    Button btn = new Button(text);
    btn.setPrefWidth(260);
    btn.getStyleClass().add("pill");

    btn.setOnAction(e -> {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(text);

        // Allow PDFs and images (typical OCR inputs)
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.png", "*.jpg", "*.jpeg"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(
            rootPane.getScene().getWindow()
        );

        if (selectedFile != null) {
       
uploadedDocuments.put(text, selectedFile);

// 👇 SPECIAL CASE: PHOTO
if (text.equalsIgnoreCase("Upload Photo")) {
    loadPhoto(selectedFile.toPath());
}

// Visual feedback
btn.setText("✔ " + text);
btn.setDisable(true);

System.out.println(text + " selected: " + selectedFile.getAbsolutePath());

        }
    });

    return btn;
}

private void setupQualificationHeader() {

    qualificationTable.getChildren().clear();

    qualificationTable.add(headerCell("S.No"), 0, 0);
    qualificationTable.add(headerCell("Education"), 1, 0);
    qualificationTable.add(headerCell("College"), 2, 0);
    qualificationTable.add(headerCell("Board"), 3, 0);
    qualificationTable.add(headerCell("Year"), 4, 0);

    qualificationRowCount = 0;
}

private void setupFamilyHeader() {

    familyTable.getChildren().clear();

    familyTable.add(headerCell("Name"), 0, 0);
    familyTable.add(headerCell("Relationship"), 1, 0);
    familyTable.add(headerCell("Income"), 2, 0);
    familyTable.add(headerCell("Cell No"), 3, 0);

    familyRowCount = 0;
}

private Label headerCell(String text) {
    Label label = new Label(text);
    label.getStyleClass().add("table-header");
    label.setMaxWidth(Double.MAX_VALUE);
    label.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
    return label;
}


@FXML
private void addQualificationRow() {

    int row = qualificationTable.getRowCount(); // next available row

    Label slNo = new Label(); // value will be set later

    TextField edu = new TextField();
    TextField college = new TextField();
    TextField board = new TextField();
    TextField year = new TextField();

    Button deleteBtn = createDeleteButton(
            qualificationTable,
            slNo, edu, college, board, year
    );

    qualificationTable.add(slNo, 0, row);
    qualificationTable.add(edu, 1, row);
    qualificationTable.add(college, 2, row);
    qualificationTable.add(board, 3, row);
    qualificationTable.add(year, 4, row);
    qualificationTable.add(deleteBtn, 5, row);

    refreshQualificationSerialNumbers();
}


@FXML
private void addFamilyRow() {

    int row = familyTable.getRowCount();

    Label dummy = new Label(); // not displayed, just for row tracking

    TextField name = new TextField();
    TextField relation = new TextField();
    TextField income = new TextField();
    TextField cell = new TextField();

    Button deleteBtn = createDeleteButton(
        familyTable,
        dummy,
        name, relation, income, cell
    );

    familyTable.add(dummy, 0, row);
    familyTable.add(name, 0, row);
    familyTable.add(relation, 1, row);
    familyTable.add(income, 2, row);
    familyTable.add(cell, 3, row);
    familyTable.add(deleteBtn, 4, row);
}



private Button createDeleteButton(
        GridPane table,
        Label slNo,
        TextField... fields
) {
    Button btn = new Button("−");
    btn.getStyleClass().add("table-delete-btn");

    btn.setOnAction(e -> {

        boolean hasData = false;
        for (TextField tf : fields) {
            if (!tf.getText().isBlank()) {
                hasData = true;
                break;
            }
        }

        Runnable deleteAction = () -> {
            Integer row = GridPane.getRowIndex(slNo);
            if (row != null) {
                removeRow(table, row);
                refreshQualificationSerialNumbers();
            }
        };

        if (hasData) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setHeaderText("Delete Row?");
            confirm.setContentText("This row contains data. Are you sure?");
            confirm.showAndWait().ifPresent(r -> {
                if (r.getButtonData().isDefaultButton()) {
                    deleteAction.run();
                }
            });
        } else {
            deleteAction.run();
        }
    });

    return btn;
}


private void removeRow(GridPane table, int row) {
    table.getChildren().removeIf(node -> {
        Integer r = GridPane.getRowIndex(node);
        return r != null && r == row;
    });
}


private void refreshQualificationSerialNumbers() {

    qualificationTable.getChildren().stream()
        .filter(node -> node instanceof Label)
        .filter(node -> {
            Integer col = GridPane.getColumnIndex(node);
            Integer row = GridPane.getRowIndex(node);
            return col != null && col == 0 && row != null && row > 0;
        })
        .sorted((a, b) ->
            GridPane.getRowIndex(a) - GridPane.getRowIndex(b)
        )
        .forEach(new java.util.function.Consumer<Node>() {
            int counter = 1;
            @Override
            public void accept(Node node) {
                ((Label) node).setText(String.valueOf(counter++));
            }
        });
}


private void setupSignatureUpload(StackPane box) {

    box.setOnMouseClicked(e -> {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Upload Image");
        chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = chooser.showOpenDialog(rootPane.getScene().getWindow());
        if (file == null) return;

        ImageView iv = new ImageView(new Image(file.toURI().toString()));
        iv.setPreserveRatio(true);
        iv.setFitHeight(40); // signature-sized
        iv.setSmooth(true);

        box.getChildren().setAll(iv);
    });
}





@Override
public void initialize(URL location, ResourceBundle resources) {

    javafx.application.Platform.runLater(this::showUploadDocumentsPopup);
    applicationDatePicker.setValue(java.time.LocalDate.now());

    // Gender
    ToggleGroup genderGroup = new ToggleGroup();
    maleRadio.setToggleGroup(genderGroup);
    femaleRadio.setToggleGroup(genderGroup);

    // Marital status
    ToggleGroup maritalGroup = new ToggleGroup();
    marriedRadio.setToggleGroup(maritalGroup);
    unmarriedRadio.setToggleGroup(maritalGroup);
    divorcedRadio.setToggleGroup(maritalGroup);

    // 🔥 TABLE SETUP (THIS WAS MISSING)
    setupQualificationHeader();
    addQualificationRow();

    setupFamilyHeader();
    addFamilyRow();
    setupSignatureUpload(candidateNameBox);
setupSignatureUpload(candidateSignBox);
setupSignatureUpload(guardianNameBox);
setupSignatureUpload(guardianSignBox);
startDatePicker.setValue(java.time.LocalDate.now());
    endDatePicker.setValue(java.time.LocalDate.now().plusMonths(3));

}



}
