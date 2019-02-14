package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.robot.commands.*;
import frc.robot.subsystems.LiftSubsystem.Target;

public class OI {
    public Joystick rightStick, leftStick;
    public JoystickButton switchToRetroreflectiveButton, followBallButton, lineupButton, startZLift, liftLow, liftMid, liftHigh;

    public OI() {
        rightStick = new Joystick(PortMap.JOYSTICK_RIGHT);
        leftStick = new Joystick(PortMap.JOYSTICK_LEFT);

        switchToRetroreflectiveButton = new JoystickButton(rightStick, PortMap.JOYSTICK_TOGGLE_OBJECTIVE);
        switchToRetroreflectiveButton.whenPressed(new SwitchToRetroreflectiveCommand());
        switchToRetroreflectiveButton.whenReleased(new SwitchToCargoCommand());
        
        followBallButton = new JoystickButton(leftStick, PortMap.JOYSTICK_FOLLOW_CARGO_BUTTON);
        //followBallButton.whileHeld(new CargoFollowCommand());

        lineupButton = new JoystickButton(leftStick, PortMap.JOYSTICK_LINEUP_BUTTON);
        //lineupButton.whenPressed(new ResetLineupInfoCommand());
        lineupButton.whileHeld(new LineupCommand());

        startZLift = new JoystickButton(rightStick, PortMap.JOYSTICK_Z_LIFT_BUTTON);
        //startZLift.whenPressed(new ZLiftCommand(false));
        //startZLift.whenReleased(new ZLiftCommand(true));

        //liftLow.whenPressed(new LiftCommand(Target.Low));
        //liftMid.whenPressed(new LiftCommand(Target.Mid));
        //liftHigh.whenPressed(new LiftCommand(Target.High));
    }
}