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
        previousData = new Hashtable<String,Object>();
        currentData  = new Hashtable<String,Object>();
        defaultValues = new Hashtable<String,DefaultValue>();
    }

    private void resetCurrentData(){
        previousData = currentData;
        currentData = new Hashtable<String,Object>();
    }

    public void newColumn(String columnName){
        csvHelper.addColumn(columnName);
    }

    public void addData(String fileName, String valueName, Object data, DefaultValue defaultValue){
        String columnName = fileName + ": " + valueName;
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