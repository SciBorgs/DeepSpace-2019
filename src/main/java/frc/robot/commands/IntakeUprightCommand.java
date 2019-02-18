package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Robot;

public class IntakeUprightCommand extends InstantCommand {

    public IntakeUprightCommand() {
        requires(Robot.intakeSubsystem);
    }

    @Override protected void execute() {
        Robot.intakeSubsystem.uprightIntakeMode();
    }
}