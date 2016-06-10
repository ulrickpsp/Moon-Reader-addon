package com.example.aaldridge.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aaldridge on 09/06/2016.
 */
public class DisplayClass {

    private int xBack, yBack, xNext, yNext;


    private static DisplayClass ourInstance = null;

    public static DisplayClass getInstance() {
        if(ourInstance == null)
            ourInstance = new DisplayClass();
        return ourInstance;
    }
    private DisplayClass() {
    }

    public List<Integer> getCoordinates(){

        ArrayList<Integer> coordinates = new ArrayList<>();
        coordinates.add(xBack);
        coordinates.add(yBack);
        coordinates.add(xNext);
        coordinates.add(yNext);

        return coordinates;
    }

    public void updateCoordinates(Context context) {

        //Get Display Size
        WindowManager window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = window.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        //Get Back coordinates
        xBack = width / 6;              //Any value lower than width / 3 should be valid
        yBack = height / 2;             // It doesn't matter. Half height is OK

        //Get next coordinates
        xNext = width - (width / 6);    //Any value lower than width / 3 should be valid
        yNext = height / 2;             // It doesn't matter. Half height is OK
    }


    //================================================================
    //                     STATIC METHODS
    //================================================================
    private static int checkAutoRotation(Context myContext){

        return Settings.System.getInt(myContext.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
    }

    public static void setAutoRotation(Context myContext) {

        if (checkAutoRotation(myContext) == 0)
            Settings.System.putInt(myContext.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
    }
}
