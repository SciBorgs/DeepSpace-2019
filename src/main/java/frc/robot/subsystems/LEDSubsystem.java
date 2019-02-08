/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import java.util.ArrayList;
import edu.wpi.first.wpilibj.DigitalOutput;



public class LEDSubsystem extends Subsystem{ 
  public static enum LEDColor{
    RED, GREEN, PINK, PURPLE, ORANGE, BLUE;
  }
  String x;
  int y;
  ArrayList<Integer> dIOArray = new ArrayList<Integer>();
 
  public LEDSubsystem(){       
  }

  public void convertNumber(int number){
    x = Integer.toBinaryString(number);
    y = Integer.parseInt(x);
    System.out.println(y);
    for (int i = 0; i <= 3; i++){
      dIOArray.add(y % 10);
      y = y / 10;
    }        

    for (int i = 0; i <= 3; i++){
      int z  = (int)dIOArray.get(i);
      if (z == 1){
        new DigitalOutput(i).set(true);
        System.out.println(i + " my number DOI is " + z + " is true");
      }else if (z == 0){
        new DigitalOutput(i).set(false);
        System.out.println(i + " my number DOI is " + z + " is false");
      }

    }
    

  }

  public void setColor(LEDColor color){
    switch (color) {
      case RED: // (0, 0, 1) = 1
        convertNumber(1);
      case PINK: // (1, 1, 1) = 7
        convertNumber(7);
      case ORANGE: // (1, 0, 0) = 4
        convertNumber(4);
      case GREEN: // (1, 1, 0) = 6
        convertNumber(6);
      case PURPLE: // (0, 1, 1) = 3
        convertNumber(3);
      case BLUE: // (0, 1, 0) = 2
        convertNumber(2);
    }
  }
  
  
  public boolean ifCargo(){
    return true;
  }
  
  @Override
  public void initDefaultCommand() {

  }
}
 