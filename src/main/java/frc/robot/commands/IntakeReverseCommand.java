package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Robot;

public class IntakeReverseCommand extends InstantCommand {

    public IntakeReverseCommand() {
        requires(Robot.intakeSubsystem);
    }

    @Override protected void execute() {
        Robot.intakeSubsystem.reverseIntakeMode();
    }
}