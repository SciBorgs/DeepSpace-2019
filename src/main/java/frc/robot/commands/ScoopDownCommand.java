package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;

public class ScoopDownCommand extends InstantCommand {
    
    public ScoopDownCommand(){}

    @Override protected void execute(){
        Robot.intakeSubsystem.scoopDown();
    }
}