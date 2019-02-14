package frc.robot.subsystems;

import frc.robot.PID;
import frc.robot.PortMap;
import frc.robot.Utils;
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
	static final double ARM_MAX_ANGLE = 66; // In degrees
	static final double ARM_LENGTH = Utils.inchesToMeters(25.953);
	static final double INITIAL_ANGLE = -Math.PI/2; // In radians
	static final double INITIAL_HEIGHT = 0; // In meters
	static final double HEIGHT_PRECISION = 0.05; // In meters
	
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
		double targetLiftHeight = targetHeight - ARM_LENGTH * Math.sin(ARM_MAX_ANGLE);
		double targetAngle  = ARM_MAX_ANGLE;
		double currentLiftHeight = getLiftHeight();
		double currentArmAngle   = getArmAngle();
		if (atMaxAngle() && Math.abs(currentLiftHeight) < HEIGHT_PRECISION){
			depositObject();
		} else {
		 armPID.add_measurement(targetAngle      - currentArmAngle);
		liftPID.add_measurement(targetLiftHeight - currentLiftHeight);
		setLiftSpeed(liftPID.getOutput());
		setArmTiltSpeed(armPID.getOutput());
		}
	}

	private void depositObject(){
		setLiftSpeed(0);
		setArmTiltSpeed(0);
		if (holdingHatch()){
			depositHatchPanel();
		} else {
				depositCargo();
		}
	}
	
	private double getTalonAngle(TalonSRX talon){
		return talon.getSensorCollection().getQuadraturePosition() / (TICKS_PER_REV * 2 * Math.PI);
	}

	private double getLiftHeight() {
		return getTalonAngle(liftTalon) * ARM_WHEEL_RADIUS + INITIAL_HEIGHT;
	}
	
	private double getArmAngle() {
		return getTalonAngle(armTiltTalonLeft) + INITIAL_ANGLE;
	}

	public void depositHatchPanel() {
		panelSolenoidIntake.set(DoubleSolenoid.Value.kForward);
	}
	public void depositCargo(){
		return;
	}
    
    public DoubleSolenoid.Value getPanelOut() {
        return panelSolenoidIntake.get();
    }
	
    public DoubleSolenoid.Value getPanelTilt() {
		return panelSolenoidTilt.get();
	}

	public boolean holdingHatch(){ // these need to be corrected
		return false;
	}
	public boolean holdingCargo(){
		return true;
	}
	public boolean atMaxAngle(){
		return false;
	}

    public void setLiftSpeed(double speed) {
    	liftTalon.set(ControlMode.PercentOutput, speed);
	}
	
	public void setArmTiltSpeed(double speed) { 
		armTiltTalonLeft.set(ControlMode.PercentOutput, speed);
		armTiltTalonRight.set(ControlMode.PercentOutput, speed);
	}
    
}	 