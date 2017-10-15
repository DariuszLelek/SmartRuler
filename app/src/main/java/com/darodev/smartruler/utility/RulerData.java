package com.darodev.smartruler.utility;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.IntegerRes;

import com.darodev.smartruler.R;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.darodev.smartruler.R.drawable.cm;
import static com.darodev.smartruler.R.drawable.inch;

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

    public boolean isCalibrated(){
        return preferences.getBoolean(getKey(R.string.calibrated_key), false);
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
