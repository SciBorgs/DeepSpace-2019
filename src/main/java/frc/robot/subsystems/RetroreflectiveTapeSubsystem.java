package org.usfirst.frc.team1155.robot.subsystems;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;

import org.usfirst.frc.team1155.robot.Robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.command.Subsystem;

public class RetroreflectiveTapeSubsystem extends Subsystem {
    
	public final static double tapeHeight = .1397;// In meters
	public final static double tapeWidth = .0508;// In meters
	public final static double widthToHeight = tapeWidth / tapeHeight;
	
    public NetworkTable getTable() {
        return Robot.limelight.getCameraTable();
    }

    public double get(NetworkTable table, String variable) {
        return Robot.limelight.getTableData(table, variable);
    }

    // Below are helper functions for extractData()
    public Comparator<Hashtable<String,Double>> dataCompare = 
        (Hashtable<String,Double> pair1, Hashtable<String, Double> pair2) -> pair1.get("x") < pair2.get("x") ? 1 : -1; // Might need to paramaterize
    public double[] averagePos(Hashtable<String,Double> data1, Hashtable<String,Double> data2){
        return new double[]{(data1.get("x") + data2.get("x"))/2,
                            (data1.get("y") + data2.get("y"))/2};
    }
    public boolean facingLeft(double skew){return skew > -45;}
    public boolean facingRight(double skew) {return skew > -45;}
    public Hashtable<String,Double> createData(NetworkTable t, double nth){
        // Takes a snapshot of data and the nth contour you want and converts it into a hashtable
        Hashtable<String, Double> end = new Hashtable<String, Double>();
        end.put("nth",nth);
        end.put("x",get(t,"tx" + nth));
        end.put("y",get(t,"ty" + nth));
        end.put("s",get(t,"ts" + nth));
        end.put("a",get(t,"ta" + nth));
        return end;
    }

    public double[] center(ArrayList<Hashtable<String,Double>> values){
        boolean leftPair  =  facingLeft(values.get(1).get("s")) && isContour(values.get(1)) && isContour(values.get(0));
        boolean rightPair = facingRight(values.get(1).get("s")) && isContour(values.get(1)) && isContour(values.get(2));
        if (leftPair)
            return averagePos(values.get(0),values.get(1));
        else if (rightPair)
            return averagePos(values.get(2),values.get(1));
        else 
            return new double[]{};
    }

    public boolean isContour(Hashtable<String,Double> data){return data.get("a") != 0;}
    // Above are helper functions for extractData()

    public Hashtable<String,Double> extractData(){
        // Extracts data from the given data. Currently: Center position, distance, shift 
        NetworkTable t = getTable();
        Hashtable<String,Double> data = new Hashtable<String,Double>();
        String[] keys = new String[]{"centerX","centerY","distance","shift"};
        for (String key : keys){data.put(key,0.0);}
        // Find the center of the two retroflective pieces of tape that are facing
        // twoards each other!
        ArrayList<Hashtable<String,Double>> values = new ArrayList<Hashtable<String,Double>>();
        for(int i = 0; i < 3; i++) {values.add(createData(t,i));}
        values.sort(dataCompare);
        double[] centerPos = center(values);
        if (centerPos.length == 0){return data;}
        double distance = Math.sqrt(Robot.limelight.meterArea / values.get(1).get("a")) + Robot.limelight.cameraWidth; // Not sure wether this is the correct math
        double metersPerPixel = 
        		Math.sqrt(widthToHeight * Robot.limelight.imageHeight * Robot.limelight.imageWidth * values.get(1).get("a")) / tapeHeight;
        double shift = distance * metersPerPixel * centerPos[0];

        data.put("centerX",centerPos[0]);
        data.put("centerY", centerPos[1]);
        data.put("distance", distance);
        data.put("shift", shift);
        return data;
    }

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}