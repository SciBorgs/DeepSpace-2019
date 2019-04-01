package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.logging.Logger.DefaultValue;


public class StopIntakeWheelsCommand extends InstantCommand {
    private final String fileName = "StilIntakeWheelsCommand.java";

    public StopIntakeWheelsCommand() {
        requires(Robot.intakeSubsystem);
    }

    @Override protected void execute() {
        Robot.logger.addData(this.fileName, Robot.logger.commandStatus, "executing", DefaultValue.Empty);
        Robot.intakeSubsystem.setIntakeSpeed(0);
    }
}