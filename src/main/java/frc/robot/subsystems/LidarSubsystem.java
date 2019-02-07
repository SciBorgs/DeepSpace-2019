package frc.robot.subsystems;

import frc.robot.Robot;
import frc.robot.Utils;

import java.util.Hashtable;
import frc.robot.subsystems.LidarServer;

import edu.wpi.first.wpilibj.command.Subsystem;
//import gui.Main;

class Line{
    public double m, b;
    public Line(double m_des, double b_des) {m = m_des; b = b_des;}
}

public class LidarSubsystem extends Subsystem{
    public LidarSubsystem() {
        //Main.main();
    }


    public double wallPrecision = 0.04; // The amount a point can be off the line of the wall and be considerned on it
    public double offWallPrecision = wallPrecision * 1/2; // How close a point can be to the line of a wall and be considered off it
    // Note: offWallPrecision and wallPrecision are different!
    public double wallLengthReq = 0.04; // How far the points need to be on the line for so that it can be considered a wall
    public int wallMeasureReq = 5; // How many points need to be on the line for so that it can be considered a wall
    public double firstAngle = 0; // This is assuming the first piece of data is backwards
    public double finalAngle = 360;
    public double deltaTheta = 1;
    public double gap = 8 / Utils.metersToInches;
    public double gapOffset = .0;
    public double gapPrecision = .15;
    public double lidarShift = 0; // In meters away from the center

    public Point toPoint(double l, double theta)   {return new Point(l * Math.cos(theta), l * Math.sin(theta));}
    public Point toPointDeg(double l, double theta){return toPoint(l,Math.toRadians(theta));}
    public Point center(Point p1, Point p2)        {return new Point((p1.x + p2.x)/2,(p1.y + p2.y)/2);}
    public Line pointSlopeForm(Point p, double m)  {return new Line(m, p.y - m * p.x);}
    public double yOf(Line l, double x)            {return l.m * x + l.b;}
    public Line twoPointForm(Point p1, Point p2)   {return pointSlopeForm(p1,(p2.y - p1.y)/(p2.x - p1.x));}
    public double distance(Point p1, Point p2)     {return Math.sqrt(Math.pow((p1.x - p2.x),2) + Math.pow((p1.y - p2.y),2));}   
    public Point pointOfX(Line l, double x)        {return new Point(x,yOf(l,x));}
    public Point intersection(Line l1, Line l2)    {return pointOfX(l1, (l1.b - l2.b)/(l2.m - l1.m));}
    public Line perpindicular(Line l, Point p)     {return pointSlopeForm(p, -1/l.m);}
    public double distanceToLine(Line l, Point p)  {return distance(p,intersection(perpindicular(l,p),l));}
    public boolean pointOnLine(Line l, Point p)    {return distanceToLine(l,p) < wallPrecision;}
    public boolean pointOffLine(Line l, Point p)   {return distanceToLine(l,p) > offWallPrecision;} // Not the same as not(pointOnLine)!!
    public double length(Point p)                  {return distance(p,new Point(0,0));}
    public boolean isOrigin(Point p)               {return length(p) == 0;}
    public Point angleOnLine(double theta, Line l) {return intersection(l, pointSlopeForm(new Point(0,0), Math.tan(theta)));}

    public double getLength(Hashtable<Double,Double> h, double angle){
        return h.containsKey(angle) ? h.get(angle) / 1000 : 0;
    }
    public double getLengthI(Hashtable<Double,Double> h, int index){
        return getLength(h,indexToAngle(index));
    }
    public double makeAngleInRange(double angle){
        while(angle < 0){angle += 360;}
        return angle % 360;
    }
    public int makeIndexInRange(int i, int max){
        while(i < 0){i += max;}
        return i % max;
    }
    public Point[] polarHashToPoints(Hashtable<Double,Double> polarPoints){
        int length = (int) ((finalAngle - firstAngle) / deltaTheta);
        Point[] points = new Point[length];
        for(int i = 0; i < length; i++){
            double l = getLengthI(polarPoints,i);
            points[i] = toPointDeg(l,indexToAngle(i));
        }
        for(int i = 0; i < length; i++)
            inferPoint(points, i);
        
        return points;
    }

    public Point[] fetchScan(){
        return polarHashToPoints(LidarServer.getInstance().lidarScan);
    }
    public double angleDistance(double angle){
        return distance(fetchScan()[indexToAngle(angle)]);
    }

    public void printScan(){
        Hashtable<Double,Double> data = LidarServer.getInstance().lidarScan;
        
        System.out.println("size: " + data.size());
        if (data.size() > 0){
            System.out.print("[");
            for (double angle = 0; angle < 360; angle++){
                if (data.containsKey(angle))
                    System.out.print("(" + angle + "," + data.get(angle) + "), ");
            }
            System.out.println("]");
        }
    }

    public int angleToIndex(double angle){
        return (int) ((angle % 360 + firstAngle % 360) / deltaTheta);
    }
    public double indexToAngle(int index){
        return index * deltaTheta + firstAngle;
    }
    public double nextAngle(double angle){return makeAngleInRange(angle + deltaTheta);}
    public int nextIndex(int index, int max){return makeIndexInRange(index + 1, max);}
    public int prevIndex(int index, int max){return makeIndexInRange(index - 1, max);}
    public double wallRotation(double a1, double a2){
        Point[] points = fetchScan();
        Line wall = twoPointForm(points[angleToIndex(a1)],points[angleToIndex(a2)]);
        return Math.atan(wall.m);
    }

    public boolean isWallSize(Point[] points, int index1, int index2){
        double l = distance(points[index1],points[index2]); 
        double meas = Math.abs(index1 - index2) + 1;
        return (meas >= wallMeasureReq) && (l >= wallLengthReq);
    }

    public boolean isWall(Point[] points, double a1, double a2){
        int i1 = angleToIndex(a1);
        int i2 = angleToIndex(a2);
        Point p1 = points[i1];
        Point p2 = points[i2];
        Line wall = twoPointForm(p1,p2);
        for(double angle = a1;  Math.abs(a2 - angle) > deltaTheta / 2; angle = (angle + deltaTheta) % 360){
            if (!(pointOnLine(wall,points[angleToIndex(angle)]))){
                return false;
            }
        }
        return isWallSize(points, i1, i2);
    }

    public void inferPoint(Point[] points, int i){
        if (!isOrigin(points[i])) {return;}
        int l = points.length;
        Point leftInfer = isOrigin(points[prevIndex(i,l)]) ? points[makeIndexInRange(i - 2, l)] : points[prevIndex(i,l)];
        Point rightInfer = isOrigin(points[nextIndex(i,l)]) ? points[makeIndexInRange(i + 2, l)] : points[nextIndex(i,l)];
        if (isOrigin(leftInfer) || isOrigin(rightInfer)){return;}
        points[i] = center(leftInfer,rightInfer);
    }

    public double minLength(Point[] points, int i1, int i2){
        double minVal = length(points[i2]);
        for (int i = i1; i != i2; i = nextIndex(i,points.length)){
            double len = length(points[i]);
            if (len < minVal && len != 0)
                minVal = len;
        }
        return minVal;
    }
    public double minLength(double a1, double a2){
        return minLength(fetchScan(),angleToIndex(a1),angleToIndex(a2));
    }
    
    public int maxLengthIndex(Point[] points, int i1, int i2){
        double maxVal = length(points[i2]);
        int maxIndex = i2;
        for (int i = i1; i != i2; i = nextIndex(i,points.length)){
            double len = length(points[i]);
            if (len > maxVal && len != 0)
                maxVal = len;
                maxIndex = i;
        }
        return maxIndex;
    }
    public double maxLengthAngle(double a1, double a2){
        return indexToAngle(maxLengthIndex(fetchScan(),angleToIndex(a1),angleToIndex(a2)));
    }

    public Hashtable<String,Double> isHatch(Point[] points, int i1, int i2){
        Hashtable<String,Double> data = new Hashtable<String,Double>();
        if (isWall(points,i1,i2)) {
            data.put("detected",0.0);
            return data;
        }
        Line wall = twoPointForm(points[i1],points[i2]);
        int leftWallEnd = i1;
        int rightWallEnd = i2;
        while(pointOnLine(wall,points[leftWallEnd])) {leftWallEnd = nextIndex(leftWallEnd,points.length);}
        while(pointOnLine(wall,points[rightWallEnd])){rightWallEnd = prevIndex(rightWallEnd,points.length);}
        int offWallEnd = leftWallEnd;
        while(pointOffLine(wall,points[offWallEnd]) && offWallEnd != rightWallEnd){offWallEnd = nextIndex(offWallEnd,points.length);}
        boolean isLeftWall  = isWallSize(points,i1,leftWallEnd);
        boolean isRightWall = isWallSize(points,rightWallEnd,i2);
        double leftAngle = Math.toRadians(indexToAngle(leftWallEnd));
        double rightAngle = Math.toRadians(indexToAngle(rightWallEnd));
        Point leftPoint = angleOnLine(leftAngle,wall);
        Point rightPoint = angleOnLine(rightAngle,wall);
        double hatchGap = distance(leftPoint,rightPoint);
        double gapError = hatchGap - (gap + gapOffset);
        boolean isHatchGap = Math.abs(gapError) < gapPrecision;
        //System.out.println("gap: " + hatchGap);
        //System.out.println("gap error: " + gapError);
        //System.out.println("left wall: " + leftAngle);
        //System.out.println("left distance: " + points[i1].x * Utils.metersToInches);
        //System.out.println("right distance: " + points[i2].x * Utils.metersToInches);
        //System.out.println("right wall: " + rightAngle);
        //System.out.println("off wall: " + indexToAngle(offWallEnd));
        //System.out.println("wall rotation: " + Math.toDegrees(Math.atan(wall.m)));
        data.put("detected", (isLeftWall && isRightWall & (offWallEnd >= rightWallEnd)) ? 1.0 : 0.0);
        if (data.get("detected") == 0) {return data;}
        int rightWallPoint = makeIndexInRange(rightWallEnd + wallMeasureReq,points.length);
        int leftWallPoint  = makeIndexInRange(leftWallEnd - wallMeasureReq, points.length);
        Point hatchCenter = center(points[rightWallPoint],points[leftWallPoint]);
        data.put("shift", hatchCenter.x);
        data.put("parallel", hatchCenter.y);
        data.put("angle", Math.atan(wall.m));
        return data;
    }

    public Hashtable<String,Double> hatchInfo(double leftTheta, double rightTheta){
        return isHatch(fetchScan(),angleToIndex(leftTheta),angleToIndex(rightTheta));
    }

    public void displayPoints(Point[] points){
        //sMain.addPoints(points);
    }
    
    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}