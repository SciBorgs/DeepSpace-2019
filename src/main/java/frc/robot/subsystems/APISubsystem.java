package frc.robot.subsystems;
import frc.robot.Robot;

import edu.wpi.first.wpilibj.command.Subsystem;
import java.util.ArrayList;
import edu.wpi.first.wpilibj.DigitalOutput;
/**
 * Add your docs here.
 */
public class APISubsystem extends Subsystem{ 
  String x;
  int y;
  ArrayList<Integer> dIOArray = new ArrayList<Integer>();
 
  public APISubsystem(){   
  }
  public void convertNumber(int number){
    x = Integer.toBinaryString(number);
    y = Integer.parseInt(x);
    System.out.println(y);
    for (int i = 0; i <= 5; i++){
      dIOArray.add(y % 10);
      y = y / 10;
    }       
    for (int i = 0; i <= 5; i++){
      int z  = (int)dIOArray.get(i);
      if (z == 1){
        new DigitalOutput(i).set(true);
        System.out.println(z);
        System.out.print("is true");
      }
      else {
        new DigitalOutput(i).set(false);
        System.out.println(z);
        System.out.print("is false");
      }}
    }
      @Override
      protected void initDefaultCommand() {
          // LITTERALLY DIE
      }
    }