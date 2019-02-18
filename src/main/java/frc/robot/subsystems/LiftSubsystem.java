package frc.robot.subsystems;

import frc.robot.helpers.*;
import frc.robot.PortMap;
import frc.robot.Robot;
import frc.robot.Utils;
import edu.wpi.first.wpilibj.command.Subsystem;

import com.ctre.phoenix.Util;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import java.util.Hashtable;

public class LiftSubsystem extends Subsystem {	

	public enum Target { High, Mid, Low }
	public TalonSRX liftTalon, armTiltTalonLeft, armTiltTalonRight;

	private PID armPID;
	private PID liftPID;
	private double armP = 0.1, armI = 0.0, armD = 0.0, liftP = 0.1, liftI = 0.0, liftD = 0.0;
	static final double TICKS_PER_REV = 10000;
	static final double ARM_WHEEL_RADIUS = 1; // In meters, the radius of the wheel that is pulling up the lift
	static final double ROCKET_HATCH_GAP = Utils.inchesToMeters(28);
	static final double LOW_HATCH_HEIGHT = Utils.inchesToMeters(19);
	static final Hashtable<Target,Integer> HATCH_POSITIONS = // Gives how many hatches above the lowest one for each
		new Hashtable<>(){{
			put(Target.High,2);
			put(Target.Mid,1);
			put(Target.Low,0);
		}
	};
	static final double HATCH_TO_CARGO_DEPOSIT = Utils.inchesToMeters(8.5);
	public static final double MAX_HINGE_HEIGHT = Utils.inchesToMeters(40);
	static final double ARM_MAX_ANGLE = Math.toRadians(66);
	static final double ARM_LENGTH = Utils.inchesToMeters(25.953);
	static final double DESIRED_ANGLE = Math.toRadians(50);
	static final double INITIAL_ANGLE = -Math.PI/2; // In radians
	static final double INITIAL_HEIGHT = 0; // In meters
	static final double HEIGHT_PRECISION = 0.05; // In meters
	static final double ANGLE_PRECISION = Math.toRadians(3);
	static final double IS_BOTTOM_PRECISION = 0.05; // In meters, precision as to whether it's at the bottom
	static final double BOTTOM_HEIGHT = 0; // In meters, the height at the lift's lowest point

	public void initDefaultCommand() {
    }
	
	public LiftSubsystem() {
		liftTalon       = new TalonSRX(PortMap.LIFT_TALON);
		armTiltTalonLeft = new TalonSRX(PortMap.ARM_TILT_TALON_LEFT);
		armTiltTalonRight = new TalonSRX(PortMap.ARM_TILT_TALON_RIGHT);
		liftPID = new PID(liftP, liftI, liftD);
		armPID  = new PID(armP, armI, armD);
	}
	
	private double getTargetHeight(Target target){
		double hatchTargetHeight = BOTTOM_HEIGHT + HATCH_POSITIONS.get(target) * ROCKET_HATCH_GAP;
		// Target height will go to the defualt HATCH_HEIGHT if it is holding the hatch, otherwise it will add the gap b/w the hatch and the cargo deposit
		return hatchTargetHeight + (Robot.intakeSubsystem.holdingHatch() ? 0 : HATCH_TO_CARGO_DEPOSIT);
	}
	
	public void moveToTarget(double targetAngle, double targetLiftHeight){
		armPID.add_measurement(getArmAngle() - targetAngle);
		liftPID.add_measurement(getLiftHeight() - targetLiftHeight);
		setLiftSpeed(liftPID.getOutput());
		setArmTiltSpeed(armPID.getOutput());
	}

	public void moveToHeight(Target target) {
		double targetHeight = getTargetHeight(target);
		double targetLiftHeight = Math.min(targetHeight - ARM_LENGTH * Math.sin(ARM_MAX_ANGLE), MAX_HINGE_HEIGHT);
		double minimumAngle = Math.asin(targetHeight - targetLiftHeight / ARM_LENGTH);
		double targetAngle  = Math.max(DESIRED_ANGLE, minimumAngle);
		boolean hitCorrectHeight = Math.abs(getLiftHeight() - targetLiftHeight) < HEIGHT_PRECISION;
		boolean hitCorrectAngle  = Math.abs(getArmAngle() - targetAngle) < ANGLE_PRECISION;
		if (hitCorrectHeight && hitCorrectAngle){
			setLiftSpeed(0);
			setArmTiltSpeed(0);
		} else {
			moveToTarget(targetAngle, targetLiftHeight);
		}
	}

	public void goDown(){
		moveToTarget(INITIAL_ANGLE, INITIAL_HEIGHT);
	}
	
	public boolean atMaxAngle(){
		return false;
	}

	private double getTalonAngle(TalonSRX talon){
		return talon.getSensorCollection().getQuadraturePosition() / (TICKS_PER_REV * 2 * Math.PI);
	}

	public double getLiftHeight() {
		return getTalonAngle(liftTalon) * ARM_WHEEL_RADIUS + INITIAL_HEIGHT;
	}
	
	public double getArmAngle() {
		return getTalonAngle(armTiltTalonLeft) + INITIAL_ANGLE;
	}

	public boolean liftAtBottom(){
		return getLiftHeight() - BOTTOM_HEIGHT < IS_BOTTOM_PRECISION;
	} 

    public void setLiftSpeed(double speed) {
    	Utils.setTalon(liftTalon, speed);
	}
	
	public void setArmTiltSpeed(double speed) { 
		Utils.setTalon(armTiltTalonLeft, speed);
		Utils.setTalon(armTiltTalonRight, speed);
	}
    
}	 