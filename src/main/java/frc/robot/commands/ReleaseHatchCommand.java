package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;

public class ReleaseHatchCommand extends InstantCommand {
    
    public ReleaseHatchCommand(){}

    @Override protected void execute(){
        Robot.intakeSubsystem.scoopDown();
    }
}