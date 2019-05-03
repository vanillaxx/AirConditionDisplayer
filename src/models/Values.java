package models;

/**
 * Stores an information about particular value of parameter measured at particular time
 */
public class Values {
    String date;
    double value;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
