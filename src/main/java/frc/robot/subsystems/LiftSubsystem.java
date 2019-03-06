package frc.robot.subsystems;

import frc.robot.helpers.*;
import frc.robot.PortMap;
import frc.robot.Robot;
import frc.robot.Utils;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.command.Subsystem;
import com.ctre.phoenix.Util;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.shuffleboard.*;
import java.util.Hashtable;

public class LiftSubsystem extends Subsystem {	

	public CANSparkMax liftSpark;
	public TalonSRX armTiltTalon;
	public enum Target { High, Mid, Low, Initial, Slam }

	private PID armPID;
	private PID liftPID;
	private double armP = 0.1, armI = 0.0, armD = 0.0, liftP = 0.1, liftI = 0.0, liftD = 0.0;
	static final double STATIC_INPUT = 0.07; // the input that will keep the cascade level
	static final double SPARK_ENCODER_WHEEL_RATIO = 1 / 9.0;
	static final double TALON_ENCODER_WHEEL_RATIO = 1 / 3.44;
	static final double LIFT_WHEEL_RADIUS = Utils.inchesToMeters(1.5); // In meters, the radius of the wheel that is pulling up the lift
	private SimpleWidget levelCounterWidget;
	private int levelCounter = 0;
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
	static final double INITIAL_HEIGHT = Utils.inchesToMeters(12); // In meters
	static final double INITIAL_GAP_TO_GROUND = Utils.inchesToMeters(1); // How far up the intake should be when it's sucking in cargo
	static final double INITIAL_ANGLE = Math.asin((INITIAL_GAP_TO_GROUND - INITIAL_HEIGHT) / ARM_LENGTH); // In radians
	static final double HEIGHT_PRECISION = 0.05; // In meters
	static final double ANGLE_PRECISION = Math.toRadians(3);
	static final double IS_BOTTOM_PRECISION = 0.05; // In meters, precision as to whether it's at the bottom
	static final double BOTTOM_HEIGHT = 0; // In meters, the height at the lift's lowest point
	static final double PICKUP_HATCH_SPEED = 0; // The speed with which we slam and and pick up a hatch from the ground

	public void initDefaultCommand() {
    }
	
	public LiftSubsystem() {
		liftSpark    = new CANSparkMax(PortMap.LIFT_SPARK, MotorType.kBrushless);
		armTiltTalon = new TalonSRX(PortMap.ARM_TILT_TALON);
		ShuffleboardTab levelCounterTab = Shuffleboard.getTab("Level Counter");
		levelCounterWidget = levelCounterTab.add("Level Counter", -1).withWidget("Text View").withPosition(1, 0).withSize(2, 2);
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
		if (target == Target.Initial) {
			goDown();
		}
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

	public void updateLevelCounter(int val) {
		levelCounter += val;
		if (levelCounter < -1) {
			levelCounter = -1;
		}
		if (levelCounter > 4) {
			levelCounter = 4;
		}
	}

	public Target getTarget() {
		switch (levelCounter) {
			case -1:
				return Target.Slam;
			case 0:
				return Target.Initial;
			case 1:
				return Target.Low;
			case 2:
				return Target.Mid;
			case 3:
				return Target.High;
			default:
				return Target.Initial;
		}
	}

	public void pickupHatchFromGround(){
		setArmTiltSpeed(PICKUP_HATCH_SPEED);
	}

	public boolean updateLevelCounterWidget() {
		return levelCounterWidget.getEntry().setNumber(levelCounter);
	}

	public void goDown(){
		moveToTarget(INITIAL_ANGLE, INITIAL_HEIGHT);
	}
	
	public boolean atMaxAngle(){
		return false;
	}
	public double getLiftHeight() {
		System.out.println("lift spark angle: " + Math.toDegrees(Robot.positioningSubsystem.getSparkAngle(liftSpark)));
		return SPARK_ENCODER_WHEEL_RATIO * Robot.positioningSubsystem.getSparkAngle(liftSpark) * LIFT_WHEEL_RADIUS + INITIAL_HEIGHT;
	}
	
	public double getArmAngle() {
		return TALON_ENCODER_WHEEL_RATIO * Robot.positioningSubsystem.getTalonAngle(armTiltTalon) + INITIAL_ANGLE;
	}

	public boolean liftAtBottom(){
		return getLiftHeight() - BOTTOM_HEIGHT < IS_BOTTOM_PRECISION;
	} 

    public void setLiftSpeed(double speed) {
    	Robot.driveSubsystem.setMotorSpeed(liftSpark, STATIC_INPUT + speed);
	}
	
	public void setArmTiltSpeed(double speed) { 
		Robot.driveSubsystem.setMotorSpeed(armTiltTalon, speed);
	}
    
}	 