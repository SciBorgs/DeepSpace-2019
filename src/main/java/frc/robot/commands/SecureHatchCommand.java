package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.logging.Logger.DefaultValue;

public class SecureHatchCommand extends InstantCommand {
    private final String fileName = "SecureHatchCommand.java";

    public SecureHatchCommand(){}

    @Override protected void execute(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);
        Robot.driveSubsystem.defaultTankMultilpier();
        Robot.intakeSubsystem.secureHatch();
        Robot.gearShiftSubsystem.shiftDown();
    }
}