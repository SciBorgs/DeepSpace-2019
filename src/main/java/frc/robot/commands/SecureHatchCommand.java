package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;

public class SecureHatchCommand extends InstantCommand {
    
    public SecureHatchCommand(){}

    @Override protected void execute(){
        Robot.intakeSubsystem.secureHatch();
        Robot.intakeSubsystem.toggleHoldingHatch();
        if (Robot.intakeSubsystem.holdingHatch()){
            Robot.intakeSubsystem.openArm();
        }
    }
}