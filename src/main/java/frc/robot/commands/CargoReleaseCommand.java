package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Robot;
import frc.robot.logging.Logger.CommandStatus;

public class CargoReleaseCommand extends InstantCommand {

    private final String fileName = "CargoReleaseCommand.java";

    public CargoReleaseCommand() {
        requires(Robot.intakeSubsystem);
    }

    @Override protected void execute() {
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);
        Robot.intakeSubsystem.openArm();
        Robot.intakeSubsystem.spit();
    }
    
}