package org.usfirst.frc.team1155.robot.subsystems;

import org.usfirst.frc.team1155.robot.PID;
import org.usfirst.frc.team1155.robot.Robot;

import edu.wpi.first.wpilibj.command.Subsystem;

public class LineupSubsystem extends Subsystem {

	private double lineAngle;
    private double desiredForward;
    private double desiredShift; // Assuming we are facing straight. To fix
    private double forwardPrecision;
    private double shiftD;
    public double desiredAngle;
    private PID shiftPID; // In the end these should depend on desired end points
    private PID forwardPID;

    public LineupSubsystem() {}
    
    public void resetInfo() {
    	lineAngle = Math.PI / 2;
    	desiredForward = 0.305 * 3;
        desiredShift = 0.305 * 1; // Assuming we are facing straight. To fix
        forwardPrecision = 0.08;
        shiftD = .35;
        desiredAngle = Math.PI/2;
        shiftPID = new PID(1.1,0,2);
    	forwardPID = new PID(0.4,.02,0);
    	reset = false;
    }
    
    public PID getShiftPID() {
    	return shiftPID;
    }
    
    public PID getForwardPID() {
    	return forwardPID;
    }
    
    public double shiftCoordinate() {
    	return Robot.pos.getX() * Math.sin(lineAngle) - Robot.pos.getY() * Math.cos(lineAngle);
    }
    public double parallelCoordinate() {
    	return Robot.pos.getX() * Math.cos(lineAngle) + Robot.pos.getY() * Math.sin(lineAngle);
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
    
    public double shiftError() {return desiredShift - shiftCoordinate();}
    public double parallelError() {return desiredForward - parallelCoordinate();}
    
    private boolean reset;
    public void move(){
    	if (parallelCoordinate() < desiredForward - forwardPrecision) {
	        shiftPID.add_measurement(shiftError());
	        forwardPID.add_measurement(parallelError());
	        double output =  limitOutput(shiftPID.getOutput(),2);
	        double defaultSpeed = forwardPID.getOutput();
	        shiftPID.setD(shiftD / defaultSpeed);
	        Robot.driveSubsystem.setSpeedTank(defaultSpeed * (1 + output), defaultSpeed * (1 - output));
    	}
    	else {
    		if (!reset) {Robot.driveSubsystem.resetTurnPID(0.5,2,0.3);}
    		Robot.driveSubsystem.turnToDegreeTank(desiredAngle);
    	}
    }
    

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}
