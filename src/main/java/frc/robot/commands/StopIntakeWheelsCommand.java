package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.logging.Logger.DefaultValue;
import frc.robot.logging.Logger.CommandStatus;


public class StopIntakeWheelsCommand extends InstantCommand {
    private final String fileName = "StilIntakeWheelsCommand.java";

    public StopIntakeWheelsCommand() {
        requires(Robot.intakeSubsystem);
    }

    @Override protected void execute() {
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);
        Robot.intakeSubsystem.setIntakeSpeed(0);
    }
}