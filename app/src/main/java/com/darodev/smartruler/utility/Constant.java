package com.darodev.smartruler.utility;

/**
 * Created by Dariusz Lelek on 10/17/2017.
 * dariusz.lelek@gmail.com
 */

public enum Constant {
    CM_IN_INCH(2.54f),
    CREDIT_CARD_WIDTH_CM(53.98f),
    CREDIT_CARD_HEIGHT_CM(85.60f);

    private float value;

    Constant(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }
}
