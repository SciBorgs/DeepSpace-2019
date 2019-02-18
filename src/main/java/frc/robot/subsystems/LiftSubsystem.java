package frc.robot.subsystems;

import frc.robot.PID;
import frc.robot.PortMap;
import frc.robot.Utils;
import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.Subsystem;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;

import java.util.Hashtable;

public class LiftSubsystem extends Subsystem{
	public DoubleSolenoid panelSolenoidTilt, panelSolenoidIntake;
	

	public enum Target { High, Mid, Low }
	public TalonSRX liftTalon, armTiltTalonLeft, armTiltTalonRight;

	private PID armPID;
	private PID liftPID;
	private double armP = 0.1, armI = 0.0, armD = 0.0, liftP = 0.1, liftI = 0.0, liftD = 0.0;
	static final double TICKS_PER_REV = 10000;
	static final double ARM_WHEEL_RADIUS = 1; // In meters, the radius of the wheel that is pulling up the lift
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
	static final double INITIAL_ANGLE = -Math.PI/2; // In radians
	static final double INITIAL_HEIGHT = 0; // In meters
	static final double HEIGHT_PRECISION = 0.05; // In meters
	static final double ANGLE_PRECISION = Math.toRadians(3);
	
	public void initDefaultCommand() {
    }
	
	public LiftSubsystem() {
		liftTalon       = new TalonSRX(PortMap.LIFT_TALON);
		armTiltTalonLeft = new TalonSRX(PortMap.ARM_TILT_TALON_LEFT);
		armTiltTalonRight = new TalonSRX(PortMap.ARM_TILT_TALON_RIGHT);
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

	private double getTalonAngle(TalonSRX talon){
		return talon.getSensorCollection().getQuadraturePosition() / (TICKS_PER_REV * 2 * Math.PI);
	}

	public double getLiftHeight() {
		return getTalonAngle(liftTalon) * ARM_WHEEL_RADIUS + INITIAL_HEIGHT;
	}
	
	private double getArmAngle() {
		return getTalonAngle(armTiltTalonLeft) + INITIAL_ANGLE;
	}

    public void setLiftSpeed(double speed) {
    	Utils.setTalon(liftTalon, speed);
	}
	
	public void setArmTiltSpeed(double speed) { 
		Utils.setTalon(armTiltTalonLeft, speed);
		Utils.setTalon(armTiltTalonRight, speed);
	}
    
}	 