package com.darodev.smartruler.ruler.line;

/**
 * Created by Dariusz Lelek on 10/16/2017.
 * dariusz.lelek@gmail.com
 */

public class LineStepLevel {
    private final int step, level;

    public LineStepLevel(int step, int stepMultiplier, int level) {
        this.step = step * stepMultiplier;
        this.level = level;
    }

    int getStep() {
        return step;
    }

    int getLevel() {
        return level;
    }
}
