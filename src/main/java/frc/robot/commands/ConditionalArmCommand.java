/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.robot.Robot;

public class ConditionalArmCommand extends ConditionalCommand {
    private boolean condition = false;
    
    public ConditionalArmCommand(Command onTrue, Command onFalse) {
        super(onTrue, onFalse);
        requires(Robot.armSubsystem);
    }

    @Override
    protected boolean condition() {
        condition = !condition;
        return condition;
    }
}