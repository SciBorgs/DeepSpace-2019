package frc.robot.helpers;

import java.io.Serializable;

public class Point implements Serializable {
    private static final long serialVersionUID = 2;

    public double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
