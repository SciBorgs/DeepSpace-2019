package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class LimelightSubsystem extends Subsystem{

    public final static double IMAGE_WIDTH = 27.; // In degrees
    public final static double IMAGE_HEIGHT = 20.5; // In degrees
    public final static double SHIFT = 0; // In meters away from the center
	private final String filename = "LimelightSubsystem.java";

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