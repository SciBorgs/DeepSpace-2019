package frc.robot.logging;

import java.util.Hashtable;
import java.util.Calendar;

public class Logger{

    public enum DefaultValue {Previous, Empty}
    public final static String loggingFilePath = "";
    private Hashtable<String,Object> previousData;
    private Hashtable<String,Object> currentData;
    private Hashtable<String,DefaultValue> defaultValues;
    private CSVHelper csvHelper;
    private Calendar calendar;

    public Logger(){
        calendar = Calendar.getInstance();
        try{
            csvHelper = new CSVHelper(loggingFilePath);
        }catch (Exception E){
            System.out.println("FILE NOT FOUND");
        }
        previousData  = new Hashtable<String,Object>();
        currentData   = new Hashtable<String,Object>();
        defaultValues = new Hashtable<String,DefaultValue>();
    }

    private void resetCurrentData(){
        previousData = currentData;
        currentData = new Hashtable<String,Object>();
    }

    public String getColumnName(String filename, String valueName){
        return filename + ": " + valueName;
    }

    public void newDataPoint(String filename, String valueName, Object firstValue){
        String columnName = getColumnName(filename, valueName);
        csvHelper.addColumn(columnName);
        currentData.put(columnName, firstValue);
    }

    public void addData(String filename, String valueName, Object data, DefaultValue defaultValue){
        String columnName = getColumnName(filename, valueName);
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
    public String getLastLogValue(String filename, String valueName){
        String columnName = getColumnName(filename,  valueName);
        return getLastLog().get(columnName);
    }
    public double getLastLogValueDouble(String filename, String valueName){
        return Double.valueOf(getLastLogValue(filename, valueName));
    }
    public boolean getLastLogValueBool(String filename, String valueName){
        return Boolean.valueOf(getLastLogValue(filename, valueName));
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
        for(String column : csvHelper.getColumns()) {
            if (currentData.containsKey(column)) {
                fullData.put(column, currentData.get(column));
            } else if (getDefaultValue(column) == DefaultValue.Previous) {
                fullData.put(column, currentData);
            }
        }
        return fullData;
    }

    public void logData(){
        csvHelper.addRow(createFullCurrentData());
        resetCurrentData();
    }

}