package com.darodev.smartruler.utility;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.darodev.smartruler.CalibrateActivity;
import com.darodev.smartruler.R;
import com.darodev.smartruler.ruler.Ruler;
import com.darodev.smartruler.ruler.RulerBitmapProvider;
import com.darodev.smartruler.ruler.RulerType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.darodev.smartruler.utility.Constant.CREDIT_CARD_WIDTH_CM;

/**
 * Created by Dariusz Lelek on 10/15/2017.
 * dariusz.lelek@gmail.com
 */

public class RulerData {
    private final Resources resources;
    private final SharedPreferences preferences;
    private final DisplayMetrics metrics;

    private final Map<Integer, String> cachedKeys = new ConcurrentHashMap<>();

    private float screenOffset = 0;

    public RulerData(Resources resources, SharedPreferences preferences, DisplayMetrics metrics) {
        this.resources = resources;
        this.preferences = preferences;
        this.metrics = metrics;
    }

    public boolean isInInchMode() {
        return preferences.getBoolean(getKey(R.string.unit_inch_mode_key), false);
    }

    public boolean isRulerCalibrated(Ruler ruler) {
        if (ruler == Ruler.LEFT_PHONE_EDGE) {
            return preferences.getFloat(getKey(R.string.pixels_to_left_edge_key), 0) > 0;
        } else if(ruler == Ruler.RIGHT_PHONE_EDGE){
            return preferences.getFloat(getKey(R.string.pixels_to_right_edge_key), 0) > 0;
        }else{
            return preferences.getBoolean(getKey(R.string.ruler_screen_calibrated_key), false);
        }
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
            saveData[0] = result + (getUnit() == Unit.INCH ? "\"" : "");
            saveData(saveData);
        }
    }

    public String[] getSavedData() {
        String[] saveSlotKeys = resources.getStringArray(R.array.save_slot_keys);
        String[] savedData = new String[saveSlotKeys.length];

        for (int i = 0; i < saveSlotKeys.length; i++) {
            savedData[i] = preferences.getString(saveSlotKeys[i], "");
        }

        return savedData;
    }

    public float getRulerStartPoint(RulerType type) {
        Ruler startPoint = type.getRuler();
        if (startPoint == Ruler.SCREEN) {
            return getScreenOffset();
        } else if (startPoint == Ruler.LEFT_PHONE_EDGE) {
            return -preferences.getFloat(getKey(R.string.pixels_to_left_edge_key), 0);
        } else if (startPoint == Ruler.RIGHT_PHONE_EDGE) {
            return preferences.getFloat(getKey(R.string.pixels_to_right_edge_key), 0);
        } else {
            return 0;
        }
    }

    public float getMeasureResult(float pointX, Ruler ruler, RulerBitmapProvider ruleBitmapProvider){
        final Unit unit = getUnit();
        float startPoint = getRulerStartPoint(RulerType.getType(unit, ruler));

        if (ruler == Ruler.SCREEN || ruler == Ruler.LEFT_PHONE_EDGE) {
            return (pointX - startPoint) / getPixelsIn(unit);
        } else if (ruler == Ruler.RIGHT_PHONE_EDGE) {
            int width = ruleBitmapProvider.getRulerBitmapWidth();
            return ((width - pointX) + startPoint) / getPixelsIn(unit);
        } else {
            return 0;
        }
    }

    public void saveCalibrationResult(String calibrationResult, Ruler ruler){
        if(validResult(calibrationResult)){
            float result = Float.parseFloat(calibrationResult);
            if(ruler == Ruler.SCREEN){
                saveScreenCalibration(result);
            }else if (ruler == Ruler.LEFT_PHONE_EDGE){
                saveCalibration(result, getKey(R.string.pixels_to_left_edge_key));
            }else if (ruler == Ruler.RIGHT_PHONE_EDGE){
                saveCalibration(result, getKey(R.string.pixels_to_right_edge_key));
            }
        }
    }

    private void saveScreenCalibration(float cardWidthPixels) {
        cardWidthPixels = cardWidthPixels - CalibrateActivity.SCREEN_OFFSET_PIXELS;
        float pixelsInCm = cardWidthPixels / CREDIT_CARD_WIDTH_CM.getValue();
        float pixelsInInch = cardWidthPixels / Constant.CREDIT_CARD_WIDTH_INCH.getValue();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(getKey(R.string.pixels_in_cm_key), pixelsInCm);
        editor.putFloat(getKey(R.string.pixels_in_inch_key), pixelsInInch);
        editor.putBoolean(getKey(R.string.ruler_screen_calibrated_key), true);
        editor.apply();
    }

    private void saveCalibration(float pixelsFromEdge, String key){
        float pixelsInCm = getPixelsIn(Unit.CM);
        float phoneEdgeCm = Constant.CREDIT_CARD_WIDTH_CM.getValue() - pixelsFromEdge / pixelsInCm;
        float phoneEdgePixels = phoneEdgeCm * pixelsInCm;

        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, phoneEdgePixels);
        editor.apply();
    }

    public float getMinSectionWidth(Unit unit){
        return getPixelsIn(unit) / unit.getSections();
    }

    private float getPixelsIn(Unit unit) {
        if(unit == Unit.CM){
            int def = Math.round(metrics.xdpi / Constant.CM_IN_INCH.getValue());
            return preferences.getFloat(getKey(R.string.pixels_in_cm_key), def);
        }else{
            return preferences.getFloat(getKey(R.string.pixels_in_inch_key), Math.round(metrics.xdpi));
        }
    }

    public float getScreenOffset() {
        if(screenOffset == 0){
            screenOffset = getPixelsIn(Unit.CM) / 2;
        }
        return screenOffset;
    }

    public void setCurrentRuler(Ruler ruler){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getKey(R.string.current_ruler_key), ruler.name());
        editor.apply();
    }

    public Ruler getCurrentRuler(){
        return Ruler.getByString(preferences.getString(getKey(R.string.current_ruler_key), Ruler.SCREEN.name()));
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
            Float value = Float.parseFloat(result);
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
