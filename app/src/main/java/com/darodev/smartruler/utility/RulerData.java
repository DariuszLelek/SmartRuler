package com.darodev.smartruler.utility;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.darodev.smartruler.R;
import com.darodev.smartruler.ruler.Ruler;
import com.darodev.smartruler.ruler.RulerType;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Dariusz Lelek on 10/15/2017.
 * dariusz.lelek@gmail.com
 */

public class RulerData {
    private final Resources resources;
    private final SharedPreferences preferences;
    private final DisplayMetrics metrics;

    private final Map<Integer, String> cachedKeys = new ConcurrentHashMap<>();

    public RulerData(Resources resources, SharedPreferences preferences, DisplayMetrics metrics) {
        this.resources = resources;
        this.preferences = preferences;
        this.metrics = metrics;
    }

    public boolean isRulerCalibrated(){
        return preferences.getBoolean(getKey(R.string.ruler_calibrated_key), false);
    }

    public boolean isInInchMode() {
        return preferences.getBoolean(getKey(R.string.unit_inch_mode_key), false);
    }

    public boolean isRulerSet(Ruler ruler) {
        if (ruler == Ruler.LEFT_PHONE_EDGE) {
            return preferences.getInt(getKey(R.string.pixels_to_left_edge_key), 0) > 0;
        } else
            return ruler != Ruler.RIGHT_PHONE_EDGE || preferences.getInt(getKey(R.string.pixels_to_right_edge_key), 0) > 0;
    }

    public void swapInchMode() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(getKey(R.string.unit_inch_mode_key), !isInInchMode());
        editor.apply();
    }

    public void saveMeasureResult(String result) {
        if (validResult(result)) {
            String[] saveData = getSavedData();
            System.arraycopy(saveData, 0, saveData, 1, saveData.length - 1);
            saveData[0] = result + " " + getUnit().toString();
            saveData(saveData);
        }
    }

    public String[] getSavedData() {
        String[] saveSlotKeys = resources.getStringArray(R.array.save_slot_keys);
        String[] savedData = new String[saveSlotKeys.length];

        for (int i = 0; i < saveSlotKeys.length; i++) {
            savedData[i] = preferences.getString(saveSlotKeys[i], "EMPTY");
        }

        return savedData;
    }

    public int getRulerStartPoint(RulerType type) {
        Ruler startPoint = type.getRuler();
        if (startPoint == Ruler.SCREEN) {
            return getPixelsIn(type.getUnit()) / getOffsetDivider(type.getUnit());
        } else if (startPoint == Ruler.LEFT_PHONE_EDGE) {
            return -preferences.getInt(getKey(R.string.pixels_to_left_edge_key), 300);
        } else if (startPoint == Ruler.RIGHT_PHONE_EDGE) {
            return preferences.getInt(getKey(R.string.pixels_to_right_edge_key), 300);
        } else {
            return 0;
        }
    }

    public String getMeasureResult(float pointX){
        int pixelsInCm = getPixelsIn(Unit.CM);
        float reult = pointX/pixelsInCm;
        return String.format(Locale.ENGLISH, "%.2f", reult);
    }

    public int getPixelsIn(Unit unit) {


        return unit == Unit.CM ? Math.round(metrics.xdpi / Constant.CM_IN_INCH.getValue()) : Math.round(metrics.xdpi);
    }

    private int getOffsetDivider(Unit unit) {
        return unit.getOffset();
    }

    public void setCurrentRuler(Ruler ruler){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getKey(R.string.current_ruler_key), ruler.name());
        editor.apply();
    }

    public Ruler getCurrentRuler(){
        return Ruler.getByString(preferences.getString(getKey(R.string.current_ruler_key), "SCREEN"));
    }

    private Unit getUnit() {
        return isInInchMode() ? Unit.INCH : Unit.CM;
    }

    private void saveData(String[] saveData) {
        String[] saveSlotKeys = resources.getStringArray(R.array.save_slot_keys);
        int size = Math.min(saveSlotKeys.length, saveData.length);

        SharedPreferences.Editor editor = preferences.edit();
        for (int i = 0; i < size; i++) {
            editor.putString(saveSlotKeys[i], saveData[i]);
        }
        editor.apply();
    }

    private boolean validResult(String result) {
        try {
            Integer value = Integer.parseInt(result);
            return value > 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private String getKey(int keyId) {
        if (!cachedKeys.containsKey(keyId)) {
            cachedKeys.put(keyId, resources.getString(keyId));
        }
        return cachedKeys.get(keyId);
    }
}
