package com.darodev.smartruler.ruler;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import org.joda.time.DateTime;

/**
 * Created by Dariusz Lelek on 10/19/2017.
 * dariusz.lelek@gmail.com
 */

public interface Measure {
    public boolean canDrawNewMeasure(DateTime time);
    public void setLastMeasureTime();
    public Bitmap getMeasureBitmap(MeasureOrigin origin, int pointX, Ruler ruler);
}
