package frc.robot.subsystems;

import frc.robot.Utils;
import frc.robot.PortMap;
import frc.robot.Robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DigitalInput;

public class IntakeSubsystem extends Subsystem {

	public enum IntakeMode {Upright, Normal}
	public enum HatchDepositControl {Deposit, Hold}
    private TalonSRX intakeTalon;
	private DoubleSolenoid intakeModeSolenoid, hatchControlSolenoid;
	private DigitalInput ballLimitSwitch, hatchLimitSwitch;
	private double lastHeld;
	private Timer timer;
	public final static double SUCK_SPEED = 0.5;
	public final static double SPIT_SPEED = SUCK_SPEED * -2;
	public final static double PICKUP_HATCH_SPEED = -0.3;
	public final static double SUCK_IF_OUT_PERIOD = 1; // The amount of time that the intake should suck if the ball stops pressing the button in seconds

    public IntakeSubsystem() {
		timer = new Timer();
		timer.start();
		lastHeld = timer.get() - SUCK_IF_OUT_PERIOD;

		intakeTalon = new TalonSRX(PortMap.INTAKE_TALON);
		hatchControlSolenoid = new DoubleSolenoid(PortMap.DEPOSIT_HATCH_PANEL_SOLENOID[0], PortMap.DEPOSIT_HATCH_PANEL_SOLENOID[1]);
		intakeModeSolenoid = new DoubleSolenoid(PortMap.INTAKE_MODE_SOLENOID[0], PortMap.INTAKE_MODE_SOLENOID[1]);
		ballLimitSwitch = new DigitalInput(PortMap.BALL_LIMIT_SWITCH);
        hatchLimitSwitch = new DigitalInput(PortMap.HATCH_LIMIT_SWITCH);
	}

	public void updateHatchControl (HatchDepositControl control) {
		if (control == HatchDepositControl.Deposit) {
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

	public boolean holdingCargoSecure(){
		boolean holding = ballLimitSwitch.get();
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

    public void suck() {
        Utils.setTalon(intakeTalon, SUCK_SPEED);
    }

    public void spit() {
        Utils.setTalon(intakeTalon, SPIT_SPEED);
	}

	public void secureCargo() {
		if (cargoLoose()){
			Utils.setTalon(intakeTalon, .6);
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