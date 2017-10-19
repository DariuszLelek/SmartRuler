package com.darodev.smartruler.utility;

/**
 * Created by Dariusz Lelek on 10/17/2017.
 * dariusz.lelek@gmail.com
 */

enum Constant {
    CM_IN_INCH(2.54f),
    CREDIT_CARD_WIDTH_CM(5.398f),
    CREDIT_CARD_WIDTH_INCH(2.125f);

    private float value;

    Constant(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }
}
