package org.usfirst.frc.team1155.robot.subsystems;

import org.usfirst.frc.team1155.robot.PID;
import org.usfirst.frc.team1155.robot.Robot;

import edu.wpi.first.wpilibj.command.Subsystem;

public class LineupSubsystem extends Subsystem {

	private double lineAngle = Math.PI / 2 * 1.1;
	private double extraShift = .305 * .5; // It seems that it goes about this far too much not matter what
    private double desiredShift = (0.305 * 2 - extraShift); // Assuming we are facing straight. To fix
    private double desiredForward = 0.305 * 5;
    private double shiftPrecision = 0.03;
    private PID shiftPID = new PID(.4,0,2 * desiredShift); // In the end these should depend on desired end points
    private PID forwardPID = new PID(.3,0,0);

    public LineupSubsystem() {}
    
    public PID getShiftPID() {
    	return shiftPID;
    }
    
    public PID getForwardPID() {
    	return forwardPID;
    }
    
    public double parallelCoordinate() {
    	return Robot.pos.getX() * Math.sin(lineAngle) + Robot.pos.getY() * Math.cos(lineAngle);
    }
    public double shiftCoordinate() {
    	return Robot.pos.getX() * Math.cos(lineAngle) + Robot.pos.getY() * Math.sin(lineAngle);
    }

    public void move(){
    	if (parallelCoordinate() < desiredForward) {
	        shiftPID.add_measurement(desiredShift   - parallelCoordinate());
	        forwardPID.add_measurement(desiredForward - shiftCoordinate());
	        double output = (Math.abs(shiftCoordinate()) < shiftPrecision) ? 0 : shiftPID.getOutput();
	        double defaultSpeed = forwardPID.getOutput();
	        System.out.println("shift: " + (desiredShift - shiftCoordinate()));
	        Robot.driveSubsystem.setSpeedTank(defaultSpeed + output, defaultSpeed - output);
    	}
    	else
    		Robot.driveSubsystem.setSpeedTank(0,0);
    	}

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}
