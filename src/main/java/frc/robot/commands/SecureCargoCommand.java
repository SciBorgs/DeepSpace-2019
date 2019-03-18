package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;

public class SecureCargoCommand extends InstantCommand {
    
    public SecureCargoCommand(){}

    @Override protected void execute(){
        Robot.intakeSubsystem.secureCargo();
    }
}