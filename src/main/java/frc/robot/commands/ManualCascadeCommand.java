package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.logging.Logger.CommandStatus;

public class ManualCascadeCommand extends InstantCommand{
    private static final String FILENAME = "ManualCascadeCommand.java";
    public static double MANUAL_CASCADE_INPUT  = 1;

    public ManualCascadeCommand(){}

    @Override protected void execute(){
        Robot.logger.logCommandStatus(FILENAME, CommandStatus.Executing);

        if(Robot.oi.leftStick.getPOV() == 0) {
            Robot.liftSubsystem.setLiftSpeed(MANUAL_CASCADE_INPUT);
        }else if(Robot.oi.leftStick.getPOV() == 180) {
            Robot.liftSubsystem.setLiftSpeed(-MANUAL_CASCADE_INPUT);
        }else if(Robot.liftSubsystem.manualCascadeMode) {
            Robot.liftSubsystem.setLiftSpeed(0);
        }
    }
}