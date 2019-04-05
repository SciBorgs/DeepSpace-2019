package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Robot;
import frc.robot.logging.Logger.CommandStatus;
import frc.robot.logging.Logger.DefaultValue;

public class ClimbFrontExtendCommand extends InstantCommand {
    private final String fileName = "ClimbFrontExtendCommand.java";

    public ClimbFrontExtendCommand(){
        requires(Robot.climbSubsystem);
    }

    @Override protected void execute() {
        Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);
        System.out.println("front climb extend");
        Robot.climbSubsystem.ExtendFront();
    }
}