package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.logging.Logger.CommandStatus;


public class SecureCargoCommand extends InstantCommand {
    private final String FILENAME = "SecureCargoCommand.java";

    public SecureCargoCommand(){}

    @Override protected void execute(){
		Robot.logger.logCommandStatus(this.FILENAME, CommandStatus.Executing);
        Robot.intakeSubsystem.secureCargo();
    }
}
