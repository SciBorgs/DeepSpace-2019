/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.robot.commands.ConditionalDriveCommand;
import frc.robot.commands.FieldCentricDriveCommand;
import frc.robot.commands.RobotCentricDriveCommand;

public class OI {
    public Joystick rightStick, leftStick;
    public JoystickButton switchCentricDriving;

    public OI() {
        rightStick = new Joystick(PortMap.RIGHT_STICK);
        leftStick = new Joystick(PortMap.LEFT_STICK);
        switchCentricDriving = new JoystickButton(rightStick, PortMap.RIGHT_JOYSTICK_BUTTON);

        switchCentricDriving.whenPressed(
                new ConditionalDriveCommand(new FieldCentricDriveCommand(), new RobotCentricDriveCommand()));
    }
}