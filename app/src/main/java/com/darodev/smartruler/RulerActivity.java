package com.darodev.smartruler;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.darodev.smartruler.utility.RulerData;

public class RulerActivity extends AppCompatActivity {
    private RulerData rulerData;
    private ImageView imageInfo;
    private TextView[] textSavedData;
    private TextView textMeasureResult;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruler);

        resources = getResources();
        SharedPreferences prefs = getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE);

        rulerData = new RulerData(resources, prefs);
        imageInfo = (ImageView) findViewById(R.id.image_info);
        textMeasureResult = (TextView) findViewById(R.id.text_measure_result);

        refreshImageUnitImage();
        showInfoScreen();
        refreshSavedData();
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
        if(!rulerData.isCalibrated()){
            imageInfo.setVisibility(View.VISIBLE);
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
        // TODO refresh ruler mode or not (if added second ruler below)
    }

    public void clickSave(View view){
        rulerData.saveMeasureResult(textMeasureResult.getText().toString());
        textMeasureResult.setText(resources.getString(R.string.text_measure_result_empty));
        refreshSavedData();
    }

    public void clickExit(View view){
        finish();
    }

    private void refreshImageUnitImage(){
        ImageView imageUnit = (ImageView) findViewById(R.id.image_unit);
        imageUnit.setImageResource(rulerData.isInInchMode() ? R.mipmap.ic_inch : R.mipmap.ic_cm);
    }
}
