package frc.robot.logging;

import java.util.Hashtable;

public class Logger{

    public enum DefaultValue {Previous, Empty}
    private Hashtable<String,Object> previousData;
    private Hashtable<String,Object> currentData;
    private Hashtable<String,DefaultValue> defaultValues;

    public Logger(){
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

    public void addData(String column, Object data, DefaultValue defaultValue){
        defaultValues.put(column, defaultValue);
        currentData.put(column, data);
    }

    public DefaultValue getDefaultValue(String column){
        if (defaultValues.containsKey(column)){
            return defaultValues.get(column);
        } else {
            return DefaultValue.Empty;
        }
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