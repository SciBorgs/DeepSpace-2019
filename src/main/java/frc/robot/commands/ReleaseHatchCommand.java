package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.logging.Logger.CommandStatus;
import frc.robot.logging.Logger.CommandStatus;
import frc.robot.logging.Logger.DefaultValue;

public class ReleaseHatchCommand extends InstantCommand {
    private final String fileName = "ReleaseHatchCommand.java";
    
    private double RELEASE_HATCH_DRIVE_MULTIPLIER = 0.5;

    public ReleaseHatchCommand(){}

    @Override protected void execute(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);
        System.out.println("Release Hatch command");
        Robot.intakeSubsystem.releaseHatch();
        Robot.intakeSubsystem.closeArm();
        Robot.intakeSubsystem.updateHoldingHatch(false);
        Robot.driveSubsystem.setTankMultiplier(RELEASE_HATCH_DRIVE_MULTIPLIER);
        Robot.gearShiftSubsystem.shiftUp();
    }
}