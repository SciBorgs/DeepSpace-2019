package frc.robot.subsystems;

import frc.robot.PID;
import frc.robot.Robot;

import edu.wpi.first.wpilibj.command.Subsystem;

public class LineupSubsystem extends Subsystem {

	private double lineAngle; // To line up with
    private double desiredForward; // This is where we will aim for b/c of overshooting
    private double desiredShift;
    private PID shiftPID; // PID for deciding the difference in our wheels' speeds
    private PID forwardPID; // PID for deciding our speed
    private double forwardGoal; // This is where we actually want to end up
    private double forwardScale = 0.8; // We're assuming that it goes an extra 20 percent.

    public LineupSubsystem() {}
    
    public void resetInfo(double forwardChange, double shiftChange, double angleChange) {
    	lineAngle = Robot.pos.getAngle() + angleChange;
    	forwardGoal = forwardChange;
    	desiredForward = forwardScale * forwardGoal + parallelCoordinate();
        desiredShift = shiftChange + shiftCoordinate();
        shiftPID = new PID(.5,0,.13);
    	forwardPID = new PID(0.45,.02,0.05);
    	shiftPID.setSmoother(6);
    	Robot.driveSubsystem.resetTurnPID(0.5,.15,0.3);
    	}
    
    public PID getShiftPID() {
    	return shiftPID;
    }
    
    public PID getForwardPID() {
    	return forwardPID;
    }
    
    public double shift(double x, double y, double angle)    {return x * Math.sin(angle) - y * Math.cos(angle);}
    public double parallel(double x, double y, double angle) {return x * Math.cos(angle) + y * Math.sin(angle);}
    public double shiftCoordinate()    {return shift(Robot.pos.getX(),Robot.pos.getY(),lineAngle);} // turns the line we are lining up with into the y-axis
    public double parallelCoordinate() {return parallel(Robot.pos.getX(),Robot.pos.getY(),lineAngle);}
    
    public double shiftError() {return desiredShift - shiftCoordinate();}
    public double parallelError() {return desiredForward - parallelCoordinate();}
    public double deltaTheta() {return lineAngle - Robot.pos.getAngle();}
    
    public void move(){
    	if (parallelCoordinate() < desiredForward) {
        	shiftPID.add_measurement_d(shiftError(),0 - Math.sin(deltaTheta())); // We use the sine of our change in angle as the derivative (that's the secret!)
	        forwardPID.add_measurement(parallelError());
	        double output =  shiftPID.getOutput();
	        double defaultSpeed = forwardPID.getOutput();
	        Robot.driveSubsystem.setSpeedTank(defaultSpeed * (1 + output), defaultSpeed * (1 - output)); // The output changes the percentage that goes to each side which makes it turn
    	}
    	else {
    		Robot.driveSubsystem.setSpeedTank(0, 0);
    	}
    }
    

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}
