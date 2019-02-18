package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Robot;

public class CargoReleaseCommand extends InstantCommand {

    public CargoReleaseCommand() {
        requires(Robot.intakeSubsystem);
    }

    @Override protected void execute() {
        Robot.intakeSubsystem.spit();
    }
}