package frc.robot.logging;

import java.util.Hashtable;
import java.util.Calendar;
import java.util.HashSet;
import java.util.ArrayList;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.RobotController;
import java.io.*;

import frc.robot.Utils;
import frc.robot.helpers.PID;

// FILE HAS NOT BEEN CLEANED UP //
public class Logger{

    public enum DefaultValue {Previous, Empty} // If in a given cycle a value isn't set, what should that value be in the next row? Empty : "", Previous : the same as the previous value
    public enum CommandStatus {Initializing, Executing, Ending, Interrupted}
    public final static String loggingFilePath = "/home/lvuser/Logs/MainLog.csv"; // Path to file where data is logged
    private Hashtable<String,Object> currentData; // Data of the current cycle
    private Hashtable<String,DefaultValue> defaultValues; // Data of the current default values for each column
    // TODO: make default values not reset every deploy
    private CSVHelper csvHelper;
    private Calendar calendar;
    private boolean loggingDisabled = true;

    // Universal naming conventions
    public String commandStatusName = "status";

    public Logger(){
        calendar = Calendar.getInstance();
        try{                
            csvHelper = new CSVHelper(loggingFilePath);
        }catch (Exception E){
            try{
                (new File(loggingFilePath)).createNewFile();
                csvHelper = new CSVHelper(loggingFilePath);
            }catch (Exception E2){
                fileNotFound();
            }
        }
        resetCurrentData();
        defaultValues = new Hashtable<String,DefaultValue>();
    }

    private void fileNotFound(){
        System.out.println("FILE NOT FOUND (logger)");
        // Probably throw an error
    }

    private void resetCurrentData(){
        // initalizes current data
        currentData = new Hashtable<String,Object>();
    }

    public String getColumnName(String fileName, String valueName){
        // This is simply how we name the columns. That way, everything is organized by file and differnet files can have data of the same name
        return fileName + ": " + valueName;
    }

    public ArrayList<String> getColumns(){
        return csvHelper.getTopics();
    }

    private void addNewColumn(String columnName){
        // adds a new column to the file and records it in the column hashset
        csvHelper.addTopic(columnName);
    }
    public void newDataPoint(String fileName, String valueName){
        if (loggingDisabled){return;}
        // Same as add new column but is what should generally be called directly
        addNewColumn(getColumnName(fileName, valueName));
    }

    public void addData(String fileName, String valueName, Object data, DefaultValue defaultValue){
        // Adds a singular piece of data to the currentData hash. Also will add the column if it is unrecognized
        if (loggingDisabled){return;}
        String columnName = getColumnName(fileName, valueName);
        if (!columnExists(columnName)) { 
            System.out.println("adding column " + columnName);
            addNewColumn(columnName);
        }
        defaultValues.put(columnName, defaultValue);
        currentData.put(columnName, data);
    }
    public void logFinalField(String fileName, String fieldName, Object fieldValue){
        addData(fileName, fieldName, fieldValue, DefaultValue.Previous);
    }
    public void logFinalPIDConstants(String fileName, String pidName, PID pid){
        addData(fileName, pidName, "(" + pid.getP() + ", " + pid.getI() + ", " + pid.getD() + ")", DefaultValue.Previous);
    }
    public void logCommandStatus(String fileName, CommandStatus commandStatus){
        String stringStatus = "";
        switch (commandStatus) {
            case Initializing:
                stringStatus = "initializing";
                break;
            case Executing:
                stringStatus = "executing";
                break;
            case Ending:
                stringStatus = "edning";
                break;
            case Interrupted:
                stringStatus = "interrupted";
                break;
        }
        addData(fileName, commandStatusName, stringStatus, DefaultValue.Empty);
    }

    public DefaultValue getDefaultValue(String column){
        if (defaultValues.containsKey(column)){
            return defaultValues.get(column);
        } else {
            return DefaultValue.Empty;
        }
    }
    
    public Hashtable<String,String> getLastLog(){
        // Gets the most recent log (IE: row) in the logging file
        return csvHelper.getLastRow();
    }
    private String getLastLoggedInColumn(String columnName){
        // Given a column name, it gives the most recent value of that column as a string
        return getLastLog().get(columnName);
    }
    public String getLastValueLogged(String fileName, String valueName){
        // Same as get lastLoggedInColumn but should generally be called
        return getLastLoggedInColumn(getColumnName(fileName,  valueName));
    }
    public double getLastLogValueDouble(String fileName, String valueName){
        // Converts last log value as a string to a double, returns 0 if it isn't a number
        // Maybe TODO - should it return an error if the previous value is not a number of ""? I think it shouldn't but to consider
        String stringValue = getLastValueLogged(fileName, valueName);
        try {
            return Double.valueOf(stringValue);
        } catch (Exception e) {
            return 0;
        }
    }
    public boolean getLastLogValueBool(String fileName, String valueName){
        // Converts last log to a bool
        return Boolean.valueOf(getLastValueLogged(fileName, valueName));
    }

    public boolean columnExists(String columnName){
        return getColumns().contains(columnName);
    }

    public void addToPrevious(String fileName, String valueName, DefaultValue defaultValue, double incrementAmount){
        // Logs the next values as the incrementAmount + the bool value of the most recent logged data point
        double lastValue = getLastLogValueDouble(fileName, valueName);
        addData(fileName, valueName, lastValue + incrementAmount, defaultValue);
    }
    public void incrementPrevious(String fileName, String valueName, DefaultValue defaultValue){
        addToPrevious(fileName, valueName, defaultValue, 1);
    }

    private void addDefaultData(){
        // All this data will be done automatically
        double year   = calendar.get(Calendar.YEAR);
        double month  = calendar.get(Calendar.MONTH);
        double day    = calendar.get(Calendar.DAY_OF_MONTH);
        double hour   = calendar.get(Calendar.HOUR_OF_DAY);
        double minute = calendar.get(Calendar.MINUTE);
        double second = calendar.get(Calendar.SECOND);
        double matchTime = Timer.getMatchTime();
        double batteryVoltage = RobotController.getBatteryVoltage();
        String prefix = "default";
        addData(prefix, "year",year, DefaultValue.Previous);
        addData(prefix, "month",month, DefaultValue.Previous);
        addData(prefix, "day",day, DefaultValue.Previous);
        addData(prefix, "hour",hour, DefaultValue.Previous);
        addData(prefix, "minute",minute, DefaultValue.Previous);
        addData(prefix, "second",second, DefaultValue.Previous);
        addData(prefix, "match time",matchTime, DefaultValue.Previous);
        addData(prefix, "battery voltage",batteryVoltage, DefaultValue.Previous);
    }

    private Hashtable<String,String> createFullCurrentData(){
        // Takes the defaultData() and the currentData to create the hash that will be given for the csvHelper to record
        addDefaultData();
        Hashtable<String,Object> fullData = new Hashtable<>();
        for(String column : getColumns()) {
            if (currentData.containsKey(column)) {
                fullData.put(column, currentData.get(column));
            } else if (getDefaultValue(column) == DefaultValue.Previous) {
                String data = getLastLoggedInColumn(column);
                fullData.put(column, data);
            }
        }
        return Utils.hatshtableDataToString(fullData);
    }

    public void logData(){
        if (loggingDisabled){return;}
        System.out.println("attempting to log data...");
        // adds a new data record to the file and resets our current data
        csvHelper.addRow(createFullCurrentData());
        resetCurrentData();
    }

}
