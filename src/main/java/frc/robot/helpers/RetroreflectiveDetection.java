package frc.robot.helpers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;

import edu.wpi.first.networktables.NetworkTable;
import frc.robot.Robot;
import frc.robot.subsystems.*;

public class RetroreflectiveDetection {

    public final static double METER_AREA = 0.62; // In percent
    public final static double CAMERA_WIDTH = 0.015; // In meters
    public final static double TAPE_LENGTH = .1397; // In meters
    public final static double TAPE_WIDTH = .0508; // In meters

    public final static double TAPE_ANGLE = .24434; // In radians, approximation according to FRC
    public final static double SEPERATION = .31; // Distance between the centers about

    public static void modeToRetroreflectiveByLimitSwitch() {
        if (stateOfLimitSwitch()) {modeToRetroreflective();}
    }

    public static boolean stateOfLimitSwitch() { // True is closed, false is open
        return (!Robot.ballLimitSwitch.get() || !Robot.hatchLimitSwitch.get());
    }

    public static void modeToRetroreflective() {
        Robot.limelightSubsystem.setCameraParams("ledMode", 3); // Force LED Off
        Robot.limelightSubsystem.setCameraParams("pipeline", 0); // Switch to Retroreflective Tape Pipeline
    }

    public static NetworkTable getTable() {
        return Robot.limelightSubsystem.getCameraTable();
    }

    public static double get(NetworkTable table, String variable) {
        return Robot.limelightSubsystem.getTableData(table, variable);
    }

    // Below are helper functions for extractData()
    public static Comparator<Hashtable<String,Double>> dataCompare = 
        (Hashtable<String,Double> pair1, Hashtable<String, Double> pair2) -> pair1.get("x") > pair2.get("x") ? 1 : -1; // Might need to paramaterize
    public static double[] averagePos(Hashtable<String,Double> data1, Hashtable<String,Double> data2){
        return new double[]{(data1.get("x") + data2.get("x"))/2,
                            (data1.get("y") + data2.get("y"))/2};
    }
    public static boolean facingLeft(double skew){
        return skew > -45;
    }
    public static boolean facingRight(double skew){
        return skew < -45;
    }

    public static double screenPercentToDegrees(double screenPer){
        return Math.atan(screenPer * Math.tan(Math.toRadians(LimelightSubsystem.IMAGE_WIDTH)));
    }

    public static Hashtable<String,Double> createData(NetworkTable t, int nth){
        // Takes a snapshot of data and the nth contour you want and converts it into a hashtable
        Hashtable<String, Double> end = new Hashtable<String, Double>();
        end.put("nth",(double) nth);
        end.put("a",get(t,"ta" + nth));
        double txPer = isContour(end) ? get(t,"tx" + nth) : 1.01; // B/c we sort by tx, we want the data that isn't a contour to be off to the side
        end.put("x",screenPercentToDegrees(txPer));
        end.put("y",get(t,"ty" + nth));
        end.put("s",get(t,"ts" + nth));
        return end;
    }

    public static double getAngle(double skew){
        double firstQuadAngle = skew + 90;
        if (firstQuadAngle > 45) {firstQuadAngle = 90 - firstQuadAngle;}
        return Math.acos(Math.tan(Math.toRadians(firstQuadAngle))/Math.tan(TAPE_ANGLE));
    }

    public static boolean areContours(ArrayList<Hashtable<String,Double>> values, int c1, int c2){
        return isContour(values.get(c1)) && isContour(values.get(c2));
    }
    public static boolean leftPair(ArrayList<Hashtable<String,Double>> values){
        return facingLeft(values.get(1).get("s")) && areContours(values,0,1);
    }
    public static boolean rightPair(ArrayList<Hashtable<String,Double>> values){
        return facingRight(values.get(1).get("s")) && areContours(values,1,2);
    }

    public static double[] center(ArrayList<Hashtable<String,Double>> values){
        if (leftPair(values)){
            return averagePos(values.get(0),values.get(1));
        } else if (rightPair(values)) {
            return averagePos(values.get(2),values.get(1));
        } else { 
            return new double[]{};
        }
    }

    public static double distance(Hashtable<String,Double> value){
        return Math.sqrt(METER_AREA / value.get("a")) + CAMERA_WIDTH;
    }
    public static double theta(ArrayList<Hashtable<String,Double>> values){
        double d1 = rightPair(values) ? distance(values.get(2)) : distance(values.get(1));
        double d2 = leftPair(values) ? distance(values.get(0)) : distance(values.get(1));
        //System.out.println("d1: " + d1 * 39.37);
        //System.out.println("d2: " + d2 * 39.37);
        return Math.asin((d1 - d2)/SEPERATION); 
    }
    public static boolean isContour(Hashtable<String,Double> data){
        return data.get("a") != 0;}
    // Above are helper functions for extractData()

    public static Hashtable<String,Double> extractData(){
        // Extracts data from the given data. Currently: Center position, distance, shift 
        NetworkTable t = getTable();
        Hashtable<String,Double> data = new Hashtable<String,Double>();
        String[] keys = new String[]{"detected","centerX","centerY","distance","shift","angle","shiftL","shiftR","rotation"};
        for (String key : keys){
            data.put(key,0.0);
        }
        // Find the center of the two retroflective pieces of tape that are facing
        // twoards each other!
        ArrayList<Hashtable<String,Double>> values = new ArrayList<Hashtable<String,Double>>();
        for(int i = 0; i < 3; i++) {
            values.add(createData(t,i));
        }
        values.sort(dataCompare);
        double[] centerPos = center(values);
        if (centerPos.length == 0){
            return data;
        }
        System.out.println("detected");
        double distance = distance(values.get(1));
        double tx0 = values.get(0).get("x");
        double tx1 = values.get(1).get("x");
        double tx2 = values.get(2).get("x");
        double adjustBy = LimelightSubsystem.SHIFT;
        double shiftL = distance * (leftPair(values) ? Math.tan(tx0) : Math.tan(tx1)) + adjustBy; //Negative to the Left, Positive to the Right
        double shiftR = distance * (leftPair(values) ? Math.tan(tx1) : Math.tan(tx2)) + adjustBy;
        double shift = (shiftL + shiftR) / 2;
        double rotation = theta(values);
        data.put("detected",1.0);
        data.put("rotation", rotation);
        data.put("angle",Math.atan(shift/distance));
        data.put("shiftL",shiftL);
        data.put("shiftR",shiftR);
        data.put("centerX",centerPos[0]);
        data.put("centerY", centerPos[1]);
        data.put("distance", distance);
        data.put("shift", shift);
        return data;
    }

}