package frc.robot.logging;

import java.util.Hashtable;
import java.util.Calendar;
import java.util.HashSet;

import frc.robot.Utils;

public class Logger{

    public enum DefaultValue {Previous, Empty}
    public final static String loggingFilePath = "";
    private Hashtable<String,Object> currentData;
    private Hashtable<String,DefaultValue> defaultValues;
    private CSVHelper csvHelper;
    private Calendar calendar;
    private HashSet<String> columns;

    public Logger(){
        calendar = Calendar.getInstance();
        try{
            csvHelper = new CSVHelper(loggingFilePath);
        }catch (Exception E){
            System.out.println("FILE NOT FOUND");
        }
        resetCurrentData();
        defaultValues = new Hashtable<String,DefaultValue>();
        columns = Utils.arrayListToHashset(csvHelper.getColumns());
    }

    private void resetCurrentData(){
        currentData = new Hashtable<String,Object>();
    }

    public String getColumnName(String filename, String valueName){
        return filename + ": " + valueName;
    }

    public void addNewColumn(String columnName){
        csvHelper.addColumn(columnName);
        columns.add(columnName);
    }
    public void newDataPoint(String filename, String valueName){
        addNewColumn(getColumnName(filename, valueName));
    }

    public void addData(String filename, String valueName, Object data, DefaultValue defaultValue){
        String columnName = getColumnName(filename, valueName);
        if (!columnExists(columnName)) { 
            addNewColumn(columnName);
        }
        defaultValues.put(columnName, defaultValue);
        currentData.put(columnName, data);
    }

    public DefaultValue getDefaultValue(String column){
        if (defaultValues.containsKey(column)){
            return defaultValues.get(column);
        } else {
            return DefaultValue.Empty;
        }
    }
    
    public Hashtable<String,String> getLastLog(){
        return csvHelper.getLastRow();
    }
    public String getLastLoggedInColumn(String columnName){
        return getLastLog().get(columnName);
    }
    public String getLastValueLogged(String filename, String valueName){
        return getLastLoggedInColumn(getColumnName(filename,  valueName));
    }
    public double getLastLogValueDouble(String filename, String valueName){
        String stringValue = getLastValueLogged(filename, valueName);
        try {
            return Double.valueOf(stringValue);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    public boolean getLastLogValueBool(String filename, String valueName){
        return Boolean.valueOf(getLastValueLogged(filename, valueName));
    }

    public boolean columnExists(String columnName){
        return columns.contains(columnName);
    }

    public void addToPrevious(String filename, String valueName, DefaultValue defaultValue, double incrementAmount){
        double lastValue = getLastLogValueDouble(filename, valueName);
        addData(filename, valueName, lastValue + incrementAmount, defaultValue);
    }
    public void incrementPrevious(String filename, String valueName, DefaultValue defaultValue){
        addToPrevious(filename, valueName, defaultValue, 1);
    }

    private Hashtable<String,Object> defaultData(){
        Hashtable<String,Object> defaultData = new Hashtable<String,Object>();

        double year   = calendar.get(Calendar.YEAR);
        double month  = calendar.get(Calendar.MONTH);
        double day    = calendar.get(Calendar.DAY_OF_MONTH);
        double hour   = calendar.get(Calendar.HOUR_OF_DAY);
        double minute = calendar.get(Calendar.MINUTE);
        double second = calendar.get(Calendar.SECOND);
        defaultData.put("year",year);
        defaultData.put("month",month);
        defaultData.put("day",day);
        defaultData.put("hour",hour);
        defaultData.put("minute",minute);
        defaultData.put("second",second);

        return defaultData;
    }

    private Hashtable<String,Object> createFullCurrentData(){
        Hashtable<String,Object> fullData = new Hashtable<String,Object>();
        for(String column : columns) {
            if (currentData.containsKey(column)) {
                fullData.put(column, currentData.get(column));
            } else if (getDefaultValue(column) == DefaultValue.Previous) {
                String data = getLastLoggedInColumn(column);
                fullData.put(column, data);
            }
        }
        return fullData;
    }

    public void logData(){
        csvHelper.addRow(createFullCurrentData());
        resetCurrentData();
    }

}