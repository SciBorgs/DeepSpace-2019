/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.TimedCommand;
import frc.robot.Robot;

public class PopHatchCommand extends TimedCommand {
  public static final double TIMEOUT = .2;
  public PopHatchCommand() {
    super("pop hatch", TIMEOUT);
  }

  @Override
  protected void initialize() {
    System.out.println("popping");
    Robot.intakeSubsystem.extendPopHatchPistons();
  }

  @Override
  protected void end() {
    System.out.println("retracting")
    Robot.intakeSubsystem.retractPopHatchPistons();
  }

  @Override
  protected void interrupted() {
    end();
  }
}
