package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Robot;

public class HatchReleaseCommand extends InstantCommand {

    public HatchReleaseCommand() {
        requires(Robot.intakeSubsystem);
    }

    @Override protected void execute() {
        Robot.intakeSubsystem.depositHatchPanel();
    }
}