package frc.robot.subsystems;

import frc.robot.Robot;
import java.util.Hashtable;

import edu.wpi.first.wpilibj.command.Subsystem;

class Point{
    public double x, y;
    public Point(double point_x, double point_y) {x = point_x; y = point_y;}
}
class Line{
    public double m, b;
    public Line(double m_des, double b_des) {m = m_des; b = b_des;}
}

public class LidarSubsystem extends Subsystem {

    public double wallPrecision = 0.03; // The amount a point can be off the line of the wall and be considerned on it
    public double offWallPrecision = wallPrecision * 3/4; // How close a point can be to the line of a wall and be considered off it
    // Note: offWallPrecision and wallPrecision are different!
    public double wallLengthReq = 0.1; // How far the points need to be on the line for so that it can be considered a wall
    public double wallMeasureReq = 6; // How many points need to be on the line for so that it can be considered a wall
    public double firstAngle = 270; // This is assuming the first piece of data is backwards
    public double deltaTheta = -1;

    public Point toPoint(double l, double theta) {return new Point(l * Math.cos(theta), l * Math.sin(theta));}
    public Point center(Point p1, Point p2)      {return new Point((p1.x + p2.x)/2,(p1.y + p2.y)/2);}
    public Line pointSlopeForm(Point p, double m){return new Line(m, p.y - m * p.x);}
    public double yOf(Line l, double x)          {return l.m * x + l.b;}
    public Line twoPointForm(Point p1, Point p2) {return pointSlopeForm(p1,(p2.y - p1.y)/(p2.x - p1.x));}
    public double distance(Point p1, Point p2)   {return Math.sqrt(Math.pow((p1.x - p2.x),2) + Math.pow((p1.y - p2.y),2));}   
    public Point pointOfX(Line l, double x)      {return new Point(x,yOf(l,x));}
    public Point intersection(Line l1, Line l2)  {return pointOfX(l1, (l1.b - l2.b)/(l2.m - l1.m));}
    public Line perpindicular(Line l, Point p)   {return pointSlopeForm(p, -1/l.m);}
    public double distanceToLine(Line l, Point p){return distance(p,intersection(perpindicular(l,p),l));}
    public boolean pointOnLine(Line l, Point p)  {return distanceToLine(l,p) < wallPrecision;}
    public boolean pointOffLine(Line l, Point p) {return distanceToLine(l,p) > offWallPrecision;} // Not the same as not(pointOnLine)!!
    

    public boolean isWallSize(Point[] points, int index1, int index2){
        double l = distance(points[index1],points[index2]); 
        double meas = Math.abs(index1 - index2) + 1;
        return (meas >= wallMeasureReq) && (l >= wallLengthReq);
    }

    public Hashtable<String,Double> isHatch(Point[] points){
        Line wall = twoPointForm(points[0],points[points.length - 1]);
        int leftWallEnd = 0;
        int rightWallEnd = points.length - 1;
        while(pointOnLine(wall,points[leftWallEnd])) {leftWallEnd++;}
        while(pointOnLine(wall,points[rightWallEnd])){rightWallEnd++;}
        int offWallEnd = leftWallEnd;
        while(pointOffLine(wall,points[offWallEnd])){offWallEnd++;}
        boolean isLeftWall  = isWallSize(points,0,leftWallEnd);
        boolean isRightWall = isWallSize(points,points.length - 1, rightWallEnd);
        Hashtable<String,Double> data = new Hashtable<String,Double>();
        data.put("detected", (isLeftWall && isRightWall & (offWallEnd >= rightWallEnd)) ? 1.0 : 0.0);
        if (data.get("detected") == 0) {return data;}
        Point hatchCenter = center(points[rightWallEnd],points[leftWallEnd]);
        data.put("shift", hatchCenter.x);
        data.put("parallel", hatchCenter.y);
        data.put("angle", Math.atan(wall.m));
        return data;
    }

    public Hashtable<String,Double> hatchInfo(double[] distances, double leftTheta, double rightTheta){
        int angleOn = (int) firstAngle;
        Point[] points = new Point[(int) ((rightTheta - leftTheta)/deltaTheta)];
        while(angleOn < leftTheta){angleOn++;}
        while(angleOn < rightTheta){
            points[angleOn] = toPoint(distances[angleOn],angleOn);
            angleOn++;
        }
        return isHatch(points);
    }
    
    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}