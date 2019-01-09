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

    public NetworkTable getTable() {
        return Robot.limelight.getCameraTable();
    }

    public double get(NetworkTable table, String variable) {
        return Robot.limelight.getTableDat(table, variable);
    }

    // Below are helper functions for center()
    public boolean dataCompare(Hashtable<String,Double> pair1, Hashtable<String,Double> pair2){
        return pair1.get("tx") > pair2.get("tx");
    } 
    public double[] averagePos(Hashtable<String,Double> data1, Hashtable<String,Double> data2){
        return new double[]{(data1.get("tx") + data2.get("tx"))/2,
                            (data1.get("ty") + data2.get("ty"))/2};
    }
    public boolean facingLeft(double skew){return skew > -45;}
    public Hashtable<String,Double> createData(NetworkTale t, double nth){
        // Takes a snapshot of data and the nth contour you want and converts it into a hashtable
        Hashtable<String, Double> end = new Hashtable<String, Double>();
        end.put("nth",nth);
        end.put("tx",get(t,"tx" + nth));
        end.put("ty",get(t,"ty" + nth));
        end.put("ts",get(t,"ts" + nth));
        return end;
    }
    // Above are helper functions for center

    public double[] center() {
        // Find the center of the two retroflective pieces of tape that are facing twoards each other!
        NetworkTable t = getTable();
        Hashtable<String,Double>[] values = new Hashtable<String, Double>[]{createData(t,0),createData(t,1),createData(t,2)};
        Arrays.sort(values,dataCompare);
        boolean leftPair = facingLeft(values[1].get("ts"));
        if (leftPair)
            return averagePos(values[0],values[1]);
        else
            return averagePos(values[2],values[1]);
    }
    
    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}