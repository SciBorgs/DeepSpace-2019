package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;

public class CloseArmCommand extends InstantCommand {

    public CloseArmCommand() {
        requires(Robot.intakeSubsystem);
    }

    @Override protected void execute() {
        System.out.println("closing arm");
        Robot.intakeSubsystem.closeArm();
    }
}