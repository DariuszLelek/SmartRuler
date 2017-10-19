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

    private final int bitmapHeight, bitmapWidth, screenOffset;
    private final Context context;

    private DateTime lastMeasureTime;

    public RulerMeasure(Bitmap bitmap, Context context, int screenOffset) {
        this.context = context;

        this.screenOffset = screenOffset;
        this.bitmapHeight = bitmap.getHeight();
        this.bitmapWidth = bitmap.getWidth();

        lastMeasureTime = DateTime.now();
    }

    public boolean canDrawNewMeasure(DateTime time) {
        return lastMeasureTime.plusMillis(measureDelayMS).isBefore(time);
    }

    public void setLastMeasureTime() {
        lastMeasureTime = DateTime.now();
    }

    public Bitmap getMeasureBitmap(MeasureOrigin origin, int pointX, Ruler ruler) {
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);

        drawMeasureField(canvas, pointX, origin == MeasureOrigin.RULER_SCREEN ? bitmapHeight / 4 : bitmapHeight, ruler);
        if(ruler == Ruler.SCREEN){
            drawMeasureStart(canvas);
        }
        drawMeasureEnd(canvas, pointX);

        return bitmap;
    }

    private void drawMeasureStart(Canvas canvas) {
        int color = ContextCompat.getColor(context, R.color.measure_start);
        drawLine(canvas, screenOffset, PaintProvider.getColorPaint(5, color));
    }

    private void drawMeasureField(Canvas canvas, int measureX, int heightY, Ruler ruler) {
        int color = ContextCompat.getColor(context, R.color.measure_field);
        canvas.drawRect(getFieldStartX(ruler), 0, measureX, heightY, PaintProvider.getColorPaint(5, color));
    }

    private void drawMeasureEnd(Canvas canvas, int measureX) {
        int color = ContextCompat.getColor(context, R.color.measure_end);
        drawLine(canvas, measureX, PaintProvider.getColorPaint(5, color));
    }

    private void drawLine(Canvas canvas, int x, Paint paint) {
        canvas.drawLine(x, 0, x, bitmapHeight, paint);
    }

    private int getFieldStartX(Ruler ruler) {
        if (ruler == Ruler.SCREEN) {
            return screenOffset;
        } else if (ruler == Ruler.RIGHT_PHONE_EDGE) {
            return bitmapWidth;
        } else {
            return 0;
        }
    }
}
