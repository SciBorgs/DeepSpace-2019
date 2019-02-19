package frc.robot.subsystems;

import frc.robot.helpers.*;
import frc.robot.PortMap;
import frc.robot.Utils;
import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.Subsystem;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.ctre.phoenix.motorcontrol.ControlMode;

import java.util.Hashtable;

public class LiftSubsystem extends Subsystem{
	public DoubleSolenoid panelSolenoidTilt, panelSolenoidIntake;
	

	public enum Target { High, Mid, Low }
	public CANSparkMax liftSpark;
	public TalonSRX armTiltTalon;

	private PID armPID;
	private PID liftPID;
	private double armP = 0.1, armI = 0.0, armD = 0.0, liftP = 0.1, liftI = 0.0, liftD = 0.0;
	static final double STATIC_INPUT = 0.1; // the input that will keep the cascade level
	static final double SPARK_ENCODER_WHEEL_RATIO = 1;
	static final double TALON_ENCODER_WHEEL_RATIO = 1;
	static final double ARM_WHEEL_RADIUS = Utils.inchesToMeters(1.5); // In meters, the radius of the wheel that is pulling up the lift
	static final Hashtable<Target,Double> HEIGHTS = // In meters
		new Hashtable<>(){{
			put(Target.High,2.12);
			put(Target.Mid,1.384);
			put(Target.Low,.4826);
		}
	};
	public static final double MAX_HINGE_HEIGHT = Utils.inchesToMeters(40);
	static final double ARM_MAX_ANGLE = Math.toRadians(66);
	static final double ARM_LENGTH = Utils.inchesToMeters(25.953);
	static final double DESIRED_ANGLE = Math.toRadians(50);
	static final double INITIAL_HEIGHT = Utils.inchesToMeters(12); // In meters
	static final double INITIAL_ANGLE = Math.asin(-INITIAL_HEIGHT / ARM_LENGTH); // In radians
	static final double HEIGHT_PRECISION = 0.05; // In meters
	static final double ANGLE_PRECISION = Math.toRadians(3);
	static final double IS_BOTTOM_PRECISION = 0.05; // In meters, precision as to whether it's at the bottom
	static final double BOTTOM_HEIGHT = 0; // In meters, the height at the lift's lowest point

	public void initDefaultCommand() {
    }
	
	public LiftSubsystem() {
		liftSpark    = new CANSparkMax(PortMap.LIFT_SPARK, MotorType.kBrushless);
		armTiltTalon = new TalonSRX(PortMap.ARM_TILT_TALON);
		liftPID = new PID(liftP, liftI, liftD);
		armPID  = new PID(armP, armI, armD);
	}
	
	public void moveToHeight(Target target) {
		double targetHeight = HEIGHTS.get(target);
		double targetLiftHeight = Math.min(targetHeight - ARM_LENGTH * Math.sin(ARM_MAX_ANGLE), MAX_HINGE_HEIGHT);
		double minimumAngle = Math.asin(targetHeight - targetLiftHeight / ARM_LENGTH);
		double targetAngle  = Math.max(DESIRED_ANGLE, minimumAngle);
		double currentLiftHeight = getLiftHeight();
		double currentArmAngle   = getArmAngle();
		boolean hitCorrectHeight = Math.abs(currentLiftHeight - targetLiftHeight) < HEIGHT_PRECISION;
		boolean hitCorrectAngle  = Math.abs(currentArmAngle - targetAngle) < ANGLE_PRECISION;
		if (hitCorrectHeight && hitCorrectAngle){
			//Robot.intakeSubsystem.depositObject(); Remove Method
		} else {
		 armPID.add_measurement(targetAngle      - currentArmAngle);
		liftPID.add_measurement(targetLiftHeight - currentLiftHeight);
		setLiftSpeed(liftPID.getOutput());
		setArmTiltSpeed(armPID.getOutput());
		}
	}
	
	public boolean atMaxAngle(){
		return false;
	}
	public double getLiftHeight() {
		return SPARK_ENCODER_WHEEL_RATIO * Robot.positioningSubsystem.getSparkAngle(liftSpark) * ARM_WHEEL_RADIUS + INITIAL_HEIGHT;
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