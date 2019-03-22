package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;

public class StopIntakeWheelsCommand extends InstantCommand {

    public StopIntakeWheelsCommand() {
        requires(Robot.intakeSubsystem);
    }

    @Override protected void execute() {
        Robot.intakeSubsystem.setIntakeSpeed(0);
    }
}