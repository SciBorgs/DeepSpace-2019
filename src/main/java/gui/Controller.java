package gui;

import frc.robot.subsystems.Point;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;


public class Controller extends Pane {
    Stage stage;
    public Controller(Stage stage) {
        this.stage = stage;
    }

    public void addPoints(Point[] points) {
        for (Point point : points) {
            Line line = new Line();
            line.setStartX((stage.getWidth() / 2) + (point.x * 25));
            line.setEndX((stage.getWidth() / 2) + (point.x * 25));
            line.setStartY((stage.getHeight() / 2) - (point.y * 25));
            line.setEndY((stage.getHeight() / 2) - (point.y * 25));
            line.setStrokeWidth(5d);
            line.setStroke(Color.BLACK);
            getChildren().add(line);
        }

    }
}
