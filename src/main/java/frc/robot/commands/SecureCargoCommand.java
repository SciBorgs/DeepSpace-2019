package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.logging.Logger.CommandStatus;
import frc.robot.logging.Logger.DefaultValue;


public class SecureCargoCommand extends InstantCommand {
    private final String fileName = "SecureCargoCommand.java";

    public SecureCargoCommand(){}

    @Override protected void execute(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);
        Robot.intakeSubsystem.secureCargo();
    }
}