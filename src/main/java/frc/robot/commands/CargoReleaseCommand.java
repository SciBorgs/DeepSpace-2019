package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class CargoReleaseCommand extends Command {

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
        return !Robot.oi.spitButton.get();
    }
    @Override protected void end() {
        Robot.intakeSubsystem.setIntakeSpeed(0);
    }
    @Override protected void interrupted() {
        end();
    }
}