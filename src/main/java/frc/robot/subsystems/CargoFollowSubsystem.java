package frc.robot.subsystems;

import frc.robot.PID;
import frc.robot.Robot;

import edu.wpi.first.wpilibj.command.Subsystem;

public class CargoFollowSubsystem extends Subsystem {

    private PID forwardPID;
    private PID anglePID;
    public double leftSpeed = 0;
    public double rightSpeed = 0;

    public CargoFollowSubsystem() {
        //TODO: Values
    	forwardPID = new PID(.2,.0,.0);
        anglePID = new PID(.01,.0,.0);
    }
    
    public void followBall() {
        if (Robot.limelight.getTableData(Robot.limelight.getCameraTable(), "tv") != 1) {return;}
        double tx = Robot.limelight.getTableData(Robot.limelight.getCameraTable(), "tx");
        double ty = Robot.limelight.getTableData(Robot.limelight.getCameraTable(), "ty");
        anglePID.add_measurement(tx);
        
        forwardPID.add_measurement(-ty);

        double angleAdjust = limitOutput(anglePID.getOutput(), 1);
        //double forwardAdjust = limitOutput(forwardPID.getOutput(), 1);
        double forwardAdjust = 0.5;
        leftSpeed = forwardAdjust + angleAdjust;
        rightSpeed = forwardAdjust - angleAdjust;
        Robot.driveSubsystem.setSpeedTank(-leftSpeed, -rightSpeed);
    }

    public double limitOutput(double output, double maxMagnitude) {
    	if (output > maxMagnitude)
    		return maxMagnitude;
    	else if (output < 0 - maxMagnitude)
    		return 0 - maxMagnitude;
    	//else if (Math.abs(output) < 0.01)
    	//	return 0;
    	else
    		return output;
    }

    public void ballLimelightMode() {
        Robot.limelight.setCameraParams("ledMode", 1); // Force LED Off
        Robot.limelight.setCameraParams("pipeline", 9); // Switch to Ball Pipeline
    }

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}