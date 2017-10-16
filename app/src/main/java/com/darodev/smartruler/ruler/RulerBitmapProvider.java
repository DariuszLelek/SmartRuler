package com.darodev.smartruler.ruler;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.darodev.smartruler.ruler.line.LineStepLevel;
import com.darodev.smartruler.ruler.line.LineStepLevelHolder;
import com.darodev.smartruler.utility.PaintProvider;
import com.darodev.smartruler.utility.RulerData;
import com.darodev.smartruler.utility.Unit;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.darodev.smartruler.utility.Unit.CM;

/**
 * Created by Dariusz Lelek on 10/16/2017.
 * dariusz.lelek@gmail.com
 */

public class RulerBitmapProvider {
    private static final int numberOfDiffHeightLines = 5;
    private final TreeMap<Integer, Integer> lineHeightByLevel = new TreeMap<>();
    private final Map<RulerType, Bitmap> bitmapByRulerType = new HashMap<>();

    private final RulerData rulerData;

    public RulerBitmapProvider(Bitmap imageRulerBitmap, RulerData rulerData) {
        this.rulerData = rulerData;

        prepareLinesHeight(imageRulerBitmap.getHeight());

        populateMapWithDefaultBitmaps(imageRulerBitmap);
    }

    private void populateMapWithDefaultBitmaps(Bitmap defaultBitmap){
        for(RulerType rulerType : RulerType.values()){
            bitmapByRulerType.put(rulerType, Bitmap.createBitmap(defaultBitmap));
        }
    }

    public void prepareRulers(Ruler ruler){
        if(ruler == Ruler.SCREEN){
            prepareRulerBitmap(RulerType.CM_SCREEN);
            prepareRulerBitmap(RulerType.CM_SCREEN_R);
            prepareRulerBitmap(RulerType.INCH_SCREEN);
            prepareRulerBitmap(RulerType.INCH_SCREEN_R);
        }else if(ruler == Ruler.LEFT_PHONE_EDGE){
            prepareRulerBitmap(RulerType.CM_PHONE_EDGE_L);
            prepareRulerBitmap(RulerType.INCH_PHONE_EDGE_L);
        }else if(ruler == Ruler.RIGHT_PHONE_EDGE){
            prepareRulerBitmap(RulerType.CM_PHONE_EDGE_R);
            prepareRulerBitmap(RulerType.INCH_PHONE_EDGE_R);
        }
    }

    public Bitmap getRulerBitmap(Unit unit) {
        RulerType type = unit == CM ? RulerType.CM_SCREEN : RulerType.INCH_SCREEN;
        return bitmapByRulerType.get(type);
    }

    private void prepareLinesHeight(int bitmapHeight) {
        int level = 0;
        int height = bitmapHeight / 2;
        while (level <= numberOfDiffHeightLines) {
            lineHeightByLevel.put(level, height);
            height /= 2;
            level++;
        }
    }

    private void prepareRulerBitmap(RulerType type) {
        Unit unit = type.getUnit();
        int startPoint = rulerData.getRulerStartPoint(type);
        Canvas canvas = new Canvas(bitmapByRulerType.get(type));
        LineStepLevelHolder lineStepLevelHolder = getLineStepLevelHolder(unit);

        if(type.isFromLeft()){
            drawRulerLinesFromLeft(canvas, startPoint, lineStepLevelHolder);
        }else{
            drawRulerLinesFromRight(canvas, startPoint, lineStepLevelHolder);
        }
    }

    private LineStepLevelHolder getLineStepLevelHolder(Unit unit) {
        if (unit == CM) {
            int pixelsInCm = rulerData.getPixelsIn(Unit.CM);
            int smallestStep = pixelsInCm / 10;
            return new LineStepLevelHolder(new LineStepLevel[]{
                    new LineStepLevel(smallestStep, 1, 2),
                    new LineStepLevel(smallestStep, 5, 1),
                    new LineStepLevel(smallestStep, 10, 0)
            });
        } else {
            int pixelsInInch = rulerData.getPixelsIn(Unit.INCH);
            int smallestStep = pixelsInInch / 8;
            return new LineStepLevelHolder(new LineStepLevel[]{
                    new LineStepLevel(smallestStep, 1, 3),
                    new LineStepLevel(smallestStep, 2, 2),
                    new LineStepLevel(smallestStep, 4, 1),
                    new LineStepLevel(smallestStep, 8, 0)
            });
        }
    }

    // TODO rewrite this to draw from right
    private void drawRulerLinesFromRight(Canvas canvas, int startPoint, LineStepLevelHolder lineStepLevelHolder) {
        int canvasWidth = canvas.getWidth();

        for (int x = startPoint; x <= canvasWidth; x ++) {
            int level = lineStepLevelHolder.getLevelByStep(x - startPoint);
            int lineHeight = getLineHeightByLevel(level);

            if (lineHeight > 0 && x >= 0) {
                canvas.drawLine(x, 0, x, lineHeight, getPaintByLevel(level));
            }
        }
    }

    private void drawRulerLinesFromLeft(Canvas canvas, int startPoint, LineStepLevelHolder lineStepLevelHolder) {
        int canvasWidth = canvas.getWidth();

        for (int x = startPoint; x <= canvasWidth; x ++) {
            int level = lineStepLevelHolder.getLevelByStep(x - startPoint);
            int lineHeight = getLineHeightByLevel(level);

            if (lineHeight > 0 && x >= 0) {
                canvas.drawLine(x, 0, x, lineHeight, getPaintByLevel(level));
            }
        }
    }

    private Paint getPaintByLevel(int level) {
        return level == 0 ? PaintProvider.getBlackPaint(3) : PaintProvider.getBlackPaint(2);
    }

    private int getLineHeightByLevel(int level) {
        if (lineHeightByLevel.containsKey(level)) {
            return lineHeightByLevel.get(level);
        }
        return -1;
    }
}
