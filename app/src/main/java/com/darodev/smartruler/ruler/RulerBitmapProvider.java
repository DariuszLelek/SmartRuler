package com.darodev.smartruler.ruler;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.darodev.smartruler.utility.RulerData;
import com.darodev.smartruler.utility.Unit;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dariusz Lelek on 10/16/2017.
 * dariusz.lelek@gmail.com
 */

public class RulerBitmapProvider {
    private final int numberOfDiffHeightLines = 5;
    private final Map<Integer, Integer> lineHeightByLevel = new HashMap<>();
    private final RulerData rulerData;

    private Bitmap rulerBitmapCm, rulerBitmapInch;
    private Paint rulerLinePaint, rulerLinePaintBold, rulerDigitPaint;

    public RulerBitmapProvider(Bitmap imageRulerBitmap, RulerData rulerData) {
        this.rulerData = rulerData;

        preparePaints();
        prepareLinesHeight(imageRulerBitmap.getHeight());
        prepareRulerBitmapCm(imageRulerBitmap);
        prepareRulerInch(imageRulerBitmap);
    }

    public Bitmap getRulerBitmap(Unit unit) {
        return unit == Unit.CM ? rulerBitmapCm : rulerBitmapInch;
    }

    private void preparePaints(){
        rulerLinePaint = getRoundPaintBlack(2);
        rulerLinePaintBold = getRoundPaintBlack(3);
        rulerDigitPaint = getRoundPaintBlack(2);
    }
    
    private Paint getRoundPaintBlack(int strokeWidth){
        Paint paint = new Paint();
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(Color.BLACK);
        return paint;
    }
    
    private void prepareLinesHeight(int bitmapHeight){
        int level = 0;
        int height = bitmapHeight/2;
        while(level <= numberOfDiffHeightLines){
            lineHeightByLevel.put(level, height);
            height /= 2;
            level++;
        }
    }

    private void prepareRulerBitmapCm(Bitmap bitmap){
        rulerBitmapCm = Bitmap.createBitmap(bitmap);
        int pixelsInCm = rulerData.getPixelsInCm();
        int pixelsOffset = pixelsInCm / 2;
        Canvas canvasCm = new Canvas(rulerBitmapCm);

        drawLines(canvasCm, pixelsOffset, pixelsInCm / 10, 2, rulerLinePaint);
        drawLines(canvasCm, pixelsOffset, pixelsInCm / 2, 1, rulerLinePaint);
        drawLines(canvasCm, pixelsOffset, pixelsInCm , 0, rulerLinePaint);
    }

    private void prepareRulerInch(Bitmap bitmap){
        rulerBitmapInch = Bitmap.createBitmap(bitmap);
        Canvas canvasCm = new Canvas(rulerBitmapInch);
        drawLines(canvasCm, 10, rulerData.getPixelsInCm() / 10, 2, rulerLinePaint);
    }

    private void drawLines(Canvas canvas, int offset, int step, int level, Paint paint){
        int canvasWidth = canvas.getWidth();
        int lineHeight = getOrDefaultLevel(level);

        if(lineHeight > 0){
            for(int i=0; i<canvasWidth - offset; i+=step){
                int x = offset + i;
                canvas.drawLine(x, 0, x, lineHeight, paint);
            }
        }
    }

    private int getOrDefaultLevel(int level){
        if(lineHeightByLevel.containsKey(level)){
            return lineHeightByLevel.get(level);
        }
        return 0;
    }

    private boolean rulerPrepared(){
        return rulerBitmapCm != null && rulerBitmapInch != null;
    }
}
