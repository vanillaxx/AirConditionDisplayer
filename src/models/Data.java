package models;

import java.util.Map;

/**
 * Stores an information about data of particular sensor
 */
public class Data {
    private String key;
    private Values[] values;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Values[] getValues() {
        return values;
    }

    public void setValues(Values[] values) {
        this.values = values;
    }
}
