package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.helpers.RetroreflectiveDetection;

public class SwitchToRetroreflectiveCommand extends Command {

    public SwitchToRetroreflectiveCommand() {
    }

    @Override protected void    initialize()  {RetroreflectiveDetection.modeToRetroreflective();}
    @Override protected void    execute()     {return;}
    @Override protected boolean isFinished()  {return true;}
    @Override protected void    end()         {return;}
    @Override protected void    interrupted() {end();}
}