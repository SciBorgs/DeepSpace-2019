package frc.robot.commands;

import frc.robot.Robot;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.DoubleSolenoid;


import edu.wpi.first.wpilibj.command.Command;

public class GearShiftCommand extends Command {

    private final String fileName = "GearShiftCommand.java";

	public GearShiftCommand() {
        requires(Robot.gearShiftSubsystem);
	}

    @Override protected void initialize(){
        Robot.gearShiftSubsystem.gearShiftSolenoid.set(DoubleSolenoid.Value.kForward);
    }

	@Override protected void execute(){
        //Robot.gearShiftSubsystem.shiftGear();
        
    }
    
    @Override protected boolean isFinished(){
        return false;
    }
    @Override protected void end(){
        Robot.gearShiftSubsystem.gearShiftSolenoid.set(DoubleSolenoid.Value.kReverse);
    }
}