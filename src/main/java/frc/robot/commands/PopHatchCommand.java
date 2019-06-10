
package frc.robot.commands;

import edu.wpi.first.wpilibj.command.TimedCommand;
import frc.robot.Robot;

public class PopHatchCommand extends TimedCommand {
  public static final double TIMEOUT = .2;
  public PopHatchCommand() { super("pop hatch", TIMEOUT);}

  @Override
  protected void initialize() {
    Robot.intakeSubsystem.extendPopHatchPistons();
  }

  @Override
  protected void end() {
    Robot.intakeSubsystem.retractPopHatchPistons();
  }

  @Override
  protected void interrupted() { end(); }
}
