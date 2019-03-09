package frc.robot;

import frc.robot.subsystems.*;
import frc.robot.subsystems.IntakeSubsystem.HatchDepositControl;
import frc.robot.subsystems.IntakeSubsystem.IntakeMode;
import frc.robot.commands.*;
import frc.robot.helpers.*;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;

import java.util.*;

public class Robot extends TimedRobot {
    private String m_autoSelected;
    public static IntakeSubsystem intakeSubsystem = new IntakeSubsystem();
    public static DriveSubsystem driveSubsystem = new DriveSubsystem();
	public static PositioningSubsystem positioningSubsystem = new PositioningSubsystem();
    public static LiftSubsystem liftSubsystem = new LiftSubsystem();
    public static ZLiftSubsystem zLiftSubsystem = new ZLiftSubsystem();
    public static OI oi = new OI();
    private final SendableChooser<String> m_chooser = new SendableChooser<>();
    public static LimelightSubsystem limelightSubsystem = new LimelightSubsystem();
    public static CargoFollowing cargoFollowing = new CargoFollowing();
    public static GearShiftSubsystem gearShiftSubsystem = new GearShiftSubsystem();
    public static PneumaticsSubsystem pneumaticsSubsystem = new PneumaticsSubsystem();
    public static Lineup lineup = new Lineup();

    public void robotInit() {
        positioningSubsystem.getPigeon().getPigeon().setYaw(0., 5);
        System.out.println("roboinited");
        positioningSubsystem.updatePositionTank();

           /* STARTS THE LIDAR     
        try {
            System.out.println("LIDAR status: starting");
            boolean started = LidarServer.getInstance().start();
            System.out.println("LIDAR status" + (started ? "started" : "failed to start"));
        } catch (Throwable t) {
            System.out.println("LIDAR status: crashed -" + t);
            t.printStackTrace();
            throw t;
        }*/

    }
 
    public void robotPeriodic() {
        positioningSubsystem.updatePositionTank();
        //positioningSubsystem.printPosition();
        //retroreflectiveSubsystem.modeToRetroreflectiveByLimitSwitch(); 
        //gearShiftSubsystem.shiftGear(); 	
        /*intakeSubsystem.secureCargo();
        if (intakeSubsystem.holdingGamePiece()){
            RetroreflectiveDetection.modeToRetroreflective();
        } else {
            cargoFollowing.modeToCargo();
        }*/
    }
        
    public void autonomousInit() {
    }

    public void autonomousPeriodic() {
        //SmartDashboard.putNumber("Pressure Sensor PSI", pneumaticsSubsystem.getPressure());
    }
    
    @Override
    public void teleopInit() {
        new TankDriveCommand().start();
    }

    public void teleopPeriodic() {
        //SmartDashboard.putNumber("Pressure Sensor PSI", pneumaticsSubsystem.getPressure());
        Scheduler.getInstance().run();
    }

    public void testPeriodic() {
    }

    public void disabledInit() {
        zLiftSubsystem.reset();
    }
}
