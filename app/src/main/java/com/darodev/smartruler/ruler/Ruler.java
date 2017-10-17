package com.darodev.smartruler.ruler;

/**
 * Created by Dariusz Lelek on 10/16/2017.
 * dariusz.lelek@gmail.com
 */

public enum Ruler {
    SCREEN, LEFT_PHONE_EDGE, RIGHT_PHONE_EDGE;

    public static Ruler getByString(String string){
        for(Ruler ruler : values()){
            if(ruler.name().equalsIgnoreCase(string)){
                return ruler;
            }
        }
        return SCREEN;
    }

    public static Ruler getNextRuler(Ruler ruler){
        if(ruler == SCREEN){
            return LEFT_PHONE_EDGE;
        }else return ruler == LEFT_PHONE_EDGE ? RIGHT_PHONE_EDGE : SCREEN;
    }
}
