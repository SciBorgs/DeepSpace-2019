package frc.robot.subsystems;

import frc.robot.Robot;
import frc.robot.Utils;
import frc.robot.PortMap;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DigitalInput;

public class IntakeSubsystem extends Subsystem {

    private TalonSRX intakeTalon;
	private DoubleSolenoid panelSolenoidIntake;
	public static DigitalInput ballLimitSwitch, hatchLimitSwitch;

    public IntakeSubsystem() {
		intakeTalon = new TalonSRX(PortMap.INTAKE_TALON);
		panelSolenoidIntake = new DoubleSolenoid(PortMap.INTAKE_SOLENOID[0], PortMap.INTAKE_SOLENOID[0]);
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
		return ballLimitSwitch.get();
	}

    public void suck() {
        if (!(holdingCargo() && Robot.oi.spitButton.get())) {
            Utils.setTalon(intakeTalon, .6);
        } else {
            Utils.setTalon(intakeTalon, 1);
        }
    }

    public void spit() {
        Utils.setTalon(intakeTalon, -1);
    }

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}