package com.darodev.smartruler.ruler;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.darodev.smartruler.utility.PaintProvider;
import com.darodev.smartruler.utility.RulerData;

import org.joda.time.DateTime;

import static android.R.attr.bitmap;

/**
 * Created by Dariusz Lelek on 10/17/2017.
 * dariusz.lelek@gmail.com
 */

public class RulerMeasure {
    private static final int measureDelayMS = 50;

    private final RulerData rulerData;
    private final int bitmapHeight, bitmapWidth;

    private DateTime lastMeasureTime;

    public RulerMeasure(Bitmap bitmap, RulerData rulerData) {
        this.rulerData = rulerData;
        this.bitmapHeight = bitmap.getHeight();
        this.bitmapWidth = bitmap.getWidth();

        lastMeasureTime = DateTime.now();
    }

    public boolean canDrawNewMeasure(DateTime time){
        return lastMeasureTime.plusMillis(measureDelayMS).isBefore(time);
    }

    public void setLastMeasureTime(){
        lastMeasureTime = DateTime.now();
    }

    public Bitmap getMeasureBitmap(int pointX, Ruler ruler){
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight,  Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);

        drawMeasureLine(canvas, pointX);


        return bitmap;
    }

    private void drawMeasureStartLine(Canvas canvas, Ruler ruler){
        if(ruler == Ruler.SCREEN){
            //drawLine(canvas, rulerData.getRulerStartPoint(RulerType.CM_SCREEN))
        }
    }

    private void drawMeasureLine(Canvas canvas, int measureX){
        drawLine(canvas, measureX, PaintProvider.getColorPaint(5, Color.GREEN));
    }

    private void drawLine(Canvas canvas, int x, Paint paint){
        canvas.drawLine(x, 0, x, bitmapHeight, paint);
    }
}
