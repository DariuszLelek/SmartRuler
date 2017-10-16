package com.darodev.smartruler.ruler;

import com.darodev.smartruler.utility.Unit;

import static com.darodev.smartruler.utility.Unit.CM;
import static com.darodev.smartruler.utility.Unit.INCH;

/**
 * Created by Dariusz Lelek on 10/16/2017.
 * dariusz.lelek@gmail.com
 */

public enum RulerType {
    CM_SCREEN(true, CM, Ruler.SCREEN),
    CM_SCREEN_R(false, CM, Ruler.SCREEN),
    CM_PHONE_EDGE_L(true, CM, Ruler.LEFT_PHONE_EDGE),
    CM_PHONE_EDGE_R(false, CM, Ruler.RIGHT_PHONE_EDGE),
    INCH_SCREEN(true, INCH, Ruler.SCREEN),
    INCH_SCREEN_R(false, INCH, Ruler.SCREEN),
    INCH_PHONE_EDGE_L(true, INCH, Ruler.LEFT_PHONE_EDGE),
    INCH_PHONE_EDGE_R(false, INCH, Ruler.RIGHT_PHONE_EDGE);

    private boolean fromLeft;
    private Unit unit;
    private Ruler startPoint;

    RulerType(boolean fromLeft, Unit unit, Ruler startPoint) {
        this.fromLeft = fromLeft;
        this.unit = unit;
        this.startPoint = startPoint;
    }

    public boolean isFromLeft() {
        return fromLeft;
    }

    public Unit getUnit() {
        return unit;
    }

    public Ruler getStartPoint() {
        return startPoint;
    }
}
