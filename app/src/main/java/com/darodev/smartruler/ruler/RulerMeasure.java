package com.darodev.smartruler.ruler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;

import com.darodev.smartruler.R;
import com.darodev.smartruler.utility.Constant;
import com.darodev.smartruler.utility.PaintProvider;

import org.joda.time.DateTime;

/**
 * Created by Dariusz Lelek on 10/17/2017.
 * dariusz.lelek@gmail.com
 */

public class RulerMeasure {
    private static final int measureDelayMS = 50;

    private final int bitmapHeight, bitmapWidth;
    private float screenOffset;
    private final Context context;

    private DateTime lastMeasureTime;

    public RulerMeasure(Bitmap bitmap, Context context, float screenOffset) {
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

        drawMeasureField(canvas, pointX, bitmapHeight / origin.getDivider(), ruler);
        drawMeasureStart(canvas, ruler);
        drawMeasureEnd(canvas, pointX);

        return bitmap;
    }

    private void drawMeasureStart(Canvas canvas, Ruler ruler) {
        if (ruler == Ruler.SCREEN) {
            int color = ContextCompat.getColor(context, R.color.measure_line);
            Paint paint = PaintProvider.getColorPaint(Constant.MEASURE_LINE_WIDTH.getValue(), color);

            drawLine(canvas, screenOffset, paint);
        }
    }

    private void drawMeasureField(Canvas canvas, int measureX, int heightY, Ruler ruler) {
        int color = ContextCompat.getColor(context, R.color.measure_field);
        Paint paint = PaintProvider.getColorPaint(0, color);

        canvas.drawRect(getFieldStartX(ruler), 0, measureX, heightY, paint);
    }

    private void drawMeasureEnd(Canvas canvas, int measureX) {
        int color = ContextCompat.getColor(context, R.color.measure_line);
        Paint paint = PaintProvider.getColorPaint(Constant.MEASURE_LINE_WIDTH.getValue(), color);

        drawLine(canvas, measureX, paint);
    }

    private void drawLine(Canvas canvas, float x, Paint paint) {
        canvas.drawLine(x, 0, x, bitmapHeight, paint);
    }

    private float getFieldStartX(Ruler ruler) {
        if (ruler == Ruler.SCREEN) {
            return screenOffset;
        } else if (ruler == Ruler.RIGHT_PHONE_EDGE) {
            return bitmapWidth;
        } else {
            return 0;
        }
    }
}
