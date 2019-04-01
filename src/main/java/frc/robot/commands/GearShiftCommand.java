package frc.robot.commands;

import frc.robot.Robot;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.DoubleSolenoid;


import edu.wpi.first.wpilibj.command.Command;
import frc.robot.logging.Logger.DefaultValue;


public class GearShiftCommand extends Command {

    private final String fileName = "GearShiftCommand.java";

	public GearShiftCommand() {
        requires(Robot.gearShiftSubsystem);
	}

    @Override protected void initialize(){
        Robot.logger.addData(fileName, Robot.logger.commandStatus, "initializing", DefaultValue.Empty);
        Robot.gearShiftSubsystem.gearShiftSolenoid.set(DoubleSolenoid.Value.kForward);
    }

	@Override protected void execute(){
        Robot.logger.addData(this.fileName, Robot.logger.commandStatus, "executing", DefaultValue.Empty);
        //Robot.gearShiftSubsystem.shiftGear();
        
    }
    
    @Override protected boolean isFinished(){
        return false;
    }
    @Override protected void end(){
        Robot.logger.addData(this.fileName, Robot.logger.commandStatus, "ending", DefaultValue.Empty);
        Robot.gearShiftSubsystem.gearShiftSolenoid.set(DoubleSolenoid.Value.kReverse);
    }
}