package com.darodev.smartruler.ruler;

/**
 * Created by Dariusz Lelek on 10/19/2017.
 * dariusz.lelek@gmail.com
 */

public enum MeasureOrigin {
    RULER_SCREEN(4),
    CALIBRATION_SCREEN(1);

    private final int divider;

    MeasureOrigin(int divider) {
        this.divider = divider;
    }

    public int getDivider() {
        return divider;
    }
}
