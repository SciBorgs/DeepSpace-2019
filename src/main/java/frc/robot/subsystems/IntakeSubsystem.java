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
	public DoubleSolenoid scoopSolenoid, secureHatchSolenoid;
	private DigitalInput ballLimitSwitch, hatchLimitSwitch;
	private double lastHeld;
	private Timer timer;
	public final static double SUCK_SPEED = 0.5;
	public final static double SPIT_SPEED = SUCK_SPEED * -2;
	public final static double PICKUP_HATCH_SPEED = -0.3;
	public final static double SUCK_IF_OUT_PERIOD = 1; // The amount of time that the intake should suck if the ball stops pressing the button in seconds
	public final static double SECURE_CARGO_SPEED = 0.6;

    public IntakeSubsystem() {
		timer = new Timer();
		timer.start();
		lastHeld = timer.get() - SUCK_IF_OUT_PERIOD;
		intakeTalon = new TalonSRX(PortMap.INTAKE_TALON);
		intakeTalon.setNeutralMode(NeutralMode.Brake);
		secureHatchSolenoid = new DoubleSolenoid(PortMap.SECURE_HATCH_SOLENOID[0], PortMap.SECURE_HATCH_SOLENOID[1]);
		scoopSolenoid = new DoubleSolenoid(PortMap.SCOOP_SOLENOID[0], PortMap.SCOOP_SOLENOID[1]);
		ballLimitSwitch = new DigitalInput(PortMap.BALL_LIMIT_SWITCH);
        hatchLimitSwitch = new DigitalInput(PortMap.HATCH_LIMIT_SWITCH);
	}

	public void scoopUp() {
		scoopSolenoid.set(Value.kForward);
	}
	public void scoopDown(){ 
		scoopSolenoid.set(Value.kReverse);
	}

	public void releaseHatch(){
		secureHatchSolenoid.set(Value.kForward);
	}
	public void secureHatch(){
		secureHatchSolenoid.set(Value.kReverse);
	}

	public boolean holdingHatch(){
		return !(hatchLimitSwitch.get());
	}

	public boolean holdingCargoSecure(){
		boolean holding = !ballLimitSwitch.get();
		if (holding){
			lastHeld = timer.get();
		}
		return holding;
	}

	public boolean cargoLoose() {
		return !holdingCargoSecure() && (timer.get() - lastHeld) < SUCK_IF_OUT_PERIOD;
	}

	public boolean holdingCargo() {
		return holdingCargoSecure() || cargoLoose();
	}

	public void setIntakeSpeed(double speed){
		Utils.setTalon(intakeTalon, speed);
	}

    public void suck() {
        setIntakeSpeed(SUCK_SPEED);
    }

    public void spit() {
        setIntakeSpeed(SPIT_SPEED);
	}

	public void secureCargo() {
		if (cargoLoose() && intakeTalon.getMotorOutputPercent() == 0){
			Utils.setTalon(intakeTalon, SECURE_CARGO_SPEED);
		}
	}

	public boolean holdingGamePiece() {
		return holdingCargo() || holdingHatch();
	}

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}