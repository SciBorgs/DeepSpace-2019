package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class LimelightSubsystem extends Subsystem{

    public final static double imageWidth = 55.;
    public final static double imageHeight = 41.;

    
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