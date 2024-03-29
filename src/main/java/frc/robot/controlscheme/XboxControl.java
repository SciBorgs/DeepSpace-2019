package frc.robot.controlscheme;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.commands.ShuffleboardCommand;
import frc.robot.helpers.Lineup;
import frc.robot.helpers.PID;
import frc.robot.subsystems.*;

/**
 * A control scheme for {@link XboxController}
 */
public class XboxControl implements ControlScheme {
    private static final int XBOX = 2;

    private final XboxController xboxController;

    private final GenericJoystick genericLeft;
    private final GenericJoystick genericRight;

    public XboxControl() {
        xboxController = new XboxController(XBOX);
        genericLeft = new GenericJoystick() {
            @Override
            public double getX() {
                return xboxController.getX(GenericHID.Hand.kLeft);
            }

            @Override
            public double getY() {
                return xboxController.getY(GenericHID.Hand.kLeft);
            }
        };

        genericRight = new GenericJoystick() {
            @Override
            public double getX() {
                return xboxController.getX(GenericHID.Hand.kRight);
            }

            @Override
            public double getY() {
                return xboxController.getY(GenericHID.Hand.kRight);
            }
        };
    }

    /*
    @Override
    public void mapBindings(Lineup lineup, LiftSubsystem liftSubsystem, IntakeSubsystem intakeSubsystem, ZLiftSubsystem zLiftSubsystem, TankDriveSubsystem tankDriveSubsystem, LimelightSubsystem limelightSubsystem) {
    }
    */

    @Override
    public ShuffleboardCommand getShuffleboardCommand(PowerDistributionPanel pdp, LiftSubsystem liftSubsystem, PneumaticsSubsystem pneumaticsSubsystem, CANSparkMax cascadeSpark, PID cargoPID, PID drivePID, double maxOmegaGoal, PID liftArmPID, PID liftLiftPID, PID lineupPID) {
        return new ShuffleboardCommand(pdp, liftSubsystem, pneumaticsSubsystem, cascadeSpark, getButton(XboxControlButton.Button.BUMPER_LEFT), getButton(XboxControlButton.Button.BUMPER_RIGHT), getButton(XboxControlButton.Button.A), cargoPID, drivePID, maxOmegaGoal, liftArmPID, liftLiftPID, lineupPID);
    }

    @Override
    public GenericJoystick getJoystickLeft() {
        return genericLeft;
    }

    @Override
    public GenericJoystick getJoystickRight() {
        return genericRight;
    }

    private XboxControlButton getButton(XboxControlButton.Button button) {
        return new XboxControlButton(xboxController, button);
    }
}
