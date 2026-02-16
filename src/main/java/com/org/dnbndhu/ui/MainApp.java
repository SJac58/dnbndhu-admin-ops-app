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
                MainApp.class.getResource("/fxml/login.fxml")
        );

        scene = new Scene(root);

        // 🔥 ATTACH ALL CSS ONCE (THIS IS THE KEY)
        scene.getStylesheets().addAll(
                MainApp.class.getResource("/css/login.css").toExternalForm(),
                MainApp.class.getResource("/css/dashboard.css").toExternalForm(),
                MainApp.class.getResource("/css/sidebar.css").toExternalForm(),
                MainApp.class.getResource("/css/ViewMyStudents.css").toExternalForm(),
                MainApp.class.getResource("/css/attendance.css").toExternalForm(),
                MainApp.class.getResource("/css/PlacementPortal.css").toExternalForm(),
                MainApp.class.getResource("/css/EnrollNewStudent.css").toExternalForm()
        );

        stage.setTitle("Deenabandhu Service Learning");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    // ===== Scene switching helper =====
    public static void setRoot(String fxml) {
        try {
            System.out.println("Loading: " + fxml);

            Parent root = FXMLLoader.load(
                    MainApp.class.getResource("/fxml/" + fxml)
            );

            scene.setRoot(root); // 👈 IMPORTANT: reuse same scene

        } catch (Exception e) {
            System.out.println("FAILED to load " + fxml);
            e.printStackTrace();
        }
    }

    // ===== Scene switching with preloaded root =====
public static void setRoot(Parent root) {
    scene.setRoot(root);
}


    public static void main(String[] args) {
        launch(args);
    }
}
