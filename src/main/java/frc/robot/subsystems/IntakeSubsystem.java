package frc.robot.subsystems;

import frc.robot.Utils;
import frc.robot.PortMap;
import frc.robot.Robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class IntakeSubsystem extends Subsystem {

    public TalonSRX intakeTalon;
	public DoubleSolenoid secureHatchSolenoid, armSolenoid, popHatchSolenoid;
	public static final DoubleSolenoid.Value RELEASE_HATCH_VALUE = Value.kReverse;
	public static final DoubleSolenoid.Value SECURE_HATCH_VALUE = Utils.oppositeDoubleSolenoidValue(RELEASE_HATCH_VALUE);
	public static final DoubleSolenoid.Value OPEN_ARM_VALUE = Value.kForward;
	public static final DoubleSolenoid.Value CLOSE_ARM_VALUE = Utils.oppositeDoubleSolenoidValue(OPEN_ARM_VALUE);
	public static final DoubleSolenoid.Value EXTEND_POP_PISTONS = Value.kReverse;
	public static final DoubleSolenoid.Value RETRACT_POP_PISTONS = Utils.oppositeDoubleSolenoidValue(EXTEND_POP_PISTONS);
	private Timer timer;
	private final String filename = "IntakeSubsystem.java";
	public final static double SUCK_SPEED = -1;
	public final static double SPIT_SPEED = SUCK_SPEED * -1;
	public final static double PICKUP_HATCH_SPEED = -0.3;
	public final static double SUCK_IF_OUT_PERIOD = 1; // The amount of time that the intake should suck if the ball stops pressing the button in seconds
	public final static double SECURE_CARGO_SPEED = SUCK_SPEED / 2;
	private boolean holdingCargo;

    public IntakeSubsystem() {
		this.timer = new Timer();
		this.timer.start();
		this.intakeTalon = new TalonSRX(PortMap.INTAKE_TALON);
		this.intakeTalon.setNeutralMode(NeutralMode.Brake);
		this.intakeTalon.configContinuousCurrentLimit(10);
		this.intakeTalon.configPeakCurrentLimit(10);
		this.intakeTalon.enableCurrentLimit(true);
		this.holdingCargo = false;
		this.secureHatchSolenoid = Utils.newDoubleSolenoid(PortMap.SECURE_HATCH_SOLENOID_PDP, PortMap.SECURE_HATCH_SOLENOID);
		this.armSolenoid         = Utils.newDoubleSolenoid(PortMap.ARM_SOLENOID_PDP, PortMap.ARM_SOLENOID);
		this.popHatchSolenoid    = Utils.newDoubleSolenoid(PortMap.POP_HATCH_SOLENOID_PDP, PortMap.POP_HATCH_SOLENOID);
	}
    
	public void periodicLog(){
	}

	public TalonSRX[] getTalons() {
    	return new TalonSRX[]{this.intakeTalon};
	}

	public void releaseHatch(){this.secureHatchSolenoid.set(RELEASE_HATCH_VALUE);}
	public void secureHatch() {this.secureHatchSolenoid.set(SECURE_HATCH_VALUE);}

	public void openArm() {this.armSolenoid.set(OPEN_ARM_VALUE);}
	public void closeArm(){this.armSolenoid.set(CLOSE_ARM_VALUE);}
	public void toggleArm(){Utils.toggleDoubleSolenoid(this.armSolenoid);}

	public void extendPopHatchPistons() {this.popHatchSolenoid.set(EXTEND_POP_PISTONS);}
	public void retractPopHatchPistons(){this.popHatchSolenoid.set(RETRACT_POP_PISTONS);}

	public boolean holdingCargo() { // assumes when the drvier tries to intake cargo, the robot actually has it
		return this.holdingCargo;
	}

	public void setIntakeSpeed(double speed){
		Robot.driveSubsystem.setMotorSpeed(this.intakeTalon, -speed, 2);
	}

    public void suck() {
		this.holdingCargo = true; // We assume that sucknig means we have the cargo. W/o limit switches it is the best we can do
		setIntakeSpeed(SUCK_SPEED);
    }

    public void spit() {
		this.holdingCargo = false;
        setIntakeSpeed(SPIT_SPEED);
	}

	public void secureCargo() {
		this.holdingCargo = true;
		setIntakeSpeed(SECURE_CARGO_SPEED);
	}

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
	}
	
}