package com.darodev.smartruler.ruler;

/**
 * Created by Dariusz Lelek on 10/16/2017.
 * dariusz.lelek@gmail.com
 */

public class RulerNotPreparedException extends Exception {
    @Override
    public String getMessage() {
        return "Ruler not prepared! Call prepareRuler in RulerBitmapProvider first.";
    }
}
