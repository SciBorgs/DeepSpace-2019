package frc.robot.commands;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.shuffleboard.*;
import frc.robot.controlscheme.ControlButton;
import frc.robot.subsystems.LiftSubsystem;
import frc.robot.subsystems.PneumaticsSubsystem;

import java.util.ArrayList;

/**
 * A Command that updates the Shuffleboard GUI. It's not done yet, we need to test it with the real robot a bit and add
 * some features that Chris wanted.
 * <p>
 * Originally made by Team Alpha: Tobias, Zawad, Matthew, Jack, Swanand, Zach, and Alejandro
 */
public class ShuffleboardCommand extends Command {
    // shuffleboard
    private final ShuffleboardTab driverStationTab;
    private final SimpleWidget timer;
    private final ShuffleboardLayout voltageList;
    private final SimpleWidget totalVoltage;
    private final SimpleWidget isVoltageOk;
    private final SimpleWidget totalMotorCurrent;
    private final SimpleWidget liftTemperature;
    private final SimpleWidget liftPosition;
    private final SimpleWidget airPressure;
    private final ArrayList<SimpleWidget> cargoList;
    private final SimpleWidget cargoSelectionText;

    // dependencies
    private final PowerDistributionPanel pdp;
    private final LiftSubsystem liftSubsystem;
    private final PneumaticsSubsystem pneumaticsSubsystem;
    private final ControlButton buttonLeft;
    private final ControlButton buttonRight;
    private final ControlButton buttonToggle;
    private final CANSparkMax[] canSparkMaxs;
    private final TalonSRX[] talonSRXs;
    private final CANSparkMax cascadeSpark;

    // needed for cargo selection
    private int cargoSelection;

    public ShuffleboardCommand(PowerDistributionPanel pdp, LiftSubsystem liftSubsystem, PneumaticsSubsystem pneumaticsSubsystem, CANSparkMax[] canSparkMaxs, TalonSRX[] talonSRXs, CANSparkMax cascadeSpark, ControlButton buttonLeft, ControlButton buttonRight, ControlButton buttonToggle) {
        this.pdp = pdp;
        this.liftSubsystem = liftSubsystem;
        this.pneumaticsSubsystem = pneumaticsSubsystem;
        this.canSparkMaxs = canSparkMaxs;
        this.talonSRXs = talonSRXs;
        this.cascadeSpark = cascadeSpark;
        this.buttonLeft = buttonLeft;
        this.buttonRight = buttonRight;
        this.buttonToggle = buttonToggle;

        driverStationTab = Shuffleboard.getTab("Driver Station");

        timer = driverStationTab.add("Match Timer", "-1 remaining").withWidget("Text View").withPosition(2, 0).withSize(1, 1);

        voltageList = driverStationTab.getLayout("Voltage", "List Layout").withPosition(8, 0).withSize(1, 2);
        isVoltageOk = voltageList.add("Above 8 volts?", false).withWidget("Boolean Box").withSize(1, 1);
        totalVoltage = voltageList.add("Current voltage", "-1 Volts").withWidget("Text View").withSize(1, 1);

        totalMotorCurrent = driverStationTab.add("Motor Current", "-1 Amps").withWidget("Text View").withPosition(9, 0).withSize(1, 1);

        liftPosition = driverStationTab.add("Lift Position", "Ground").withWidget("Text View").withPosition(9, 2).withSize(1, 1);
        liftTemperature = driverStationTab.add("Lift Temp", "-1 C\u00B0").withWidget("Text View").withPosition(8, 2).withSize(1, 1);

        airPressure = driverStationTab.add("Air Pressure", "-1 PSI").withWidget("Text View").withPosition(2, 2).withSize(1, 1);

        NetworkTableInstance.getDefault().getEntry("/CameraPublisher/Limelight/streams").setStringArray(new String[]{"mjpeg:http://10.11.55.11:5800/?action=stream"});

        cargoList = new ArrayList<>();
        cargoSelection = 0;
        cargoSelectionText = driverStationTab.add("Selection", "-1").withWidget("Text View").withPosition(0, 4).withSize(1, 1);
        setUpCargoList();

        setRunWhenDisabled(true);
    }

    @Override
    protected void initialize() {
    }

    @Override
    protected void execute() {
        updateTimer();
        updatePowerOutput();
        updateMotorCurrent();
        updateCascadeMotorTemp();
        updateLiftPosition();
        updateAirPressure();
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

    @Override
    protected void end() {
    }

    @Override
    protected void interrupted() {
        end();
    }

    private boolean updateTimer() {
        return timer.getEntry().setString(DriverStation.getInstance().getMatchTime() + " remaining");
    }

    private boolean updatePowerOutput() {
        double volts = pdp.getVoltage();
        totalVoltage.getEntry().setString(volts + " Volts");
        if (volts > 8) {
            return isVoltageOk.getEntry().setBoolean(true);
        } else {
            return isVoltageOk.getEntry().setBoolean(false);
        }
    }

    private boolean updateMotorCurrent() {
        double motorCurrent = 0;
        for (CANSparkMax canSparkMax : canSparkMaxs) {
            motorCurrent += canSparkMax.getOutputCurrent();
        }

        for (TalonSRX talon : talonSRXs) {
            motorCurrent += talon.getOutputCurrent();
        }

        return totalMotorCurrent.getEntry().setString(motorCurrent + " Amps");
    }

    private boolean updateCascadeMotorTemp() {
        double temperature = cascadeSpark.getMotorTemperature();
        return liftTemperature.getEntry().setString(temperature + " C\u00B0");
    }

    private boolean updateLiftPosition() {
        return liftPosition.getEntry().setString(liftSubsystem.getTarget().toString());
    }

    private boolean updateAirPressure() {
        return airPressure.getEntry().setString(pneumaticsSubsystem.getPressure() + " PSI");
    }

    private void setUpCargoList() {
        cargoList.add(getBaseCargoBox(cargoList.size(), 0, 0));
        cargoList.add(getBaseCargoBox(cargoList.size(), 1, 0));
        cargoList.add(getBaseCargoBox(cargoList.size(), 0, 1));
        cargoList.add(getBaseCargoBox(cargoList.size(), 1, 1));
        cargoList.add(getBaseCargoBox(cargoList.size(), 0, 2));
        cargoList.add(getBaseCargoBox(cargoList.size(), 1, 2));
        cargoList.add(getBaseCargoBox(cargoList.size(), 0, 3));
        cargoList.add(getBaseCargoBox(cargoList.size(), 1, 3));

        buttonToggle.whenPressed(() -> cargoList.get(cargoSelection).getEntry().setBoolean(!cargoList.get(cargoSelection).getEntry().getBoolean(false)));

        buttonLeft.whenPressed(this::movePointerLeft);
        buttonRight.whenPressed(this::movePointerRight);
    }

    private SimpleWidget getBaseCargoBox(int number, int x, int y) {
        return driverStationTab.add(String.valueOf(number), false).withWidget("Boolean Box").withPosition(x, y);
    }

    private void changePointer(int change) {
        cargoSelection += change;
        if (cargoSelection < 0) {
            cargoSelection = 0;
        } else if (cargoSelection > 7) {
            cargoSelection = 7;
        }
        cargoSelectionText.getEntry().setString(String.valueOf(cargoSelection));
    }

    private void movePointerLeft() {
        changePointer(-1);
    }

    private void movePointerRight() {
        changePointer(1);
    }
}
