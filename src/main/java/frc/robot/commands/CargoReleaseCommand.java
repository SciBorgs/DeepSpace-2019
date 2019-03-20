package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Robot;

public class CargoReleaseCommand extends InstantCommand {

    public CargoReleaseCommand() {
        requires(Robot.intakeSubsystem);
    }

    @Override protected void execute() {
        System.out.println("releasing cargo");
        Robot.intakeSubsystem.openArm();
        Robot.intakeSubsystem.spit();
    }
    
}