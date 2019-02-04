package gui;

import frc.robot.subsystems.Point;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    private static Controller controller;

    public static void main(String... args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        final String version = "0.1.0";

        stage.setHeight(500);
        stage.setWidth(500);
        controller = new Controller(stage);
        stage.setTitle("SciBorgs LIDAR GUI - Version " + version);
        stage.setScene(new Scene(controller));

        stage.setResizable(false);
        stage.toFront();
        stage.show();
    }

    public static void addPoints(Point[] points) {
        controller.addPoints(points);
    }
}