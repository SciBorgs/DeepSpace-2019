package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;

public class ReleaseHatchCommand extends InstantCommand {
    
    private double RELEASE_HATCH_DRIVE_MULTIPLIER = 0.5;

    public ReleaseHatchCommand(){}

    @Override protected void execute(){
        System.out.println("Release Hatch command");
        Robot.intakeSubsystem.releaseHatch();
        Robot.intakeSubsystem.closeArm();
        Robot.intakeSubsystem.updateHoldingHatch(false);
        Robot.driveSubsystem.setTankMultiplier(RELEASE_HATCH_DRIVE_MULTIPLIER);
        Robot.gearShiftSubsystem.shiftUp();
    }
}