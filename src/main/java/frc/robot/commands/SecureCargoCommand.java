package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.logging.Logger.DefaultValue;


public class SecureCargoCommand extends InstantCommand {
    private final String fileName = "SecureCargoCommand.java";

    public SecureCargoCommand(){}

    @Override protected void execute(){
        Robot.logger.addData(this.fileName, Robot.logger.commandStatus, "executing", DefaultValue.Empty);
        Robot.intakeSubsystem.secureCargo();
    }
}