package org.usfirst.frc.team1155.robot.subsystems;

import org.usfirst.frc.team1155.robot.PID;
import org.usfirst.frc.team1155.robot.Robot;

import edu.wpi.first.wpilibj.command.Subsystem;

public class LineupSubsystem extends Subsystem {

	private double lineAngle;
    private double desiredForward;
    private double desiredShift; // Assuming we are facing straight. To fix
    private PID shiftPID; // In the end these should depend on desired end points
    private PID forwardPID;
    private double forwardGoal;
    private double forwardScale = 0.8;

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
    public double shiftCoordinate()    {return shift(Robot.pos.getX(),Robot.pos.getY(),lineAngle);}
    public double parallelCoordinate() {return parallel(Robot.pos.getX(),Robot.pos.getY(),lineAngle);}

    
    public double limitOutput(double output, double maxMagnitude) {
    	if (output > maxMagnitude)
    		return maxMagnitude;
    	else if (output < 0 - maxMagnitude)
    		return 0 - maxMagnitude;
    	else
    		return output;
    }
    
    public double shiftError() {return desiredShift - shiftCoordinate();}
    public double parallelError() {return desiredForward - parallelCoordinate();}
    public double deltaTheta() {return lineAngle - Robot.pos.getAngle();}
    public double projectedShift() {
    	return shiftError() - (forwardGoal - parallelCoordinate()) * Math.tan(deltaTheta());
    }
    
    public void move(){
    	if (parallelCoordinate() < desiredForward) {
        	shiftPID.add_measurement_d(shiftError(),0 - Math.sin(deltaTheta()));
	        forwardPID.add_measurement(parallelError());
	        double output =  shiftPID.getOutput();
	        double defaultSpeed = forwardPID.getOutput();
	        Robot.driveSubsystem.setSpeedTank(defaultSpeed * (1 + output), defaultSpeed * (1 - output));
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
