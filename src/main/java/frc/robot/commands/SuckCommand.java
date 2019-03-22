package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;

public class SuckCommand extends InstantCommand {

    public SuckCommand() {
        requires(Robot.intakeSubsystem);
    }

    @Override protected void execute() {
        System.out.println("suck");
        Robot.intakeSubsystem.openArm();
        Robot.intakeSubsystem.suck();
    }
}
