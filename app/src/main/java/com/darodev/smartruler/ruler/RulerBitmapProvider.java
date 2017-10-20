package com.darodev.smartruler.ruler;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

import com.darodev.smartruler.utility.Constant;
import com.darodev.smartruler.utility.PaintProvider;
import com.darodev.smartruler.utility.RulerData;
import com.darodev.smartruler.utility.Unit;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Dariusz Lelek on 10/16/2017.
 * dariusz.lelek@gmail.com
 */

public class RulerBitmapProvider {
    private static final int numberOfDiffHeightLines = 5;
    private static final float textPositionHeightDivider = 1.5F;
    private static final Map<Unit, TreeMap<Integer, Integer>> levelSectionsByUnit = new HashMap<>();

    private final Resources res;
    private final TreeMap<Integer, Integer> lineHeightByLevel = new TreeMap<>();
    private final Map<RulerType, BitmapDrawable> bitmapByRulerType = new HashMap<>();
    private final int rulerBitmapWidth;

    private int digitsCounter = 0;
    private final RulerData rulerData;

    public RulerBitmapProvider(Bitmap imageRulerBitmap, RulerData rulerData, Resources res) {
        this.rulerData = rulerData;
        this.res = res;
        this.rulerBitmapWidth = imageRulerBitmap.getWidth();

        prepareLevelSectionsByUnit();
        prepareLinesHeight(imageRulerBitmap.getHeight());
        populateMapWithDefaultBitmaps(imageRulerBitmap);
    }

    private void prepareLevelSectionsByUnit(){
        levelSectionsByUnit.put(Unit.CM, getLevelSectionsForCm());
        levelSectionsByUnit.put(Unit.INCH, getLevelSectionsForInch());
    }

    private TreeMap<Integer, Integer> getLevelSectionsForCm(){
        TreeMap<Integer, Integer> levelSections = new TreeMap<>();
        levelSections.put(0, 10);
        levelSections.put(1, 5);
        levelSections.put(2, 1);
        return levelSections;
    }

    private TreeMap<Integer, Integer> getLevelSectionsForInch(){
        TreeMap<Integer, Integer> levelSections = new TreeMap<>();
        levelSections.put(0, 8);
        levelSections.put(1, 4);
        levelSections.put(2, 2);
        levelSections.put(3, 1);
        return levelSections;
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

    private void populateMapWithDefaultBitmaps(Bitmap defaultBitmap) {
        for (RulerType rulerType : RulerType.values()) {
            bitmapByRulerType.put(rulerType, new BitmapDrawable(res, Bitmap.createBitmap(defaultBitmap)));
        }
    }

    public int getRulerBitmapWidth() {
        return rulerBitmapWidth;
    }

    public void prepareRulers() {
        for(RulerType rulerType : RulerType.values()){
            prepareRulerBitmap(rulerType);
        }
    }

    private void prepareRulerBitmap(RulerType type) {
        final Unit unit = type.getUnit();
        final boolean isFromLeft = type.isFromLeft();
        final float startPoint = rulerData.getRulerStartPoint(type);
        final Canvas canvas = new Canvas(bitmapByRulerType.get(type).getBitmap());
        digitsCounter = 0;

        for(Map.Entry<Integer, Integer> entry : levelSectionsByUnit.get(unit).entrySet()){
            int level = entry.getKey();
            int lineHeight = getLineHeightByLevel(level);
            float sectionWidth = rulerData.getMinSectionWidth(unit) * entry.getValue();

            if(isFromLeft){
                for (float x = startPoint; sectionWidth > 0 && x <= canvas.getWidth(); x += sectionWidth){
                    drawRulerSection(canvas, level, lineHeight, x);
                    drawRulerDigits(canvas, level, x);
                }
            }else{
                for (float x = canvas.getWidth() + startPoint; sectionWidth > 0 && x >= 0; x -= sectionWidth) {
                    drawRulerSection(canvas, level, lineHeight, x);
                    drawRulerDigits(canvas, level, x);
                }
            }
        }
    }

    private int getLineHeightByLevel(int level) {
        if (lineHeightByLevel.containsKey(level)) {
            return lineHeightByLevel.get(level);
        }
        return -1;
    }

    private void drawRulerSection(Canvas canvas, int level, int lineHeight, float x) {
        if(x >=0 && x <= canvas.getWidth() && lineHeight > 0){
            canvas.drawLine(x, 0, x, lineHeight, getPaintByLevel(level));
        }
    }

    private Paint getPaintByLevel(int level) {
        if(level == 0){
            return PaintProvider.getBlackPaint(Constant.RULER_MAIN_LINE_WIDTH.getValue());
        }else{
            return PaintProvider.getBlackPaint(Constant.RULER_LINE_WIDTH.getValue());
        }
    }

    private void drawRulerDigits(Canvas canvas, int level, float x) {
        if(level == 0){
            float textY = canvas.getHeight() / textPositionHeightDivider;
            canvas.drawText(String.valueOf(digitsCounter), x, textY, PaintProvider.getTextPaint());
            digitsCounter++;
        }
    }

    public BitmapDrawable getRulerBitmap(Unit unit, Ruler ruler) {
        return bitmapByRulerType.get(RulerType.getType(unit, ruler));
    }
}
