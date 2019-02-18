package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Robot;

public class HatchRetractCommand extends InstantCommand {

    public HatchRetractCommand() {
        requires(Robot.intakeSubsystem);
    }

    @Override protected void execute() {
        Robot.intakeSubsystem.retractHatchPanel();
    }
}