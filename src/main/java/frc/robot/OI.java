package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.robot.commands.*;
import frc.robot.subsystems.IntakeSubsystem.HatchDepositControl;
import frc.robot.subsystems.IntakeSubsystem.IntakeMode;

public class OI {
    public Joystick leftStick, rightStick;
    public JoystickButton lineupButton, startZLiftButton, liftLevelUpButton, liftLevelDownButton, suckButton, spitButton, depositPanelButton, intakeModeButton;

    public OI() {
        leftStick = new Joystick(PortMap.JOYSTICK_LEFT);
        rightStick = new Joystick(PortMap.JOYSTICK_RIGHT);

        // Left Stick
        lineupButton = new JoystickButton(leftStick, PortMap.JOYSTICK_LEFT_BUTTON);
        lineupButton.whenPressed(new ResetLineupInfoCommand());
        lineupButton.whileHeld(new LineupCommand());

        liftLevelUpButton = new JoystickButton(leftStick, PortMap.JOYSTICK_CENTER_BUTTON);
        liftLevelUpButton.whenPressed(new LevelCounterUpdate(LevelCounterUpdate.LevelChange.Up));
        liftLevelUpButton.whenReleased(new ConditionalLiftCommand());

        depositPanelButton = new JoystickButton(leftStick, PortMap.JOYSTICK_RIGHT_BUTTON);
        depositPanelButton.whenPressed(new HatchControlCommand(HatchDepositControl.Deposit));
        depositPanelButton.whenReleased(new HatchControlCommand(HatchDepositControl.Hold));

        liftLevelDownButton = new JoystickButton(leftStick, PortMap.JOYSTICK_TRIGGER);
        liftLevelDownButton.whenPressed(new LevelCounterUpdate(LevelCounterUpdate.LevelChange.Down));
        liftLevelDownButton.whenReleased(new ConditionalLiftCommand());

        // Right Stick
        startZLiftButton = new JoystickButton(rightStick, PortMap.JOYSTICK_LEFT_BUTTON);
        startZLiftButton.whenPressed(new ZLiftCommand(leftStick, rightStick));

        suckButton = new JoystickButton(rightStick, PortMap.JOYSTICK_CENTER_BUTTON);
        suckButton.whileHeld(new ConditionalSuckCommand());
        
        spitButton = new JoystickButton(rightStick, PortMap.JOYSTICK_RIGHT_BUTTON);
        spitButton.whileHeld(new CargoReleaseCommand());

        intakeModeButton = new JoystickButton(rightStick, PortMap.JOYSTICK_TRIGGER);
        intakeModeButton.whenPressed(new IntakeModeCommand(IntakeMode.Normal));
        intakeModeButton.whenReleased(new IntakeModeCommand(IntakeMode.Upright));

    }
}