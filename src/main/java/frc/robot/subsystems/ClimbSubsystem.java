package frc.robot.subsystems;

import frc.robot.PortMap;
import frc.robot.Robot;
import frc.robot.Utils;
import frc.robot.logging.Logger.DefaultValue;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class ClimbSubsystem extends Subsystem {

	private DoubleSolenoid frontSolenoid, backSolenoid;
	private Value defaultValue = Value.kForward; 

	public ClimbSubsystem() {
		frontSolenoid = new DoubleSolenoid(PortMap.FRONT_CLIMB_SOLENOID[0], PortMap.FRONT_CLIMB_SOLENOID[1]);
		backSolenoid  = new DoubleSolenoid(PortMap.BACK_CLIMB_SOLENOID[0], PortMap.BACK_CLIMB_SOLENOID[1]);
		frontSolenoid.set(defaultValue);
		backSolenoid.set(defaultValue);
	}

	public void toggleFront() {
		Utils.toggleDoubleSolenoid(frontSolenoid);
	}

	public void toggleBack() {
		Utils.toggleDoubleSolenoid(backSolenoid);
	}
	
	@Override
	protected void initDefaultCommand() {}
}
