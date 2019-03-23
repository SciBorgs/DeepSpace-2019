package frc.robot.controlscheme;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import frc.robot.commands.ShuffleboardCommand;
import frc.robot.helpers.Lineup;
import frc.robot.helpers.PID;
import frc.robot.subsystems.*;

/**
 * An interface to hold a preconfigured control scheme, or keybindings for the robot.
 * <p>
 * It is designed to be easy to swap between any different input devices, as long as it's output can be represented as
 * two joystick values.
 */
public interface ControlScheme {
    /**
     * Assigns commands to the buttons on the input device.
     *
     * @param lineup             instance of the Lineup class used by the robot
     * @param liftSubsystem      instance of the LiftSubsystem used by the robot
     * @param intakeSubsystem    instance of the IntakeSubsystem used by the robot
     * @param zLiftSubsystem     instance of the ZLiftSubsystem used by the robot
     * @param tankDriveSubsystem instance of the TankDriveSubsystem used by the robot
     * @param limelightSubsystem instance of the LimelightSubsystem used by the robot
     */
    //void mapBindings(Lineup lineup, LiftSubsystem liftSubsystem, IntakeSubsystem intakeSubsystem, ZLiftSubsystem zLiftSubsystem, TankDriveSubsystem tankDriveSubsystem, LimelightSubsystem limelightSubsystem);

    /**
     * Instantiates a ShuffleboardCommand that works with the control scheme
     *
     * @param pdp                 needed for voltage display
     * @param liftSubsystem       needed for current lift level
     * @param pneumaticsSubsystem needed for air pressure
     * @param canSparkMaxs        needed for total motor current
     * @param talonSRXs           needed for total motor current
     * @param cascadeSpark        needed for cascade temperature
     * @return a ShuffleboardCommand that works with the control scheme
     */
    ShuffleboardCommand getShuffleboardCommand(PowerDistributionPanel pdp, LiftSubsystem liftSubsystem, PneumaticsSubsystem pneumaticsSubsystem, CANSparkMax[] canSparkMaxs, TalonSRX[] talonSRXs, CANSparkMax cascadeSpark, PID cargoPID);

    /**
     * @return the input values of the Control Scheme, represented as a left joystick
     */
    GenericJoystick getJoystickLeft();

    /**
     * @return the input values of the Control Scheme, represented as a right joystick
     */
    GenericJoystick getJoystickRight();
}
