package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.helpers.RetroreflectiveDetection;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.InstantCommand;


public class ResetLineupInfoCommand extends InstantCommand {
    
    public ResetLineupInfoCommand() {}

    @Override protected void execute(){
        Robot.lineup.resetFound();
    }
}
