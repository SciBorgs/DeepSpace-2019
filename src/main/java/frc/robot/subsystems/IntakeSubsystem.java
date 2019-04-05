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
import edu.wpi.first.wpilibj.DigitalInput;

public class IntakeSubsystem extends Subsystem {

    public TalonSRX intakeTalon;
	public DoubleSolenoid scoopSolenoid, secureHatchSolenoid, armSolenoid;
	private double lastHeld;
	private Timer timer;
	private final String filename = "IntakeSubsystem.java";
	public final static double SUCK_SPEED = 1;
	public final static double SPIT_SPEED = SUCK_SPEED * -1;
	public final static double PICKUP_HATCH_SPEED = -0.3;
	public final static double SUCK_IF_OUT_PERIOD = 1; // The amount of time that the intake should suck if the ball stops pressing the button in seconds
	public final static double SECURE_CARGO_SPEED = SUCK_SPEED / 2;
	private boolean holdingHatch;
	private boolean holdingCargo;

    public IntakeSubsystem() {
		timer = new Timer();
		timer.start();
		lastHeld = timer.get() - SUCK_IF_OUT_PERIOD;
		intakeTalon = new TalonSRX(PortMap.INTAKE_TALON);
		intakeTalon.setNeutralMode(NeutralMode.Brake);
		intakeTalon.configContinuousCurrentLimit(10);
		intakeTalon.configPeakCurrentLimit(10);
		intakeTalon.enableCurrentLimit(true);
		holdingHatch = false;
		holdingCargo = false;
		secureHatchSolenoid = new DoubleSolenoid(0, PortMap.SECURE_HATCH_SOLENOID[0], PortMap.SECURE_HATCH_SOLENOID[1]);
		// scoopSolenoid = new DoubleSolenoid(PortMap.SCOOP_SOLENOID[0], PortMap.SCOOP_SOLENOID[1]);
		armSolenoid = new DoubleSolenoid(0, PortMap.ARM_SOLENOID[0], PortMap.ARM_SOLENOID[1]);
	}
    
	public void periodicLog(){
	}

	public TalonSRX[] getTalons() {
    	return new TalonSRX[]{intakeTalon};
	}

	public void scoopUp() {
		scoopSolenoid.set(Value.kForward);
	}
	public void scoopDown(){
		scoopSolenoid.set(Value.kReverse);
	}

	public void releaseHatch(){
		System.out.println("releasing Hatch");
		secureHatchSolenoid.set(Value.kReverse);
		System.out.println("solenoid: " + secureHatchSolenoid.get());
	}
	public void secureHatch(){
		System.out.println("Securing Hatch");
		secureHatchSolenoid.set(Value.kForward);
		System.out.println("solenoid: " + secureHatchSolenoid.get());
	}

	public void openArm(){
		System.out.println("Opening Arm");
		armSolenoid.set(Value.kForward);
	}
	public void closeArm(){
		System.out.println("Closing Arm");
		armSolenoid.set(Value.kReverse);
	}
	public boolean isArmOpen(){
		return armSolenoid.get() == Value.kForward;
	}
	public void toggleArm(){
		if (isArmOpen()){
			closeArm();
		} else {
			openArm();
		}
	}

	public void updateHoldingHatch(boolean holdingHatch){
		this.holdingHatch = holdingHatch;
	}

	public boolean holdingHatch(){ // assumes when the driver tries to secure a hatch, the robot actually has it
		return holdingHatch;
	}

	public boolean holdingCargo() { // assumes when the drvier tries to intake cargo, the robot actually has it
		return holdingCargo;
	}

	public void setIntakeSpeed(double speed){
		Utils.setTalon(intakeTalon, speed);
	}

    public void suck() {
		holdingCargo = true; // We assume that sucknig means we have the cargo. W/o limit switches it is the best we can do
        setIntakeSpeed(SUCK_SPEED);
    }

    public void spit() {
		holdingCargo = false;
        setIntakeSpeed(SPIT_SPEED);
	}

	public void secureCargo() {
		setIntakeSpeed(SECURE_CARGO_SPEED);
	}

	public boolean holdingGamePiece() {
		return holdingCargo() || holdingHatch();
	}

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}