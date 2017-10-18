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
    CM_PHONE_EDGE_L(true, CM, Ruler.LEFT_PHONE_EDGE),
    CM_PHONE_EDGE_R(false, CM, Ruler.RIGHT_PHONE_EDGE),
    INCH_SCREEN(true, INCH, Ruler.SCREEN),
    INCH_PHONE_EDGE_L(true, INCH, Ruler.LEFT_PHONE_EDGE),
    INCH_PHONE_EDGE_R(false, INCH, Ruler.RIGHT_PHONE_EDGE);

    private boolean fromLeft;
    private Unit unit;
    private Ruler ruler;

    RulerType(boolean fromLeft, Unit unit, Ruler ruler) {
        this.fromLeft = fromLeft;
        this.unit = unit;
        this.ruler = ruler;
    }

    public boolean isFromLeft() {
        return fromLeft;
    }

    public Unit getUnit() {
        return unit;
    }

    public Ruler getRuler() {
        return ruler;
    }

    public static RulerType getType(Unit unit, Ruler ruler){
        for(RulerType type : RulerType.values()){
            if(type.unit == unit && type.ruler == ruler){
                return type;
            }
        }
        return CM_SCREEN;
    }
}
