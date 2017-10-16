package com.darodev.smartruler.ruler.line;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Dariusz Lelek on 10/16/2017.
 * dariusz.lelek@gmail.com
 */

public class LineStepLevelHolder {
    private final TreeMap<Integer, Integer> levelByStep = new TreeMap<>(Collections.reverseOrder());

    private final int smallestStep;

    public LineStepLevelHolder(LineStepLevel[] stepLevelsArray){
        populateLevelByStep(stepLevelsArray);

        smallestStep = levelByStep.lastKey();
    }

    public int getSmallestStep(){
        return smallestStep;
    }

    public int getLevelByStep(int step){
        for(Map.Entry<Integer, Integer> entry : levelByStep.entrySet()){
            if(step % entry.getKey() == 0){
                return entry.getValue();
            }
        }
        return -1;
    }

    private void populateLevelByStep(LineStepLevel[] stepLevelsArray){
        for(LineStepLevel lsl : stepLevelsArray){
            levelByStep.put(lsl.getStep(), lsl.getLevel());
        }
    }
}
