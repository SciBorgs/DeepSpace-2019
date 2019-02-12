package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.AnalogInput;
import frc.robot.PortMap;

public class PneumaticsSubsystem extends Subsystem {
  private AnalogInput pressureSensor;
  private final double NORMALIZED_SUPPLY_VOLTAGE = 5.0;
  
  @Override
  public void initDefaultCommand() {
   
  }


  public PneumaticsSubsystem() {
    this.pressureSensor = new AnalogInput(PortMap.PRESSURE_SENSOR);
  }

  public double getPressure() {
    return 250.0 * pressureSensor.getVoltage() / NORMALIZED_SUPPLY_VOLTAGE - 15.0;
  }

  public double getRawVoltage() {
    return pressureSensor.getVoltage();
  }
}
