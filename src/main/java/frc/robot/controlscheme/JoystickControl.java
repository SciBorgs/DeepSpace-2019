package frc.robot.controlscheme;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import frc.robot.commands.*;
import frc.robot.helpers.Lineup;
import frc.robot.helpers.PID;
import frc.robot.subsystems.*;

/**
 * A control scheme for two {@link Joystick}
 */
public class JoystickControl implements ControlScheme {
    // ports
    private static final int JOYSTICK_LEFT = 1;
    private static final int JOYSTICK_RIGHT = 2;

    private final Joystick joystickLeft;
    private final Joystick joystickRight;

    private final GenericJoystick genericLeft;
    private final GenericJoystick genericRight;

    public JoystickControl() {
        joystickLeft = new Joystick(JOYSTICK_LEFT);
        joystickRight = new Joystick(JOYSTICK_RIGHT);

        genericLeft = new GenericJoystick() {
            @Override
            public double getX() {
                return joystickLeft.getX();
            }

            @Override
            public double getY() {
                return joystickLeft.getY();
            }
        };

        genericRight = new GenericJoystick() {
            @Override
            public double getX() {
                return joystickRight.getX();
            }

            @Override
            public double getY() {
                return joystickRight.getY();
            }
        };
    }

    /*
    @Override
    public void mapBindings(Lineup lineup, LiftSubsystem liftSubsystem, IntakeSubsystem intakeSubsystem, ZLiftSubsystem zLiftSubsystem, TankDriveSubsystem tankDriveSubsystem, LimelightSubsystem limelightSubsystem) {
        ControlButton lineupButton, startZLiftButton, liftLevelUpButton, liftLevelDownButton, suckButton, spitButton, depositPanelButton, intakeModeButton, hatchSecureModeButton, armModeButton, cargoFollowButton;

        // left stick
        lineupButton = new JoystickControlButton(joystickLeft, JoystickControlButton.Button.LEFT);
        //lineupButton.whenPressed(new ResetLineupInfoCommand());
        //lineupButton.whileHeld(new LineupCommand());
        lineupButton.whenPressed(new ResetLiftCommand(liftSubsystem));

        liftLevelUpButton = new JoystickControlButton(joystickLeft, JoystickControlButton.Button.CENTER);
        liftLevelUpButton.whenPressed(new LevelCounterUpdateCommand(liftSubsystem, LevelCounterUpdateCommand.LevelChange.Up));
        liftLevelUpButton.whenPressed(new LiftCommand(liftSubsystem));

        liftLevelDownButton = new JoystickControlButton(joystickLeft, JoystickControlButton.Button.TRIGGER);
        liftLevelDownButton.whenPressed(new LevelCounterUpdateCommand(liftSubsystem, LevelCounterUpdateCommand.LevelChange.Down));
        liftLevelDownButton.whenPressed(new LiftCommand(liftSubsystem));

        // Right Stick
        //startZLiftButton = new JoystickButton(rightStick, PortMap.JOYSTICK_LEFT_BUTTON);
        //startZLiftButton.whenPressed(new ZLiftCommand(leftStick, rightStick));

        suckButton = new JoystickControlButton(joystickLeft, JoystickControlButton.Button.LEFT);
        cargoFollowButton = liftLevelDownButton;
        suckButton.whenPressed(new ConditionalSuckCommand(tankDriveSubsystem, intakeSubsystem, limelightSubsystem, genericLeft, genericRight, suckButton, cargoFollowButton));
        suckButton.whenReleased(new CloseArmCommand(intakeSubsystem));
        suckButton.whenReleased(new SecureCargoCommand(intakeSubsystem));

        spitButton = new JoystickControlButton(joystickLeft, JoystickControlButton.Button.RIGHT);
        spitButton.whenPressed(new CargoReleaseCommand(intakeSubsystem));
        spitButton.whenReleased(new StopIntakeCommand(intakeSubsystem));

        hatchSecureModeButton = new JoystickControlButton(joystickRight, JoystickControlButton.Button.TRIGGER);
        hatchSecureModeButton.whenPressed(new ReleaseHatchCommand(intakeSubsystem));
        hatchSecureModeButton.whenReleased(new SecureHatchCommand(intakeSubsystem));

        armModeButton = new JoystickControlButton(joystickRight, JoystickControlButton.Button.CENTER);
        armModeButton.whenPressed(new ToggleArmCommand(intakeSubsystem));
    }
    */

    @Override
    public ShuffleboardCommand getShuffleboardCommand(PowerDistributionPanel pdp, LiftSubsystem liftSubsystem, PneumaticsSubsystem pneumaticsSubsystem, CANSparkMax[] canSparkMaxs, TalonSRX[] talonSRXs, CANSparkMax cascadeSpark, PID cargoPID, PID drivePID, double[] maxOmegaGoal, PID liftArmPID) {
        return new ShuffleboardCommand(pdp, liftSubsystem, pneumaticsSubsystem, canSparkMaxs, talonSRXs, cascadeSpark, null, null, null, cargoPID, drivePID, maxOmegaGoal, liftArmPID);
    }

    @Override
    public GenericJoystick getJoystickLeft() {
        return genericLeft;
    }

    @Override
    public GenericJoystick getJoystickRight() {
        return genericRight;
    }
}
