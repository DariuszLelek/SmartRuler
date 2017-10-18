package com.darodev.smartruler.utility;

import android.graphics.Color;
import android.graphics.Paint;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Dariusz Lelek on 10/16/2017.
 * dariusz.lelek@gmail.com
 */

public class PaintProvider {
    private static final Map<Integer, Paint> blackPaints = new ConcurrentHashMap<>();
    private static final Map<ColorWidth, Paint> colorPaints = new ConcurrentHashMap<>();
    private static final Paint textPaint = getNewTextPaint();

    public static Paint getBlackPaint(int strokeWidth){
        if(!blackPaints.containsKey(strokeWidth)){
            blackPaints.put(strokeWidth, getNewPaint(strokeWidth, Color.BLACK));
        }
        return blackPaints.get(strokeWidth);
    }

    public static Paint getColorPaint(int strokeWidth, int color){
        ColorWidth key = new ColorWidth(color, strokeWidth);
        if(!colorPaints.containsKey(key)){
            colorPaints.put(key, getNewPaint(strokeWidth, color));
        }
        return colorPaints.get(key);
    }

    public static Paint getTextPaint(){
        return textPaint;
    }

    private static Paint getNewTextPaint(){
        Paint paint = new Paint();
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.BLACK);
        return paint;
    }

    private static Paint getNewPaint(int strokeWidth, int color){
        Paint paint = new Paint();
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(color);
        return paint;
    }

    private static class ColorWidth{
        private int color;
        private int width;

        ColorWidth(int color, int width) {
            this.color = color;
            this.width = width;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ColorWidth that = (ColorWidth) o;

            return color == that.color && width == that.width;

        }

        @Override
        public int hashCode() {
            int result = color;
            result = 31 * result + width;
            return result;
        }
    }

}
