package frc.robot.subsystems;

import frc.robot.Utils;
import frc.robot.PortMap;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DigitalInput;

public class IntakeSubsystem extends Subsystem {

	public enum IntakeMode {Upright, Normal}
	public enum HatchControl {Deposit, Normal}
    private TalonSRX intakeTalon;
	private DoubleSolenoid intakeModeSolenoid, hatchControlSolenoid;
	private DigitalInput ballLimitSwitch, hatchLimitSwitch;
	private double lastHeld;
	private Timer timer;
	public final static double SUCK_IF_OUT_PERIOD = 1; // The amount of time that the intake should suck if the ball stops pressing the button in seconds

    public IntakeSubsystem() {
		timer = new Timer();
		timer.start();
		lastHeld = timer.get() - SUCK_IF_OUT_PERIOD;

		intakeTalon = new TalonSRX(PortMap.INTAKE_TALON);
		hatchControlSolenoid = new DoubleSolenoid(PortMap.HATCH_CONTROL_SOLENOID[0], PortMap.HATCH_CONTROL_SOLENOID[1]);
		intakeModeSolenoid = new DoubleSolenoid(PortMap.INTAKE_MODE_SOLENOID[0], PortMap.INTAKE_MODE_SOLENOID[1]);
		ballLimitSwitch = new DigitalInput(PortMap.BALL_LIMIT_SWITCH);
        hatchLimitSwitch = new DigitalInput(PortMap.HATCH_LIMIT_SWITCH);
	}

    public boolean stateOfLimitSwitch() { // True is closed, false is open
        return (!ballLimitSwitch.get() || !hatchLimitSwitch.get());
    }

	public void updateHatchControl (HatchControl control) {
		if (control == HatchControl.Deposit) {
			hatchControlSolenoid.set(Value.kForward);
		} else {
			hatchControlSolenoid.set(Value.kReverse);
		}
	}

	public void updateIntakeMode (IntakeMode mode) {
		if (mode == IntakeMode.Upright) {
			intakeModeSolenoid.set(Value.kForward);
		} else {
			intakeModeSolenoid.set(Value.kReverse);
		}
	}

	public boolean holdingHatch(){
		return hatchLimitSwitch.get();
	}

	public boolean holdingCargo(){
		boolean holding = ballLimitSwitch.get();
		if (holding){
			lastHeld = timer.get();
		}
		return holding;
	}

    public void suck() {
        Utils.setTalon(intakeTalon, 1);
    }

    public void spit() {
        Utils.setTalon(intakeTalon, -1);
	}

	public boolean cargoLoose() {
		return !holdingCargo() && (timer.get() - lastHeld) < SUCK_IF_OUT_PERIOD;
	}
	
	public void secureCargo() {
		if (cargoLoose()){
			Utils.setTalon(intakeTalon, .6);
		}
	}

	public boolean holdingGamePiece() {
		return cargoLoose() || holdingCargo() || holdingHatch();
	}

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}