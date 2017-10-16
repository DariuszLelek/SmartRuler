package com.darodev.smartruler.utility;

import android.content.SharedPreferences;
import android.content.res.Resources;

import com.darodev.smartruler.R;
import com.darodev.smartruler.ruler.Ruler;
import com.darodev.smartruler.ruler.RulerType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Dariusz Lelek on 10/15/2017.
 * dariusz.lelek@gmail.com
 */

public class RulerData {
    private final Resources resources;
    private final SharedPreferences preferences;

    private final Map<Integer, String> cachedKeys = new ConcurrentHashMap<>();

    public RulerData(Resources resources, SharedPreferences preferences) {
        this.resources = resources;
        this.preferences = preferences;
    }

    public boolean isInInchMode(){
        return preferences.getBoolean(getKey(R.string.unit_inch_mode_key), false);
    }

    public boolean isRulerSet(Ruler ruler){
        if(ruler == Ruler.LEFT_PHONE_EDGE){
            return preferences.getBoolean(getKey(R.string.phone_set_edge_left_key), false);
        }else if (ruler == Ruler.RIGHT_PHONE_EDGE) {
            return preferences.getBoolean(getKey(R.string.phone_set_edge_right_key), false);
        } else
            // TODO change flag to false after testing
            return ruler == Ruler.SCREEN && preferences.getBoolean(getKey(R.string.phone_set_screen_key), true);
    }

    public void swapInchMode(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(getKey(R.string.unit_inch_mode_key), !isInInchMode());
        editor.apply();
    }

    public void saveMeasureResult(String result){
        if(validResult(result)){
            String[] saveData = getSavedData();
            System.arraycopy(saveData, 0, saveData, 1, saveData.length - 1);
            saveData[0] = result + " " + getUnit().toString();
            saveData(saveData);
        }
    }

    public String[] getSavedData(){
        String[] saveSlotKeys = resources.getStringArray(R.array.save_slot_keys);
        String[] savedData = new String[saveSlotKeys.length];

        for(int i=0; i < saveSlotKeys.length; i++){
            savedData[i] = preferences.getString(saveSlotKeys[i], "empty");
        }

        return savedData;
    }

    public int getRulerStartPoint(RulerType type){
        Ruler startPoint = type.getStartPoint();
        if(startPoint == Ruler.SCREEN){
            return getPixelsIn(type.getUnit()) / getOffsetDivider(type.getUnit());
        }else if(startPoint == Ruler.LEFT_PHONE_EDGE){
            // TODO
            return -300;
        }else if(startPoint == Ruler.RIGHT_PHONE_EDGE){
            // TODO
            return 300;
        }else{
            return 0;
        }
    }
    public int getPixelsIn(Unit unit){
        return unit == Unit.CM ? 100 : 250;
    }

    private int getOffsetDivider(Unit unit){
        return unit == Unit.CM ? 2 : 5;
    }

    private Unit getUnit(){
        return isInInchMode() ? Unit.INCH : Unit.CM;
    }

    private void saveData(String[] saveData){
        String[] saveSlotKeys = resources.getStringArray(R.array.save_slot_keys);
        int size = Math.min(saveSlotKeys.length, saveData.length);

        SharedPreferences.Editor editor = preferences.edit();
        for(int i=0; i < size; i++){
            editor.putString(saveSlotKeys[i], saveData[i]);
        }
        editor.apply();
    }

    private boolean validResult(String result){
        try{
            Integer value = Integer.parseInt(result);
            return value > 0;
        }catch (NumberFormatException ex){
            return false;
        }
    }

    private String getKey(int keyId){
        if(!cachedKeys.containsKey(keyId)){
            cachedKeys.put(keyId, resources.getString(keyId));
        }
        return cachedKeys.get(keyId);
    }
}
