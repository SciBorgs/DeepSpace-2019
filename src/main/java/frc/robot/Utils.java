package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

import edu.wpi.first.wpilibj.DigitalOutput;

import java.util.*;
import java.util.Collections;

// FILE HAS NOT BEEN CLEANED UP //
public class Utils{

    public static double METERS_TO_INCHES = 39.37;

    public static double metersToInches(double meters){
        return meters * METERS_TO_INCHES;
    }
    public static double inchesToMeters(double inches){
        return inches / METERS_TO_INCHES;
    }

    public static double last(ArrayList<Double> arr) {
        return arr.get(arr.size() - 1);
    }

    public static int signOf(double value){
        if (value == 0){
            return 0;
        } else {
            return (int) (Math.abs(value) / value);
        }
    }

    public static void trimIf(ArrayList<Double> arr, int maxSize) {
        // Trims an array down to a max size, starting from the start
        while (maxSize < arr.size())
            arr.remove(0);
    }

    public static boolean inRange(double n1, double n2, double error){
        return Math.abs(n1 - n2) < error;
    }

    public static boolean inRange(ArrayList<Double> arr, double error){
        return inRange(Collections.max(arr), Collections.min(arr), error);
    }

    public static double averageRange(ArrayList<Double> arr) {
        return (last(arr) - arr.get(0)) / arr.size();
    }

    public static void trimAdd(ArrayList<Double> arr, double val, int maxSize) {
        // Adds a value to the array and then trims it to a max size
        arr.add(val);
        trimIf(arr, maxSize);
    }

    public static double limitOutput(double output, double max){
        if (output > max)
            return max;
        else if (output < - max)
            return -max;
        else
            return output;
    }

    public static void setTalon(TalonSRX talon, double speed){
        talon.set(ControlMode.PercentOutput, speed);
    }

    public static int boolToInt(boolean b){
        if (b) {
            return 1;
        } else {
            return 0;
        }
    }

    public static HashSet<String> arrayListToHashset(ArrayList<String> values){
        HashSet<String> end = new HashSet<String>();
        for (String val : values){
            end.add(val);
        }
        return end;
    }

    public static Hashtable<String,String> hatshtableDataToString(Hashtable<String,Object> hashTable){
        Hashtable<String,String> end = new Hashtable<String,String>();
        for (String key : hashTable.keySet()){
            end.put(key,hashTable.get(key).toString());
        }
        return end;
    }

    // Generic function to merge 2 arrays of same type in Java
    public static<T> T[] combineArray(T[] arr1, T[] arr2) {
	    T[] result = Arrays.copyOf(arr1, arr1.length + arr2.length);
	    System.arraycopy(arr2, 0, result, arr1.length, arr2.length);
	    return result;
    }

    public static Value oppositeDoubleSolenoidValue(Value val){
        switch (val) {
            case kForward:
                return Value.kReverse;
            case kReverse:
                return Value.kForward;
        }
        return Value.kOff;
    }

    public static void toggleDoubleSolenoid(DoubleSolenoid doubleSolenoid){
        doubleSolenoid.set(oppositeDoubleSolenoidValue(doubleSolenoid.get()));
    }

    public static boolean oppositeDigitalOutput(boolean bool){
        return !bool;
    }

    public static void toggleDigitalOutput(DigitalOutput digitalOutput){
        oppositeDigitalOutput(digitalOutput.get());
    }

    public static DoubleSolenoid newDoubleSolenoid(int[] ports){
        return new DoubleSolenoid(ports[0], ports[1]);
    }
    public static DoubleSolenoid newDoubleSolenoid(int pdpPort, int[] ports){
        return new DoubleSolenoid(pdpPort, ports[0], ports[1]);
    }
}