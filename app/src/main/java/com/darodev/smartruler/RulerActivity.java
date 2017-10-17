package com.darodev.smartruler;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.darodev.smartruler.ruler.Ruler;
import com.darodev.smartruler.ruler.RulerBitmapProvider;
import com.darodev.smartruler.utility.RulerData;
import com.darodev.smartruler.utility.Unit;

public class RulerActivity extends AppCompatActivity {
    private RulerData rulerData;
    private ImageView imageInfo, imageRuler;
    private TextView[] textSavedData;
    private TextView textMeasureResult;
    private Resources resources;
    private RulerBitmapProvider rulerBitmapProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruler);

        resources = getResources();
        SharedPreferences prefs = getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);

        imageRuler = (ImageView) findViewById(R.id.image_ruler);
        imageInfo = (ImageView) findViewById(R.id.image_info);
        textMeasureResult = (TextView) findViewById(R.id.text_measure_result);
        rulerData = new RulerData(resources, prefs, metrics);

        refreshImageUnitImage();
        showInfoScreen();
        refreshSavedData();

        prepareImageRulerBitmap(imageRuler);
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

    private void showInfoScreen(){
        // TODO at first run
//        if(!rulerData.isCalibrated()){
//            imageInfo.setVisibility(View.VISIBLE);
//        }
    }

    public void clickInfo(View view){
        imageInfo.setVisibility(View.INVISIBLE);
    }

    public void clickOptions(View view){

    }

    public void clickUnit(View view){
        rulerData.swapInchMode();
        refreshImageUnitImage();
        refreshRulerBitmap();
    }

    public void clickSave(View view){
        rulerData.saveMeasureResult(textMeasureResult.getText().toString());
        textMeasureResult.setText(resources.getString(R.string.text_measure_result_empty));
        refreshSavedData();
    }

    private void prepareImageRulerBitmap(final ImageView imageRulerView){
        imageRulerView.post(new Runnable() {
            @Override
            public void run() {
                imageRulerView.setDrawingCacheEnabled(true);
                imageRulerView.buildDrawingCache();
                prepareRulerBitmapProvider(imageRulerView.getDrawingCache());
            }
        });
    }

    private void prepareRulerBitmapProvider(Bitmap imageRulerBitmap){
        rulerBitmapProvider = new RulerBitmapProvider(imageRulerBitmap, rulerData);

        for(Ruler ruler : Ruler.values()){
            if(rulerData.isRulerSet(ruler)){
                rulerBitmapProvider.prepareRulers(ruler);
            }
        }

        refreshRulerBitmap();
    }

    private void refreshRulerBitmap(){
        if(rulerData.isInInchMode()){
            imageRuler.setImageBitmap(rulerBitmapProvider.getRulerBitmap(Unit.INCH));
        }else{
            imageRuler.setImageBitmap(rulerBitmapProvider.getRulerBitmap(Unit.CM));
        }
    }

    public void clickRulerType(View view){

    }

    public void clickExit(View view){
        finish();
    }

    private void refreshImageUnitImage(){
        ImageView imageUnit = (ImageView) findViewById(R.id.image_unit);
        imageUnit.setImageResource(rulerData.isInInchMode() ? R.mipmap.ic_inch : R.mipmap.ic_cm);
    }
}
