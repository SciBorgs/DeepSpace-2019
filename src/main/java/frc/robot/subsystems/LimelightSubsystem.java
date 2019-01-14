package org.usfirst.frc.team1155.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class LimelightSubsystem extends Subsystem{

    public final static double meterDegreeLength = .02;
    public final static double meterArea = 0.605; // In percent
    public final static double cameraWidth = .015; // In meters
    public final static double imageHeight = 27.5 * 2; // In degrees
    public final static double imageWidth = 20.4 * 2; // In degrees
    
    public NetworkTable getCameraTable(){
        return NetworkTableInstance.getDefault().getTable("limelight");
    }
    public double getTableData(NetworkTable table, String variable){ 
        // According to API, should get a given variable (x1,a2... etc.)
        return table.getEntry(variable).getDouble(0);
    }
    public void setCameraParams(String param, int setting){ // According to API, should set a given param (camMode, pipeline... etc.)
        NetworkTableInstance.getDefault().getTable("limelight").getEntry(param).setNumber(setting);
    }

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}