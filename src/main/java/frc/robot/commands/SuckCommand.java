package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;

public class SuckCommand extends Command {

    public SuckCommand() {
        requires(Robot.intakeSubsystem);
    }

    @Override protected void initialize() {
        Robot.intakeSubsystem.suck();
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
