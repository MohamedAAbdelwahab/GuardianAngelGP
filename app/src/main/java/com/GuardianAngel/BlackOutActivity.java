package com.GuardianAngel;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public class BlackOutActivity extends Activity {
    public WindowManager wm;
    public View myView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams windowManagerParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY  ,
                WindowManager.LayoutParams. FLAG_DIM_BEHIND, PixelFormat.TRANSLUCENT);

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        myView = inflater.inflate(R.layout.blackout_activity, null);

        wm.addView(myView, windowManagerParams);

    }
}
