package com.darodev.smartruler;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.darodev.smartruler.ruler.MeasureOrigin;
import com.darodev.smartruler.ruler.Ruler;
import com.darodev.smartruler.ruler.RulerMeasure;

import org.joda.time.DateTime;

public class CalibrateActivity extends AppCompatActivity {
    public static final int CALIBRATION_REQUEST_CODE = 1234;
    public static final int SCREEN_OFFSET_PIXELS = 0;

    private ImageView imageCalibrate;
    private RulerMeasure rulerMeasure;
    private int screenMeasurePointX;
    private TextView textInfoCalibrate;
    private Resources resources;
    private Ruler ruler;
    private LinearLayout layout_components;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate);

        resources = getResources();

        imageCalibrate = (ImageView) findViewById(R.id.image_calibrate);
        textInfoCalibrate = (TextView) findViewById(R.id.text_info_calibrate);
        layout_components = (LinearLayout) findViewById(R.id.layout_components);

        String rulerName = getIntent().getStringExtra(resources.getString(R.string.ruler_key));
        ruler = Ruler.getByString(rulerName);

        prepareLayout();
        prepareCalibrationInfo(ruler);
        prepareRulerMeasure();
        prepareListener();
    }

    private void prepareLayout(){
        if(ruler == Ruler.RIGHT_PHONE_EDGE){
            layout_components.setGravity(Gravity.START);
        }
    }

    private void prepareCalibrationInfo(Ruler ruler){
        if(ruler == Ruler.SCREEN){
            textInfoCalibrate.setText(resources.getString(R.string.info_calibrate_screen));
            imageCalibrate.setBackgroundResource(R.drawable.info_screen);
        }else if(ruler == Ruler.LEFT_PHONE_EDGE){
            textInfoCalibrate.setText(resources.getString(R.string.info_calibrate_left));
            imageCalibrate.setBackgroundResource(R.drawable.info_left);
        }else if(ruler == Ruler.RIGHT_PHONE_EDGE){
            textInfoCalibrate.setText(resources.getString(R.string.info_calibrate_right));
            imageCalibrate.setBackgroundResource(R.drawable.info_right);
        }
    }

    private void prepareRulerMeasure(){
        imageCalibrate.post(new Runnable() {
            @Override
            public void run() {
                imageCalibrate.setDrawingCacheEnabled(true);
                imageCalibrate.buildDrawingCache();
                Bitmap drawingCacheBitmap = imageCalibrate.getDrawingCache();

                rulerMeasure = new RulerMeasure(drawingCacheBitmap, getApplicationContext(), SCREEN_OFFSET_PIXELS);
            }
        });
    }

    private void prepareListener(){
        imageCalibrate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE){
                    if(rulerMeasure != null && rulerMeasure.canDrawNewMeasure(DateTime.now())){
                        rulerMeasure.setLastMeasureTime();
                        int pointX = Math.round(event.getX());
                        setMeasureBitmap(imageCalibrate, MeasureOrigin.CALIBRATION_SCREEN, pointX);
                        screenMeasurePointX = pointX;
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void setMeasureBitmap(ImageView imageView, MeasureOrigin origin, int pointX){
        Bitmap bitmap = rulerMeasure.getMeasureBitmap(origin, pointX, ruler);
        imageView.setImageBitmap(bitmap);
    }

    public void clickDone(View view){
        finishActivity();
    }

    public void clickCancel(View view){
        //setResult(Activity.RESULT_OK, new Intent());
        finish();
    }

    private void finishActivity(){
        Intent data = new Intent();
        data.putExtra(resources.getString(R.string.calibration_result_key), String.valueOf(screenMeasurePointX));
        setResult(Activity.RESULT_OK, data);
        finish();
    }
}
