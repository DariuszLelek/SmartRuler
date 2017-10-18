package com.darodev.smartruler;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.darodev.smartruler.ruler.Ruler;
import com.darodev.smartruler.ruler.RulerBitmapProvider;
import com.darodev.smartruler.ruler.RulerMeasure;
import com.darodev.smartruler.utility.RulerData;
import com.darodev.smartruler.utility.Unit;

import org.joda.time.DateTime;

import java.util.Locale;

public class RulerActivity extends AppCompatActivity {
    private RulerData rulerData;
    private RulerMeasure rulerMeasure;
    private ImageView imageInfo, imageRuler;
    private TextView[] textSavedData;
    private TextView textMeasureResult, textInfo;
    private Resources resources;
    private RulerBitmapProvider rulerBitmapProvider;
    private Ruler currentRuler;
    private ImageView imageUnit, imageRulerButton, imageShadowL, imageShadowR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruler);

        resources = getResources();
        SharedPreferences prefs = getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);


        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(resources.getString(R.string.pixels_to_left_edge_key), 300);
        editor.putInt(resources.getString(R.string.pixels_to_right_edge_key), 300);
        editor.apply();

        imageRuler = (ImageView) findViewById(R.id.image_ruler);
        imageUnit = (ImageView) findViewById(R.id.image_unit);
        imageRulerButton = (ImageView) findViewById(R.id.image_ruler_button);
        imageInfo = (ImageView) findViewById(R.id.image_info);
        imageShadowL = (ImageView) findViewById(R.id.image_shadow_left);
        imageShadowR = (ImageView) findViewById(R.id.image_shadow_right);
        textMeasureResult = (TextView) findViewById(R.id.text_measure_result);
        textInfo = (TextView) findViewById(R.id.text_info);
        rulerData = new RulerData(resources, prefs, metrics);
        currentRuler = rulerData.getCurrentRuler();

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/digital-7.ttf");
        textMeasureResult.setTypeface(typeface);

        refreshImageRulerType();
        refreshImageUnitImage();
        refreshRulerShadow();
        refreshTextInfo();
        refreshSavedData();

//        prepareLabelShowLastMeasure();
        prepareImageRulerBitmap();
        prepareImageRulerListener();
    }

    private TextView[] getTextSaveViews(){
        if(textSavedData == null){
            int numOfSavedDataFields = 5;
            textSavedData = new TextView[numOfSavedDataFields];

            textSavedData[0] = (TextView) findViewById(R.id.text_save_1);
            textSavedData[1] = (TextView) findViewById(R.id.text_save_2);
            textSavedData[2] = (TextView) findViewById(R.id.text_save_3);
            textSavedData[3] = (TextView) findViewById(R.id.text_save_4);
        }

        return textSavedData;
    }

    private void refreshSavedData() {
        String[] savedData = rulerData.getSavedData();
        TextView[] textSaveViews = getTextSaveViews();

        int size = Math.min(savedData.length, textSaveViews.length);

        for (int i = 0; i < size; i++) {
            textSaveViews[i].setText(savedData[i]);
        }
    }

    public void clickInfo(View view){
        imageInfo.setVisibility(View.INVISIBLE);
    }

    public void clickOptions(View view){

    }

    public void clickUnit(View view){
        rulerData.swapInchMode();
        refreshImageUnitImage();
        refreshRulerBackgroundBitmap();
    }

    public void clickSave(View view){
        rulerData.saveMeasureResult(textMeasureResult.getText().toString());

        clearMeasure();
        refreshSavedData();
    }

//    private void prepareLabelShowLastMeasure(){
//        TextView lastMeasureView = (TextView) findViewById(R.id.label_show_last_measure);
//        lastMeasureView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                double lastMeasure = rulerData.getLastMeasure();
//                if(lastMeasure > 0){
//                    imageRuler.setImageBitmap(rulerMeasure.getLastMeasureBitmap(rulerData.getLastMeasureX(), currentRuler));
//                    double result = rulerData.getLastMeasure();
//                    textMeasureResult.setText();
//                }
//            }
//        });
//    }

    private void prepareImageRulerListener(){
        imageRuler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE){
                    if(rulerMeasure != null && rulerMeasure.canDrawNewMeasure(DateTime.now())){
                        rulerMeasure.setLastMeasureTime();
                        int pointX = Math.round(event.getX());
                        imageRuler.setImageBitmap(rulerMeasure.getMeasureBitmap(pointX, currentRuler));
                        float result = rulerData.getMeasureResult(pointX, currentRuler, rulerBitmapProvider);
                        textMeasureResult.setText(getFormattedResult(result));
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private String getFormattedResult(float result){
        return String.format(Locale.ENGLISH, "%.2f", Math.max(result, 0));
    }

    private void prepareImageRulerBitmap(){
        imageRuler.post(new Runnable() {
            @Override
            public void run() {
                imageRuler.setDrawingCacheEnabled(true);
                imageRuler.buildDrawingCache();
                Bitmap drawingCacheBitmap = imageRuler.getDrawingCache();

                prepareRulerBitmapProvider(drawingCacheBitmap);
                prepareRulerMeasure(drawingCacheBitmap);
            }
        });
    }

    private void prepareRulerMeasure(Bitmap imageRulerBitmap){
        rulerMeasure = new RulerMeasure(imageRulerBitmap, rulerData, getApplicationContext());
    }

    private void prepareRulerBitmapProvider(Bitmap imageRulerBitmap){
        rulerBitmapProvider = new RulerBitmapProvider(imageRulerBitmap, rulerData, resources);

        for(Ruler ruler : Ruler.values()){
            if(rulerData.isRulerSet(ruler)){
                rulerBitmapProvider.prepareRulers(ruler);
            }
        }

        refreshRulerBackgroundBitmap();
    }

    private void clearMeasure(){
        resetMeasureResult();
        imageRuler.setImageBitmap(null);
    }

    private void refreshRulerBackgroundBitmap(){
        clearMeasure();

        Unit unit = rulerData.isInInchMode() ? Unit.INCH : Unit.CM;
        imageRuler.setBackground(rulerBitmapProvider.getRulerBitmap(unit, currentRuler));
    }

    public void clickRulerType(View view){
        int rulersNum = Ruler.values().length - 1;
        Ruler ruler;

        while(rulersNum > 0){
            ruler = Ruler.getNextRuler(currentRuler);
            if(rulerData.isRulerSet(ruler)){
                currentRuler = ruler;
                rulerData.setCurrentRuler(currentRuler);
                refreshRulerBackgroundBitmap();
                refreshImageRulerType();
                refreshRulerShadow();
                break;
            }
            rulersNum --;
        }
    }

    public void clickExit(View view){
        finish();
    }

    private void resetMeasureResult(){
        textMeasureResult.setText(resources.getString(R.string.text_measure_result_empty));
    }

    private void refreshTextInfo(){
        if(!rulerData.isRulerCalibrated()){
            textInfo.setText(resources.getString(R.string.info_ruler_not_calibrated));
        }

    }

    private void refreshImageRulerType(){
        imageRulerButton.setImageResource(getRulerButtonImageId());
    }

    private int getRulerButtonImageId(){
        if(currentRuler == Ruler.SCREEN){
            return R.drawable.phone_center;
        }else
            return currentRuler == Ruler.LEFT_PHONE_EDGE ? R.drawable.phone_left : R.drawable.phone_right;
    }

    private void refreshRulerShadow(){
        if(currentRuler == Ruler.SCREEN){
            imageShadowL.setVisibility(View.INVISIBLE);
            imageShadowR.setVisibility(View.INVISIBLE);
        }else if (currentRuler == Ruler.LEFT_PHONE_EDGE){
            imageShadowL.setVisibility(View.VISIBLE);
            imageShadowR.setVisibility(View.INVISIBLE);
        }else if (currentRuler == Ruler.RIGHT_PHONE_EDGE){
            imageShadowL.setVisibility(View.INVISIBLE);
            imageShadowR.setVisibility(View.VISIBLE);
        }
    }

    private void refreshImageUnitImage(){
        imageUnit.setImageResource(rulerData.isInInchMode() ? R.mipmap.ic_inch : R.mipmap.ic_cm);
    }
}
