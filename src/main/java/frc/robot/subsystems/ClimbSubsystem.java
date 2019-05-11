package frc.robot.subsystems;

import frc.robot.PortMap;
import frc.robot.Robot;
import frc.robot.Utils;
import frc.robot.logging.Logger.DefaultValue;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

// FILE HAS NOT BEEN CLEANED UP //
public class ClimbSubsystem extends Subsystem {

	private DoubleSolenoid frontSolenoid, backSolenoid;

	public ClimbSubsystem() {
		//UNCOMMENT TO USE CLIMBER!!!

		// frontSolenoid = new DoubleSolenoid(0, PortMap.FRONT_CLIMB_SOLENOID[0], PortMap.FRONT_CLIMB_SOLENOID[1]);
		// backSolenoid  = new DoubleSolenoid(1, PortMap.BACK_CLIMB_SOLENOID[0], PortMap.BACK_CLIMB_SOLENOID[1]);
		//frontSolenoid.set(Value.kReverse); //YOU MIGHT HAVE TO REVERSE THIS
		//backSolenoid.set(Value.kForward); 
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
