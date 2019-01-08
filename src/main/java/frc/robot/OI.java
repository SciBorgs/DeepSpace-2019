/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

public class OI {
    // Temporary Joystick ports
    public final static Joystick rightStick = new Joystick(PortMap.RIGHT_STICK),
            leftStick = new Joystick(PortMap.LEFT_STICK);
}