package com.darodev.smartruler;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.darodev.smartruler.ruler.MeasureOrigin;
import com.darodev.smartruler.ruler.Ruler;
import com.darodev.smartruler.ruler.RulerBitmapProvider;
import com.darodev.smartruler.ruler.RulerMeasure;
import com.darodev.smartruler.utility.RulerData;
import com.darodev.smartruler.utility.Unit;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.joda.time.DateTime;

import java.util.Locale;

import static android.R.attr.button;

public class RulerActivity extends AppCompatActivity {
    private AdView adView;
    private RulerData rulerData;
    private RulerMeasure rulerScreenMeasure, rulerCalibrateMeasure;
    private ImageView imageRuler, imageCalibrate;
    private TextView[] textSavedData;
    private TextView textMeasureResult, textInfo, textInfoCalibrate;
    private Resources resources;
    private RulerBitmapProvider rulerBitmapProvider;
    private Ruler currentRuler;
    private ImageView imageUnit, imageRulerButton, imageShadowL, imageShadowR;
    private int screenMeasurePointX;
    private Button btnDoneLeft, btnDoneRight;
    private FrameLayout calibrateScreen;
    private boolean isCalibrateMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruler);

        //adView = getAdView();

        resources = getResources();
        SharedPreferences prefs = getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);


        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(resources.getString(R.string.pixels_to_left_edge_key), 300);
        editor.putInt(resources.getString(R.string.pixels_to_right_edge_key), 300);
        editor.apply();

        isCalibrateMode = false;
        imageRuler = (ImageView) findViewById(R.id.image_ruler);
        imageUnit = (ImageView) findViewById(R.id.image_unit);
        imageRulerButton = (ImageView) findViewById(R.id.image_ruler_button);
        imageShadowL = (ImageView) findViewById(R.id.image_shadow_left);
        imageShadowR = (ImageView) findViewById(R.id.image_shadow_right);
        textMeasureResult = (TextView) findViewById(R.id.text_measure_result);
        textInfo = (TextView) findViewById(R.id.text_info);
        textInfoCalibrate = (TextView) findViewById(R.id.text_info_calibrate);
        imageCalibrate = (ImageView) findViewById(R.id.image_calibrate);
        calibrateScreen = (FrameLayout) findViewById(R.id.layout_calibrate_screen);
        btnDoneLeft = (Button) findViewById(R.id.btn_done_left);
        btnDoneRight = (Button) findViewById(R.id.btn_done_right);

        rulerData = new RulerData(resources, prefs, metrics);
        currentRuler = rulerData.getCurrentRuler();

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/digital-7.ttf");
        textMeasureResult.setTypeface(typeface);

        refreshImageRulerType();
        refreshImageUnitImage();
        refreshRulerShadow();
        refreshTextInfo();
        refreshSavedData();

        prepareImageRulerBitmap();
        prepareImageRulerListener();
        prepareCalibrateFeature();

        //prepareAds();
    }

    private void prepareAds(){
        LinearLayout add_holder = (LinearLayout) findViewById(R.id.layout_ad);
        add_holder.addView(getAdView());
    }

    private void prepareCalibrateFeature(){
        imageCalibrate.post(new Runnable() {
            @Override
            public void run() {
                imageCalibrate.setDrawingCacheEnabled(true);
                imageCalibrate.buildDrawingCache();
                Bitmap drawingCacheBitmap = imageCalibrate.getDrawingCache();

                prepareRulerMeasure(MeasureOrigin.CALIBRATION_SCREEN, drawingCacheBitmap);
            }
        });

        imageCalibrate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

//                if(action == MotionEvent.ACTION_DOWN){
//                    showDoneButton().setText(resources.getString(R.string.btn_done_text));
//                    return false;
//                }

                if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE){
                    if(rulerCalibrateMeasure != null && rulerCalibrateMeasure.canDrawNewMeasure(DateTime.now())){
                        rulerCalibrateMeasure.setLastMeasureTime();
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

    private AdView getAdView(){
        if(adView == null){
            MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.app_id));

            RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            adParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

            adView = new AdView(getApplicationContext());
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId(getResources().getString(R.string.add_unit_id));
            adView.setBackgroundColor(Color.TRANSPARENT);
            adView.setLayoutParams(adParams);

            // Test Ads
            // AdRequest adRequest = new AdRequest.Builder().addTestDevice(getResources().getString(R.string.test_device_id)).build();
            AdRequest adRequest = new AdRequest.Builder().build();

            adView.loadAd(adRequest);
        }

        return adView;
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

    public void clickOptions(View view){
        isCalibrateMode = true;
        screenMeasurePointX = 0;

        calibrateScreen.setVisibility(View.VISIBLE);
        showCalibrateInfo(rulerData.getCurrentRuler());
        showDoneButton(rulerData.getCurrentRuler());
    }

    private void showCalibrateInfo(Ruler ruler){
        if(ruler == Ruler.SCREEN){
            textInfoCalibrate.setText(resources.getString(R.string.info_calibrate_screen));
            imageCalibrate.setImageResource(R.drawable.info_screen);
        }else if(ruler == Ruler.LEFT_PHONE_EDGE){
            textInfoCalibrate.setText(resources.getString(R.string.info_calibrate_left));
            imageCalibrate.setImageResource(R.drawable.info_left);
        }else if(ruler == Ruler.RIGHT_PHONE_EDGE){
            textInfoCalibrate.setText(resources.getString(R.string.info_calibrate_right));
            imageCalibrate.setImageResource(R.drawable.info_right);
        }
    }

    private void showDoneButton(Ruler ruler){
        if(ruler == Ruler.RIGHT_PHONE_EDGE){
            btnDoneLeft.setVisibility(View.VISIBLE);
            btnDoneRight.setVisibility(View.INVISIBLE);
        }else{
            btnDoneLeft.setVisibility(View.INVISIBLE);
            btnDoneRight.setVisibility(View.VISIBLE);
        }
    }

    public void clickDone(View view){
        isCalibrateMode = false;
        calibrateScreen.setVisibility(View.INVISIBLE);
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

    private void prepareImageRulerListener(){
        imageRuler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!isCalibrateMode){
                    int action = event.getAction();
                    if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE){
                        if(rulerScreenMeasure != null && rulerScreenMeasure.canDrawNewMeasure(DateTime.now())){
                            rulerScreenMeasure.setLastMeasureTime();
                            int pointX = Math.round(event.getX());
                            setMeasureBitmap(imageRuler, MeasureOrigin.RULER_SCREEN, pointX);
                            setMeasureResult(pointX);
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void setMeasureBitmap(ImageView imageView, MeasureOrigin origin, int pointX){
        RulerMeasure rulerMeasure = origin == MeasureOrigin.RULER_SCREEN ? rulerScreenMeasure : rulerCalibrateMeasure;
        Bitmap bitmap = rulerMeasure.getMeasureBitmap(origin, pointX, currentRuler);
        imageView.setImageBitmap(bitmap);
    }

    private void setMeasureResult(int pointX){
        float result = rulerData.getMeasureResult(pointX, currentRuler, rulerBitmapProvider);
        textMeasureResult.setText(getFormattedResult(result));
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
                prepareRulerMeasure(MeasureOrigin.RULER_SCREEN, drawingCacheBitmap);
            }
        });
    }

    private void prepareRulerMeasure(MeasureOrigin origin, Bitmap bitmap){
        if(origin == MeasureOrigin.RULER_SCREEN){
            rulerScreenMeasure = new RulerMeasure(bitmap, rulerData, getApplicationContext());
        }else if(origin == MeasureOrigin.CALIBRATION_SCREEN){
            rulerCalibrateMeasure = new RulerMeasure(bitmap, rulerData, getApplicationContext());
        }
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
        currentRuler = Ruler.getNextRuler(currentRuler);
        rulerData.setCurrentRuler(currentRuler);
        refreshRulerBackgroundBitmap();
        refreshImageRulerType();
        refreshRulerShadow();
    }

    private void resetMeasureResult(){
        textMeasureResult.setText(resources.getString(R.string.text_measure_result_empty));
    }

    private void refreshTextInfo(){
        if(!rulerData.isScreenRulerCalibrated()){
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
