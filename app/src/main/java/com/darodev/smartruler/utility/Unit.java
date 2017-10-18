package com.darodev.smartruler.utility;

import static android.R.attr.offset;

/**
 * Created by Dariusz Lelek on 10/15/2017.
 * dariusz.lelek@gmail.com
 */

public enum Unit {
    INCH("inch"),
    CM("cm");

    private String value;

    Unit(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return value;
    }
}
