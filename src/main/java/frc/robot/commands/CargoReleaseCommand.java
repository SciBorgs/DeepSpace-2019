package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Robot;
import frc.robot.logging.Logger.DefaultValue;

public class CargoReleaseCommand extends InstantCommand {

    private final String fileName = "CargoReleaseCommand.java";

    public CargoReleaseCommand() {
        requires(Robot.intakeSubsystem);
    }

    @Override protected void execute() {
        Robot.logger.addData(this.fileName, Robot.logger.commandStatus, "executing", DefaultValue.Empty);
        System.out.println("releasing cargo");
        Robot.intakeSubsystem.openArm();
        Robot.intakeSubsystem.spit();
    }
    
}