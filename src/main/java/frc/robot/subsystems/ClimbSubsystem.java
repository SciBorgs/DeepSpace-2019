package frc.robot.subsystems;

import frc.robot.PortMap;
import frc.robot.Robot;
import frc.robot.Utils;
import frc.robot.logging.Logger.DefaultValue;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.DoubleSolenoid

public class ClimbSubsystem extends Subsystem {

	private DoubleSolenoid frontSoldenoid, backSoldenoid;

	public ClimbSubsystem() {
		frontSolenoid = new DoubleSolenoid(PortMap.FRONT_CLIMB_SOLENOID[0], PortMap.FRONT_CLIMB_SOLENOID[1]);
		backSolenoid  = new DoubleSolenoid(PortMap.BACK_CLIMB_SOLENOID[0], PortMap.BACK_CLIMB_SOLENOID[1]);
	}

	public void extendFront() {
		frontSolenoid.set(Value.kForward);
	}

	public void extendBack() {
		backSolenoid.set(Value.kForward);
	}

	public void retractFront() {
		frontSolenoid.set(Value.kReverse);
	}
 
	public void retractBack() {
		backSolenoid.set(Value.kReverse);	
	}
	
	@Override
	protected void initDefaultCommand() {}
}
