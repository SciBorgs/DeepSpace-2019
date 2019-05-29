package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.logging.Logger.CommandStatus;
import frc.robot.logging.Logger.DefaultValue;

public class CloseArmCommand extends InstantCommand {
    private final String fileName = "CloseArmCommand.java";

    public CloseArmCommand(){ requires(Robot.intakeSubsystem) }

    @Override 
    protected void execute(){
	Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);
        Robot.intakeSubsystem.closeArm();
    }
}
