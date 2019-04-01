package frc.robot.logging;

import java.util.Hashtable;
import java.util.Calendar;
import java.util.HashSet; 

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.RobotController;

import frc.robot.Utils;

public class Logger{

    public enum DefaultValue {Previous, Empty} // If in a given cycle a value isn't set, what should that value be in the next row? Empty : "", Previous : the same as the previous value
    public final static String loggingFilePath = ""; // Path to file where data is logged
    private Hashtable<String,Object> currentData; // Data of the current cycle
    private Hashtable<String,DefaultValue> defaultValues; // Data of the current default values for each column
    // TODO: make default values not reset every deplot
    private CSVHelper csvHelper;
    private Calendar calendar;
    private HashSet<String> columns; // Easy reference for what columns exist

    public Logger(){
        calendar = Calendar.getInstance();
        try{
            csvHelper = new CSVHelper(loggingFilePath);
        }catch (Exception E){
            fileNotFound();
        }
        resetCurrentData();
        defaultValues = new Hashtable<String,DefaultValue>();
        columns = Utils.arrayListToHashset(csvHelper.getColumns()); // We use csvHelper to get new columns at initialization and never again
    }

    private void fileNotFound(){
        System.out.println("FILE NOT FOUND");
        // Probably throw an error
    }

    private void resetCurrentData(){
        // initalizes current data
        currentData = new Hashtable<String,Object>();
    }

    public String getColumnName(String filename, String valueName){
        // This is simply how we name the columns. That way, everything is organized by file and differnet files can have data of the same name
        return filename + ": " + valueName;
    }

    private void addNewColumn(String columnName){
        // adds a new column to the file and records it in the column hashset
        csvHelper.addColumn(columnName);
        columns.add(columnName);
    }
    public void newDataPoint(String filename, String valueName){
        // Same as add new column but is what should generally be called directly
        addNewColumn(getColumnName(filename, valueName));
    }

    public void addData(String filename, String valueName, Object data, DefaultValue defaultValue){
        // Adds a singular piece of data to the currentData hash. Also will add the column if it is unrecognized
        String columnName = getColumnName(filename, valueName);
        if (!columnExists(columnName)) { 
            addNewColumn(columnName);
        }
        defaultValues.put(columnName, defaultValue);
        currentData.put(columnName, data);
    }
    public void logFinalField(String filename, String fieldName, Object fieldValue){
        addData(filename, fieldName, fieldValue, DefaultValue.Previous);
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
    public String getLastValueLogged(String filename, String valueName){
        // Same as get lastLoggedInColumn but should generally be called
        return getLastLoggedInColumn(getColumnName(filename,  valueName));
    }
    public double getLastLogValueDouble(String filename, String valueName){
        // Converts last log value as a string to a double, returns 0 if it isn't a number
        // Maybe TODO - should it return an error if the previous value is not a number of ""? I think it shouldn't but to consider
        String stringValue = getLastValueLogged(filename, valueName);
        try {
            return Double.valueOf(stringValue);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    public boolean getLastLogValueBool(String filename, String valueName){
        // Converts last log to a bool
        return Boolean.valueOf(getLastValueLogged(filename, valueName));
    }

    public boolean columnExists(String columnName){
        return columns.contains(columnName);
    }

    public void addToPrevious(String filename, String valueName, DefaultValue defaultValue, double incrementAmount){
        // Logs the next values as the incrementAmount + the bool value of the most recent logged data point
        double lastValue = getLastLogValueDouble(filename, valueName);
        addData(filename, valueName, lastValue + incrementAmount, defaultValue);
    }
    public void incrementPrevious(String filename, String valueName, DefaultValue defaultValue){
        addToPrevious(filename, valueName, defaultValue, 1);
    }

    private Hashtable<String,Object> defaultData(){
        // All this data will be done automatically

        Hashtable<String,Object> defaultData = new Hashtable<String,Object>();

        double year   = calendar.get(Calendar.YEAR);
        double month  = calendar.get(Calendar.MONTH);
        double day    = calendar.get(Calendar.DAY_OF_MONTH);
        double hour   = calendar.get(Calendar.HOUR_OF_DAY);
        double minute = calendar.get(Calendar.MINUTE);
        double second = calendar.get(Calendar.SECOND);
        double matchTime = Timer.getMatchTime();
        double batteryVoltage = RobotController.getBatteryVoltage();
        String prefix = "default: ";
        defaultData.put(prefix + "year",year);
        defaultData.put(prefix + "month",month);
        defaultData.put(prefix + "day",day);
        defaultData.put(prefix + "hour",hour);
        defaultData.put(prefix + "minute",minute);
        defaultData.put(prefix + "second",second);
        defaultData.put(prefix + "match time",matchTime);
        defaultData.put(prefix + "battery voltage",batteryVoltage);

        return defaultData;
    }

    private Hashtable<String,String> createFullCurrentData(){
        // Takes the defaultData() and the currentData to create the hash that will be given for the csvHelper to record
        Hashtable<String,Object> fullData = defaultData();
        for(String column : columns) {
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
        // adds a new data record to the file and resets our current data
        csvHelper.addRow(createFullCurrentData());
        resetCurrentData();
    }

}