package frc.robot.commands;

import frc.robot.Robot;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.DoubleSolenoid;


import edu.wpi.first.wpilibj.command.Command;
import frc.robot.logging.Logger.CommandStatus;
import frc.robot.logging.Logger.DefaultValue;


public class GearShiftCommand extends Command {

    private final String fileName = "GearShiftCommand.java";

	public GearShiftCommand() {
        requires(Robot.gearShiftSubsystem);
	}

    @Override protected void initialize(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Initializing);
        Robot.gearShiftSubsystem.gearShiftSolenoid.set(DoubleSolenoid.Value.kForward);
    }

	@Override protected void execute(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);
        //Robot.gearShiftSubsystem.shiftGear();
        
    }
    
    @Override protected boolean isFinished(){
        return false;
    }
    @Override protected void end(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Ending);
        Robot.gearShiftSubsystem.gearShiftSolenoid.set(DoubleSolenoid.Value.kReverse);
    }
    @Override protected void interrupted(){
        Robot.logger.logCommandStatus(this.fileName, CommandStatus.Interrupted);
        end();
    }
}