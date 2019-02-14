package frc.robot.subsystems;
import frc.robot.Robot;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.DigitalOutput;
/**
 * Add your docs here.
 */
public class LEDSubsystem extends Subsystem{ 
 
  public LEDSubsystem(){   
  }

  public void convertNumber(int number){
    char[] binaryDigits = Integer.toBinaryString(number).toCharArray();
    int i = 0;
    for (char digitChar : binaryDigits){
      int digit = Integer.valueOf(String.valueOf(digitChar));
      System.out.println(digit);
      new DigitalOutput(i).set(digit == 1);
      i++;
    }
  }

  @Override
  protected void initDefaultCommand() {
      // LITTERALLY DIE
  }
}