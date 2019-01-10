package main.java.frc.robot.subsystems;

import org.usfirst.frc.team1155.robot.Robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.PWMTalonSRX;
import edu.wpi.first.wpilibj.command.Subsystem;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import java.util.Arrays;
import java.util.Hasthable;

public class RetroreflectiveTapeSubsystem extends Subsystem {

    public final static double meterDegreeLength = .02; // Needs to be measured
    public final static double meterArea = 0.615; // In percent

    public NetworkTable getTable() {
        return Robot.limelight.getCameraTable();
    }

    public double get(NetworkTable table, String variable) {
        return Robot.limelight.getTableDat(table, variable);
    }

    // Below are helper functions for extractData()
    public boolean dataCompare(Hashtable<String,Double> pair1, Hashtable<String,Double> pair2){
        return pair1.get("x") > pair2.get("x");
    } 
    public double[] averagePos(Hashtable<String,Double> data1, Hashtable<String,Double> data2){
        return new double[]{(data1.get("x") + data2.get("x"))/2,
                            (data1.get("y") + data2.get("y"))/2};
    }
    public boolean facingLeft(double skew){return skew > -45;}
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

    public double[] center(Hashtable<String,Double>[] values){
        boolean leftPair = facingLeft(values[1].get("s"));
        if (leftPair)
            return averagePos(values[0],values[1]);
        else
            return averagePos(values[2],values[1]);
    }

    // Above are helper functions for extractData()

    public Hashtable<String,Double> extractData(){
        // Extracts data from the given data. Currently: Center position, distance, shift 
        NetworkTable t = getTable();
        Hashtable<String,Double> data = new Hashtable<String,Double>();
        // Find the center of the two retroflective pieces of tape that are facing
        // twoards each other!
        Hashtable<String,Double>[] values = new Hashtable<String,Double>[]{createData(t,0),createData(t,1),createData(t,2)};
        Arrays.sort(values, dataCompare);
        double[] centerPos = center(values);
        double distance = Math.sqrt(meterArea / values[1].get("a")); // Not sure wether this is the correct math
        double shift = distance * meterDegreeLength * centerPos[0];

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