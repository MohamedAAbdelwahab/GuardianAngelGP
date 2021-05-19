package com.GuardianAngel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class ScreenCaptureActivity extends Activity {
    Button Submit;
    EditText password;
    private static final int REQUEST_CODE = 100;



    /****************************************** Activity Lifecycle methods ************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // start projection
        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startProjection();
            }
        });
        Submit=findViewById(R.id.button5);
        password=findViewById(R.id.PasswordEditTextInMonitor);
        // stop projection
        final Button stopButton = findViewById(R.id.stopButton);
        Submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                stopButton.setEnabled(true);
            }
        });
        stopButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {


                stopProjection();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                startService(com.GuardianAngel.ScreenCaptureService.getStartIntent(this, resultCode, data));
            }
        }
    }

    /****************************************** UI Widget Callbacks *******************************/
    private void startProjection() {
        MediaProjectionManager mProjectionManager =
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
    }

    private void stopProjection() {
        startService(com.GuardianAngel.ScreenCaptureService.getStopIntent(this));
    }


}