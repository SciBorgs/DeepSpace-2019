package org.usfirst.frc.team1155.robot.subsystems;

import org.usfirst.frc.team1155.robot.PID;
import org.usfirst.frc.team1155.robot.Robot;

import edu.wpi.first.wpilibj.command.Subsystem;

public class LineupSubsystem extends Subsystem {

	private double lineAngle = Math.PI / 2;
	private double lineRotation = Math.PI / 10;
	private double realLineAngle = lineAngle + lineRotation;
	private double extraShift = .305 * .5; // It seems that it goes about this far too much not matter what
    private double desiredForward = 0.305 * 5;
    private double desiredShift = (0.305 * 2 - extraShift + Math.sin(lineRotation) * desiredForward); // Assuming we are facing straight. To fix
    private double shiftPrecision = 0.05;
    private double forwardPrecision = 0.08;
    private double shiftD = .2;
    public double desiredAngle = Math.PI/2;
    private PID shiftPID = new PID(0.6,0,2.8); // In the end these should depend on desired end points
    private PID forwardPID = new PID(0.1,.015,.0);

    public LineupSubsystem() {}
    
    public PID getShiftPID() {
    	return shiftPID;
    }
    
    public PID getForwardPID() {
    	return forwardPID;
    }
    
    public double shiftCoordinate() {
    	return Robot.pos.getX() * Math.sin(realLineAngle) - Robot.pos.getY() * Math.cos(realLineAngle);
    }
    public double parallelCoordinate() {
    	return Robot.pos.getX() * Math.cos(realLineAngle) + Robot.pos.getY() * Math.sin(realLineAngle);
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
    
    public void move(){
    	if (parallelCoordinate() < desiredForward - forwardPrecision) {
	        shiftPID.add_measurement(shiftError());
	        forwardPID.add_measurement(parallelError());
	        double output =  limitOutput((Math.abs(shiftError()) < shiftPrecision) ? 0 : shiftPID.getOutput(),2);
	        double defaultSpeed = forwardPID.getOutput();
	        System.out.println("shift coord: " + shiftCoordinate());
	        System.out.println("parallel error: " + parallelError());
	        System.out.println("output: " + output);
	        shiftPID.setD(shiftD / defaultSpeed);
	        System.out.println("D: " + shiftPID.d);
	        System.out.println("default speed: " + defaultSpeed);
	        Robot.driveSubsystem.setSpeedTank(defaultSpeed * (1 + output), defaultSpeed * (1 - output));
    	}
    	else {
    		Robot.driveSubsystem.turnToDegreeTank(desiredAngle);
    	}
    }
    

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}
