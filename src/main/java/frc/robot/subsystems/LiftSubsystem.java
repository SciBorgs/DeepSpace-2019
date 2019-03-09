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

public class LiftSubsystem extends Subsystem {	

	public CANSparkMax liftSpark;
	public TalonSRX armTiltTalon;
	public enum Target { High, Mid, Low, Initial, Slam }

	private PID armPID;
	private PID liftPID;
	private double armP = 1, armI = 0.0, armD = 0, liftP = 1.5, liftI = 0.01, liftD = 0.03;
	static final double LIFT_STATIC_INPUT = 0.085; // the input that will keep the cascade level
	static final double SPARK_ENCODER_WHEEL_RATIO = 1 / 9.08;
	static final double TALON_ENCODER_WHEEL_RATIO = 18.0 / 62;
	static final double LIFT_WHEEL_RADIUS = Utils.inchesToMeters(1.5); // In meters, the radius of the wheel that is pulling up the lift
	private SimpleWidget levelCounterWidget;
	private int levelCounter = 0;
	private double ARM_OUTPUT_LIMIT = 1;
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
	public static final double MAX_HINGE_HEIGHT = Utils.inchesToMeters(72.5);
	static final double ARM_MAX_ANGLE = Math.toRadians(66);
	static final double ARM_TARGET_ANGLE = Math.toRadians(20);
	static final double ARM_LENGTH = Utils.inchesToMeters(19);
	static final double DESIRED_ANGLE = Math.toRadians(40);
	static final double RESTING_HEIGHT = Utils.inchesToMeters(12.5); // In meters
	static final double INITIAL_GAP_TO_GROUND = Utils.inchesToMeters(1); // How far up the intake should be when it's sucking in cargo
	static final double RESTING_ANGLE = Math.asin((INITIAL_GAP_TO_GROUND - RESTING_HEIGHT) / ARM_LENGTH); // In radians
	static final double HEIGHT_PRECISION = 0.05; // In meters
	static final double ANGLE_PRECISION = Math.toRadians(5);
	static final double IS_BOTTOM_PRECISION = 0.05; // In meters, precision as to whether it's at the bottom
	static final double BOTTOM_HEIGHT = 0; // In meters, the height at the lift's lowest point
	static final double PICKUP_HATCH_SPEED = 0; // The speed with which we slam and and pick up a hatch from the ground
	static final double INITIAL_ANGLE  = RESTING_ANGLE;
	static final double INITIAL_HEIGHT = RESTING_HEIGHT;
	private double offsetCascadeHeight = 0;
	private double offsetArmAngle = 0;
	private int LIFT_PID_SMOOTHNESS = 3; // Probably change to 4
	private int ARM_PID_SMOOTHNESS = 7;
	public Target lastTarget = Target.Initial;

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
		offsetCascadeHeight += INITIAL_HEIGHT - getLiftHeight();
		offsetArmAngle += INITIAL_ANGLE - getArmAngle();
	}
	
	private double getTargetHeight(Target target){
		double hatchTargetHeight = BOTTOM_HEIGHT + HATCH_POSITIONS.get(target) * ROCKET_HATCH_GAP;
		// Target height will go to the defualt HATCH_HEIGHT if it is holding the hatch, otherwise it will add the gap b/w the hatch and the cargo deposit
		if (Robot.intakeSubsystem.holdingHatch()) {
			return hatchTargetHeight;
		} else {
			return hatchTargetHeight + HATCH_TO_CARGO_DEPOSIT;
		}
	}

	public void moveLiftToheight(double targetLiftHeight){
		liftPID.add_measurement(targetLiftHeight - getLiftHeight());
		setLiftSpeed(liftPID.getOutput());
	}
	public void moveArmToAngle(double targetAngle){
		// System.out.println("active traj velocity: " + armTiltTalon.getActiveTrajectoryVelocity());
		armPID.add_measurement(targetAngle - getArmAngle());
		setArmTiltSpeed(armPID.getLimitOutput(ARM_OUTPUT_LIMIT));
		System.out.println("arm angle: " + Math.toDegrees(getArmAngle()));
		System.out.println("desired angle: " + Math.toDegrees(targetAngle));
		System.out.println("output: " + armPID.getOutput());
	}
	
	public void moveToTarget(double targetAngle, double targetLiftHeight){
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

	public void moveToHeight(double targetHeight){
		double targetLiftHeight = Math.min(targetHeight - ARM_LENGTH * Math.sin(ARM_TARGET_ANGLE), MAX_HINGE_HEIGHT);
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

	public void moveToTargetHeight(Target target) {
		if (lastTarget != target){
			liftPID.reset();
			armPID.reset();
			lastTarget = target;
		}
		if (target == Target.Initial) {
			goDown();
		} else {
		moveToHeight(getTargetHeight(target));
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
		moveToTarget(RESTING_ANGLE, RESTING_HEIGHT);
	}
	
	public boolean atMaxAngle(){
		return false;
	}
	public double getLiftHeight() {
		return SPARK_ENCODER_WHEEL_RATIO * Robot.positioningSubsystem.getSparkAngle(liftSpark) * LIFT_WHEEL_RADIUS + RESTING_HEIGHT + offsetCascadeHeight;
	}
	
	public double getArmAngle() {
		return TALON_ENCODER_WHEEL_RATIO * Robot.positioningSubsystem.getTalonAngle(armTiltTalon) + RESTING_ANGLE + offsetArmAngle;
	}

	public boolean liftAtBottom(){
		return getLiftHeight() - BOTTOM_HEIGHT < IS_BOTTOM_PRECISION;
	} 

    public void setLiftSpeed(double speed) {
    	Robot.driveSubsystem.setMotorSpeed(liftSpark, LIFT_STATIC_INPUT + speed);
	}
	
	public void setArmTiltSpeed(double speed) {
		Robot.driveSubsystem.setMotorSpeed(armTiltTalon, speed, .2);
	}
    
}	 