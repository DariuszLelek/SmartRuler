package com.darodev.smartruler;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.darodev.smartruler.utility.OptionsProvider;

public class RulerActivity extends AppCompatActivity {
    private SharedPreferences.Editor editor;
    private OptionsProvider options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruler);

        Resources res = getResources();
        SharedPreferences prefs = getSharedPreferences(res.getString(R.string.app_name), Context.MODE_PRIVATE);

        options = new OptionsProvider(res, prefs);

        refreshImageUnitImage();
    }

    public void clickOptions(View view){

    }

    public void clickUnit(View view){
        options.swapInchMode();
        refreshImageUnitImage();
        // TODO refresh ruler mode
    }

    public void clickExit(View view){
        finish();
    }

    private void refreshImageUnitImage(){
        ImageView imageUnit = (ImageView) findViewById(R.id.image_unit);
        imageUnit.setImageResource(options.isInInchMode() ? R.drawable.inch : R.drawable.cm);
    }
}
