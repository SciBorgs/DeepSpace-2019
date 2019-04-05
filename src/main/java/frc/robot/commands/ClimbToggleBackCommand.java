package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Robot;
import frc.robot.logging.Logger.CommandStatus;
import frc.robot.logging.Logger.DefaultValue;

public class ClimbBackExtendCommand extends InstantCommand {
    private final String fileName = "ClimbBackExtendCommand.java";

    public ClimbBackExtendCommand(){
        requires(Robot.climbSubsystem);
    }

    @Override protected void execute() {
        Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);
        System.out.println("back climb extend");
        Robot.climbSubsystem.ExtendBack();
    }
}