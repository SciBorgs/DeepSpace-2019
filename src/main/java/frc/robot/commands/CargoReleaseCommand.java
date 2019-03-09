package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Robot;

public class CargoReleaseCommand extends InstantCommand {

    public CargoReleaseCommand() {
        requires(Robot.intakeSubsystem);
    }

    @Override protected void initialize() {
        Robot.intakeSubsystem.spit();
    }

    @Override protected void execute() {
        return;
    }

    @Override protected boolean isFinished() {
        return !Robot.oi.suckButton.get();
    }
    @Override protected void end() {
        Robot.intakeSubsystem.setIntakeSpeed(0);
    }
    @Override protected void interrupted() {
        end();
    }
}