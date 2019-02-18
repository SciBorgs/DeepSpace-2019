package frc.robot.subsystems;

import frc.robot.Robot;
import frc.robot.Utils;
import frc.robot.PortMap;

import edu.wpi.first.wpilibj.Timer;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DigitalInput;

public class IntakeSubsystem extends Subsystem {

    private TalonSRX intakeTalon;
	private DoubleSolenoid panelSolenoidIntake;
	public static DigitalInput ballLimitSwitch, hatchLimitSwitch;
	private double lastHeld;
	private Timer timer;
	public final static double SUCK_IF_OUT_PERIOD = 1; // The amount of time that the intake should suck if the ball stops pressing the button in seconds

    public IntakeSubsystem() {
		timer = new Timer();
		timer.start();
		lastHeld = timer.get() - SUCK_IF_OUT_PERIOD;

		intakeTalon = new TalonSRX(PortMap.INTAKE_TALON);
		panelSolenoidIntake = new DoubleSolenoid(PortMap.INTAKE_SOLENOID[0], PortMap.INTAKE_SOLENOID[1]);
		ballLimitSwitch = new DigitalInput(PortMap.BALL_LIMIT_SWITCH);
        hatchLimitSwitch = new DigitalInput(PortMap.HATCH_LIMIT_SWITCH);
	}

    public boolean stateOfLimitSwitch() { // True is closed, false is open
        return (!ballLimitSwitch.get() || !hatchLimitSwitch.get());
    }

    public void depositHatchPanel() {
		panelSolenoidIntake.set(DoubleSolenoid.Value.kForward);
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