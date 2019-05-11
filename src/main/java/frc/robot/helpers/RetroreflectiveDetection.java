package frc.robot.helpers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;

import edu.wpi.first.networktables.NetworkTable;
import frc.robot.Robot;
import frc.robot.subsystems.*;

public class RetroreflectiveDetection {

    public static NetworkTable getTable() {
        return Robot.limelightSubsystem.getCameraTable();
    }

    public static double get(NetworkTable table, String variable) {
        return Robot.limelightSubsystem.getTableData(table, variable);
    }

    // Below are helper functions for extractData()
    private static Comparator<Hashtable<String,Double>> contourComparator = 
        (Hashtable<String,Double> pair1, Hashtable<String, Double> pair2) -> 
        pair1.get("x") > pair2.get("x") ? 1 : -1;
    private static double[] averagePos(Hashtable<String,Double> data1, Hashtable<String,Double> data2){
        return new double[]{(data1.get("x") + data2.get("x"))/2,
                            (data1.get("y") + data2.get("y"))/2};
    }
    private static boolean facingLeft(double skew) {return skew > -45;}
    private static boolean facingRight(double skew){return skew < -45;}

    private static double screenPercentToRadians(double screenPer){
        // Takes where something is on the screen (-1 to 1) and converts it to the angle that it is being seen at in radians
        return Math.atan(screenPer * Math.tan(LimelightSubsystem.IMAGE_WIDTH));
    }

    private static Hashtable<String,Double> createData(NetworkTable t, int nth){
        // Takes a snapshot of data and the nth contour you want and converts it into a hashtable
        Hashtable<String, Double> end = new Hashtable<String, Double>();
        end.put("nth",(double) nth);
        end.put("a",get(t,"ta" + nth));
        double txPer = isContour(end) ? get(t,"tx" + nth) : 1.01; // B/c we sort by tx, we want the data that isn't a contour to be off to the side
        end.put("x",screenPercentToRadians(txPer));
        end.put("y",get(t,"ty" + nth));
        end.put("s",get(t,"ts" + nth));
        end.put("h",get(t,"th" + nth));
        return end;
    }
    
    private static boolean areContours(ArrayList<Hashtable<String,Double>> values, int c1, int c2){
        return isContour(values.get(c1)) && isContour(values.get(c2));
    }
    private static boolean leftPair(ArrayList<Hashtable<String,Double>> values){
        // checks if the left two contours are the correct pair
        return facingLeft(values.get(1).get("s")) && areContours(values,0,1);
    }
    private static boolean rightPair(ArrayList<Hashtable<String,Double>> values){
        // checks if the right two contours are the correct pair
        return facingRight(values.get(1).get("s")) && areContours(values,1,2);
    }

    private static double[] center(ArrayList<Hashtable<String,Double>> values){
        if (leftPair(values)){
            return averagePos(values.get(0),values.get(1));
        } else if (rightPair(values)) {
            return averagePos(values.get(2),values.get(1));
        } else { 
            return new double[]{};
        }
    }
    public static boolean isContour(Hashtable<String,Double> data){return data.get("a") != 0;}

    public static Hashtable<String,Double> extractData(){
        // Extracts data from the given data. Currently just the angle to the center
        NetworkTable t = getTable();
        Hashtable<String,Double> data = new Hashtable<String,Double>();
        String[] keys = new String[]{"detected","angle"};
        for (String key : keys){data.put(key,0.0);}
        // Find the center of the two retroflective pieces of tape that are facing twoards each other!
        ArrayList<Hashtable<String,Double>> values = new ArrayList<Hashtable<String,Double>>();
        for(int i = 0; i < 3; i++) {
            values.add(createData(t,i));
        }
        values.sort(contourComparator);
        double[] centerPos = center(values);
        if (centerPos.length != 0){
            System.out.println("detected");
            double tx0 = leftPair(values) ? values.get(0).get("x") : values.get(1).get("x");
            double tx1 = leftPair(values) ? values.get(1).get("x") : values.get(2).get("x");
            double angle = Math.atan((Math.tan(tx0) + Math.tan(tx1) / 2));
            data.put("detected",1.0);
            data.put("angle",angle); // In radians
        }
        return data;
    }

}