package com.darodev.smartruler.utility;

/**
 * Created by Dariusz Lelek on 10/15/2017.
 * dariusz.lelek@gmail.com
 */

public enum Unit {
    INCH("inch", 8),
    CM("cm", 10);

    private String value;
    private int sections;

    Unit(String value, int sections) {
        this.value = value;
        this.sections = sections;
    }

    public int getSections() {
        return sections;
    }

    @Override
    public String toString() {
        return value;
    }
}
