package com.org.dnbndhu.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage primaryStage;
    private static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        Parent root = FXMLLoader.load(
                MainApp.class.getResource("/ui/fxml/login.fxml")
        );

        scene = new Scene(root);

        // Attach all CSS once
        scene.getStylesheets().addAll(
                MainApp.class.getResource("/ui/styles/login.css").toExternalForm(),
                MainApp.class.getResource("/ui/styles/dashboard.css").toExternalForm(),
                MainApp.class.getResource("/ui/styles/sidebar.css").toExternalForm(),
                MainApp.class.getResource("/ui/styles/ViewMyStudents.css").toExternalForm(),
                MainApp.class.getResource("/ui/styles/attendance.css").toExternalForm(),
                MainApp.class.getResource("/ui/styles/PlacementPortal.css").toExternalForm(),
                MainApp.class.getResource("/ui/styles/EnrollNewStudent.css").toExternalForm()
        );

        stage.setTitle("Deenabandhu Service Learning");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    // Scene switching helper
    public static void setRoot(String fxml) {
        try {
            System.out.println("Loading: " + fxml);

            Parent root = FXMLLoader.load(
                    MainApp.class.getResource("/ui/fxml/" + fxml)
            );

            scene.setRoot(root);

        } catch (Exception e) {
            System.out.println("FAILED to load " + fxml);
            e.printStackTrace();
        }
    }

    public static void setRoot(Parent root) {
        scene.setRoot(root);
    }

    public static void main(String[] args) {
        launch(args);
    }
    public static void setRootWithNode(Parent node) {
        scene.setRoot(node);
    }
}
