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
import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.wpilibj.shuffleboard.*;
import java.util.Hashtable;
import edu.wpi.first.wpilibj.DigitalInput;

public class LiftSubsystem extends Subsystem {	

	public CANSparkMax liftSpark;
	public TalonSRX armTiltTalon;
	public enum Target { High, Mid, Low, Ground, Initial }

	private PID armPID;
	private PID liftPID;
	private double armP = 1, armI = 0.0, armD = 0, liftP = 1.5, liftI = 0.0, liftD = 0.03;
	static final double SPARK_ENCODER_WHEEL_RATIO = 1 / 20.0;
	static final double TALON_ENCODER_WHEEL_RATIO = 18.0 / 62;
	static final double LIFT_WHEEL_RADIUS = Utils.inchesToMeters(1.5); // In meters, the radius of the wheel that is pulling up the lift
	private SimpleWidget levelCounterWidget;
	private int levelCounter = 0;
	private double ARM_OUTPUT_LIMIT = 1;
	static final double ROCKET_HATCH_GAP = Utils.inchesToMeters(28);
	static final double LOW_HATCH_HEIGHT = Utils.inchesToMeters(17);
	static final Hashtable<Target,Integer> HATCH_POSITIONS = // Gives how many hatches above the lowest one for each
		new Hashtable<>(){{
			put(Target.High,2);
			put(Target.Mid,1);
			put(Target.Low,0);
		}
	};
	static final double HATCH_TO_CARGO_DEPOSIT = Utils.inchesToMeters(8.5);
	public static final double MAX_HINGE_HEIGHT = Utils.inchesToMeters(72.5);
	static final double ARM_MAX_ANGLE = Math.toRadians(66);
	static final double ARM_TARGET_ANGLE = Math.toRadians(0);
	static final double ARM_LENGTH = Utils.inchesToMeters(17.5);
	static final double DESIRED_ANGLE = Math.toRadians(30);
	static final double RESTING_HEIGHT = Utils.inchesToMeters(10.5); // In meters
	static final double INITIAL_GAP_TO_GROUND = Utils.inchesToMeters(3.5); // How far up the intake should be when it's sucking in cargo
	static final double RESTING_ANGLE = Math.asin((INITIAL_GAP_TO_GROUND - RESTING_HEIGHT) / ARM_LENGTH); // In radians
	static final double HEIGHT_PRECISION = 0.05; // In meters
	static final double ANGLE_PRECISION = Math.toRadians(5);
	static final double IS_BOTTOM_PRECISION = 0.1; // In meters, precision as to whether it's at the bottom
	static final double BOTTOM_HEIGHT = Utils.inchesToMeters(12.5); // In meters, the height at the lift's lowest point
	static final double INITIAL_ANGLE  = Math.toRadians(9); // In reality should be 60ish
	static final double INITIAL_HEIGHT = BOTTOM_HEIGHT;
	static final double SLOW_LIFT_INPUT = .2; // An input that should move the lift slowly, not for in game purposes
	static final double SLOW_ARM_INPUT = .2; // An input that should move the arm slowly, not for in game purposes
	private double offsetCascadeHeight = 0;
	private double offsetArmAngle = 0;
	static final int LIFT_PID_SMOOTHNESS = 3; // Probably change to 4
	static final int ARM_PID_SMOOTHNESS = 7;
	static final int MIN_LEVEL = -1;
	static final int MAX_LEVEL = 3;
	public Target lastTarget = Target.Ground;
	public DigitalInput cascadeAtBottomLimitSwitch, armAtTopSwitch;

	public void initDefaultCommand() {
    }
	
	public LiftSubsystem() {
		liftSpark    = new CANSparkMax(PortMap.LIFT_SPARK, MotorType.kBrushless);
		armTiltTalon = new TalonSRX(PortMap.ARM_TILT_TALON);
		armTiltTalon.setNeutralMode(NeutralMode.Brake);
		ShuffleboardTab levelCounterTab = Shuffleboard.getTab("Level Counter");
		levelCounterWidget = levelCounterTab.add("Level Counter", -1).withWidget("Text View").withPosition(1, 0).withSize(2, 2);
		liftPID = new PID(liftP, liftI, liftD);
		liftPID.setSmoother(LIFT_PID_SMOOTHNESS);
		armPID  = new PID(armP, armI, armD);
		armPID.setSmoother(ARM_PID_SMOOTHNESS);
		realLiftHeightIs(INITIAL_HEIGHT);
		realArmAngleIs(INITIAL_ANGLE);
		cascadeAtBottomLimitSwitch = new DigitalInput(PortMap.CASCADE_AT_BOTTOM_LIMIT_SWITCH);
		armAtTopSwitch = new DigitalInput(PortMap.ARM_AT_TOP_LIMIT_SWITCH);
	}
	
	private double getTargetHeight(Target target){
		double hatchTargetHeight = LOW_HATCH_HEIGHT + HATCH_POSITIONS.get(target) * ROCKET_HATCH_GAP;
		// Target height will go to the defualt HATCH_HEIGHT if it is holding the hatch, otherwise it will add the gap b/w the hatch and the cargo deposit
		if (Robot.intakeSubsystem.holdingHatch()) {
			return hatchTargetHeight;
		} else if (Robot.intakeSubsystem.holdingCargo()) {
			return hatchTargetHeight + HATCH_TO_CARGO_DEPOSIT;
		} else {
			return hatchTargetHeight;
		}
	}

	public void moveLiftToheight(double targetLiftHeight){
		double error = targetLiftHeight - getLiftHeight();
		boolean hitCorrectHeight = Math.abs(error) < HEIGHT_PRECISION;
		liftPID.add_measurement(error);
		System.out.println("error: " + error);
		System.out.println("spark input: " + liftSpark.get());
		conditionalSetLiftSpeed(liftPID.getOutput(), hitCorrectHeight);
		System.out.println("cascade height: " + Utils.metersToInches(getLiftHeight()));
		System.out.println("desired cascade height: " + Utils.metersToInches(targetLiftHeight));
		System.out.println("height output: " + liftPID.getOutput());
	}
	public void moveArmToAngle(double targetAngle){
		// System.out.println("active traj velocity: " + armTiltTalon.getActiveTrajectoryVelocity());
		double error = getArmAngle() - targetAngle;
		boolean hitCorrectAngle  = Math.abs(error) < ANGLE_PRECISION;
		armPID.add_measurement(error);
		conditionalSetArmTiltSpeed(armPID.getLimitOutput(ARM_OUTPUT_LIMIT), hitCorrectAngle);
		System.out.println("arm angle: " + Math.toDegrees(getArmAngle()));
		System.out.println("desired angle: " + Math.toDegrees(targetAngle));
		System.out.println("angle output: " + armPID.getOutput());
	}
	
	public void moveToPosition(double targetAngle, double targetLiftHeight){
		moveArmToAngle(targetAngle);
		moveLiftToheight(targetLiftHeight);
	}

	public double getTargetLiftHeight(double depositHeight){
		if (ARM_LENGTH * Math.sin(ARM_MAX_ANGLE) + RESTING_HEIGHT > depositHeight) {
			return RESTING_HEIGHT;
		} else {
			return Math.min(depositHeight - ARM_LENGTH * Math.sin(ARM_TARGET_ANGLE), MAX_HINGE_HEIGHT);
		}
	}

	public boolean needsCascade(double height){
		return height > (ARM_LENGTH * Math.sin(ARM_TARGET_ANGLE) + RESTING_HEIGHT);
	}

	public void moveToHeight(double targetHeight){
		double targetLiftHeight = getTargetLiftHeight(targetHeight);
		System.out.println("targetLiftHeight: " + targetLiftHeight);
		double targetAngle = Math.asin((targetHeight - targetLiftHeight) / ARM_LENGTH);
		System.out.println("target angle: " + targetAngle);
		moveToPosition(targetAngle, targetLiftHeight);
	}

	public void moveToTarget(Target target) {
		System.out.println("moving to: " + target);
		if (target == Target.Ground) {
			moveToPosition(RESTING_ANGLE, RESTING_HEIGHT);
		} else if (target == Target.Initial) {
			moveToInitial();
		} else {
			moveToHeight(getTargetHeight(target));
		}
	}

	public void moveToInitial(){
		conditionalSetLiftSpeed(-SLOW_LIFT_INPUT, !liftAtBottom());
		conditionalSetArmTiltSpeed(SLOW_ARM_INPUT, !armAtInitial());
	}

	public void moveLevelCounter(int change) {
		setLevelCounter(levelCounter + change);
	}

	public void setLevelCounter(int level) {
		levelCounter = level;
		levelCounter = Math.max(levelCounter, MIN_LEVEL);
		levelCounter = Math.min(levelCounter, MAX_LEVEL);
		liftPID.reset();
		armPID.reset();
	}

	public Target getTarget() {
		switch (levelCounter) {
			case 0:
				return Target.Ground;
			case 1:
				return Target.Low;
			case 2:
				return Target.Mid;
			case 3:
				return Target.High;
			default:
				return Target.Ground;
		}
	}

	public boolean updateLevelCounterWidget() {
		return levelCounterWidget.getEntry().setNumber(levelCounter);
	}
	
	public boolean atMaxAngle(){
		return false;
	}

	public void realLiftHeightIs(double height){
		offsetCascadeHeight += INITIAL_HEIGHT - getLiftHeight();
	}
	public void realArmAngleIs(double angle){
		offsetArmAngle += INITIAL_ANGLE - getArmAngle();
	}

	public double getLiftHeight() {
		if (liftAtBottom()) {
			realLiftHeightIs(BOTTOM_HEIGHT);
			return BOTTOM_HEIGHT;
		} else {
			return SPARK_ENCODER_WHEEL_RATIO * Robot.positioningSubsystem.getSparkAngle(liftSpark) * LIFT_WHEEL_RADIUS + RESTING_HEIGHT + offsetCascadeHeight;
		}
	}
	
	public double getArmAngle() {
		if (armAtInitial()){
			realArmAngleIs(INITIAL_ANGLE);
			return INITIAL_ANGLE;
		} else {
			return TALON_ENCODER_WHEEL_RATIO * Robot.positioningSubsystem.getTalonAngle(armTiltTalon) + RESTING_ANGLE + offsetArmAngle;
		}
	}

	public boolean liftAtBottom(){
		return !cascadeAtBottomLimitSwitch.get();
	} 

	public boolean armAtInitial(){
		// Returns whether or not the arm (the carriage) is bent back until it hits the cascade, IE: is it at the starting positiong
		return !armAtTopSwitch.get();
	}

	public boolean isStatic(){
		return armTiltTalon.getMotorOutputPercent() == 0 && liftSpark.get() == 0;
	}

	public void conditionalSetLiftSpeed(double speed, boolean b){
		// if the boolean is true it will simply do a set lift speed. Otherwise, it will set it to zero
		if (b) {
			setLiftSpeed(speed);
		} else {
			setLiftSpeed(0);
		}
	}

	public void conditionalSetArmTiltSpeed(double speed, boolean b){
		// if the boolean is true it will simply do a set arm tilt speed. Otherwise, it will set it to zero
		if (b) {
			setArmTiltSpeed(speed);
		} else {
			setArmTiltSpeed(0);
		}
	}

    public void setLiftSpeed(double speed) {
		System.out.println("speed: " + speed);
    	Robot.driveSubsystem.setMotorSpeed(liftSpark, speed);
	}
	
	public void setArmTiltSpeed(double speed) {
		Robot.driveSubsystem.setMotorSpeed(armTiltTalon, speed, .2);
	}
    
}	 