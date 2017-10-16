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

    public static Paint getBlackPaint(int strokeWidth){
        if(!blackPaints.containsKey(strokeWidth)){
            blackPaints.put(strokeWidth, getNewBlackPaint(strokeWidth));
        }
        return blackPaints.get(strokeWidth);
    }

    private static Paint getNewBlackPaint(int strokeWidth){
        Paint paint = new Paint();
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(Color.BLACK);
        return paint;
    }

}