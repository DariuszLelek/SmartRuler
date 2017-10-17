package com.darodev.smartruler.utility;

/**
 * Created by Dariusz Lelek on 10/15/2017.
 * dariusz.lelek@gmail.com
 */

public enum Unit {
    INCH("inch", 5),
    CM("cm", 2);

    private String value;
    private int offset;

    Unit(String value, int offset) {
        this.value = value;
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return value;
    }
}
