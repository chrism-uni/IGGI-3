package utils;

/**
 * Created by jliu on 13/05/16.
 */
public class MutableDouble {

    private double value;

    public MutableDouble(double value) {
        this.value = value;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int intValue() {
        return (int) this.value;
    }
}