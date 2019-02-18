package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Robot;

public class CargoFollowCommand extends InstantCommand {

    public CargoFollowCommand() {
        requires(Robot.driveSubsystem);
    }

    @Override protected void execute() {
        Robot.cargoFollowing.followBall();
        Robot.intakeSubsystem.suck();
    }
}