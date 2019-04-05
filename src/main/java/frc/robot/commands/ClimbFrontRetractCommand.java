package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Robot;
import frc.robot.logging.Logger.CommandStatus;
import frc.robot.logging.Logger.DefaultValue;

public class ClimbFrontRetractCommand extends InstantCommand {
    private final String fileName = "ClimbFrontRetractCommand.java";

    public ClimbFrontRetractCommand(){
        requires(Robot.climbSubsystem);
    }

    @Override protected void execute() {
        Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);
        System.out.println("front climb retract");
        Robot.climbSubsystem.retractFront();
    }

}