package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;

public class ScoopUpCommand extends InstantCommand {
    
    public ScoopUpCommand(){}

    @Override protected void execute(){
        Robot.intakeSubsystem.scoopUp();
    }
}