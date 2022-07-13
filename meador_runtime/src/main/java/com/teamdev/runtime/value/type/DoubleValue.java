package com.teamdev.runtime.value.type;

public class DoubleValue implements Value {

    private final double value;

    public DoubleValue(double value) {
        this.value = value;
    }

    @Override
    public void acceptVisitor(ValueVisitor visitor) {
        visitor.visit(this);
    }

    public double value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DoubleValue)) return false;

        DoubleValue that = (DoubleValue) o;

        return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(value);
        return (int) (temp ^ (temp >>> 32));
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
