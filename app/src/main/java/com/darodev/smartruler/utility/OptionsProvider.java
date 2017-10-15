package com.darodev.smartruler.utility;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.IntegerRes;

import com.darodev.smartruler.R;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Dariusz Lelek on 10/15/2017.
 * dariusz.lelek@gmail.com
 */

public class OptionsProvider {
    private final Resources resources;
    private final SharedPreferences preferences;

    private final Map<Integer, String> cachedKeys = new ConcurrentHashMap<>();

    public OptionsProvider(Resources resources, SharedPreferences preferences) {
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

    private String getKey(int keyId){
        if(!cachedKeys.containsKey(keyId)){
            cachedKeys.put(keyId, resources.getString(keyId));
        }
        return cachedKeys.get(keyId);
    }
}
