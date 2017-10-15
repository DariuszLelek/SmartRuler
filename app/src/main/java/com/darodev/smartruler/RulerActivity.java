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
    private SharedPreferences.Editor editor;
    private RulerData rulerData;
    private ImageView imageInfo;
    private TextView[] textSavedData;
    private TextView textMeasureResult;

    int saveTest = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruler);

        Resources res = getResources();
        SharedPreferences prefs = getSharedPreferences(res.getString(R.string.app_name), Context.MODE_PRIVATE);

        rulerData = new RulerData(res, prefs);
        imageInfo = (ImageView) findViewById(R.id.image_info);
        textMeasureResult = (TextView) findViewById(R.id.text_measure_result);

        refreshImageUnitImage();
        showInfoScreen();
        showSavedData();
    }

    private TextView[] getTextSaveViews(){
        if(textSavedData == null){
            int numOfSavedDataFields = 5;
            textSavedData = new TextView[numOfSavedDataFields];

            textSavedData[0] = (TextView) findViewById(R.id.text_save_1);
            textSavedData[1] = (TextView) findViewById(R.id.text_save_2);
            textSavedData[2] = (TextView) findViewById(R.id.text_save_3);
            textSavedData[3] = (TextView) findViewById(R.id.text_save_4);
            textSavedData[4] = (TextView) findViewById(R.id.text_save_5);
        }

        return textSavedData;
    }

    private void showSavedData() {
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
        // TODO refresh ruler mode
    }

    public void clickSave(View view){

        rulerData.saveMeasureResult(saveTest++ + "");
        showSavedData();
//        rulerData.saveMeasureResult(textMeasureResult.getText().toString());
    }

    public void clickExit(View view){
        finish();
    }

    private void refreshImageUnitImage(){
        ImageView imageUnit = (ImageView) findViewById(R.id.image_unit);
        imageUnit.setImageResource(rulerData.isInInchMode() ? R.drawable.inch : R.drawable.cm);
    }
}
