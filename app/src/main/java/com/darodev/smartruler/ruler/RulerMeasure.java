package com.darodev.smartruler.ruler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;

import com.darodev.smartruler.R;
import com.darodev.smartruler.utility.PaintProvider;
import com.darodev.smartruler.utility.RulerData;

import org.joda.time.DateTime;

/**
 * Created by Dariusz Lelek on 10/17/2017.
 * dariusz.lelek@gmail.com
 */

public class RulerMeasure {
    private static final int measureDelayMS = 50;

    private final RulerData rulerData;
    private final int bitmapHeight, bitmapWidth;
    private final Context context;

    private DateTime lastMeasureTime;

    public RulerMeasure(Bitmap bitmap, RulerData rulerData, Context context) {
        this.rulerData = rulerData;
        this.context = context;
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

        drawMeasureField(canvas, pointX, ruler);
        drawMeasureStart(canvas, ruler);
        drawMeasureEnd(canvas, pointX);

        return bitmap;
    }

    private void drawMeasureStart(Canvas canvas, Ruler ruler){
        if(ruler == Ruler.SCREEN){
            int color = ContextCompat.getColor(context, R.color.measure_start);
            drawLine(canvas, rulerData.getScreenOffset(), PaintProvider.getColorPaint(5, color));
        }
    }

    private void drawMeasureField(Canvas canvas, int measureX, Ruler ruler){
        int startX = ruler == Ruler.SCREEN ? rulerData.getScreenOffset() : 0;
        int color = ContextCompat.getColor(context, R.color.measure_field);
        canvas.drawRect(startX, 0, measureX, bitmapHeight/4, PaintProvider.getColorPaint(5, color));
    }

    private void drawMeasureEnd(Canvas canvas, int measureX){
        int color = ContextCompat.getColor(context, R.color.measure_end);
        drawLine(canvas, measureX, PaintProvider.getColorPaint(5, color));
    }

    private void drawLine(Canvas canvas, int x, Paint paint){
        canvas.drawLine(x, 0, x, bitmapHeight, paint);
    }
}
