package com.darodev.smartruler.ruler;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.darodev.smartruler.utility.PaintProvider;
import com.darodev.smartruler.utility.RulerData;

import org.joda.time.DateTime;

import static android.R.attr.src;

/**
 * Created by Dariusz Lelek on 10/17/2017.
 * dariusz.lelek@gmail.com
 */

public class RulerMeasure {
    private static final int measureDelayMS = 50;

    private final Bitmap originalBitmap;
    private final RulerData rulerData;
    private final int bitmapHeight, bitmapWidth, bitmapH;


    private DateTime lastMeasureTime;
    private int lastMeasureX;

    public RulerMeasure(Bitmap bitmap, RulerData rulerData) {
        this.originalBitmap = bitmap;
        this.rulerData = rulerData;
        this.bitmapHeight = bitmap.getHeight();
        this.bitmapWidth = originalBitmap.getWidth();
        this.bitmapH = originalBitmap.getHeight();

        //bitmapWithMeasure = getMeasureBitmap(0);
        lastMeasureTime = DateTime.now();
    }

    public boolean canDrawNewMeasure(DateTime time){
        return lastMeasureTime.plusMillis(measureDelayMS).isBefore(time);
    }

    public Bitmap getMeasureBitmap(int pointX){
        Bitmap bitmap;

        lastMeasureX = pointX;
        lastMeasureTime = DateTime.now();

        bitmap = Bitmap.createBitmap(bitmapWidth, bitmapH,  Bitmap.Config.ALPHA_8);
        drawMeasure(bitmap);

        return bitmap;
    }

    private void drawMeasure(Bitmap bitmap){
        Canvas canvas = new Canvas(bitmap);
//        Paint paint = new Paint();
//        paint.setAlpha(0);
//        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.drawLine(lastMeasureX, 0, lastMeasureX, bitmapHeight, PaintProvider.getBlackPaint(4));
    }
}
