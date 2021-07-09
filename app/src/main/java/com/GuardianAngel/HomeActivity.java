package com.GuardianAngel;
//After entering the password and validating it
//requestOverlayPermission asking for opening overlay permission on mobile phone or tablet

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;

public class HomeActivity extends Activity {
    private static final int REQUEST_CODE = 100;

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home);

        if(!(Settings.canDrawOverlays(this)))
            requestOverlayPermission();
        SwitchCompat simpleSwitch = (SwitchCompat) findViewById(R.id.swOnOff);
        SharedPreferences pref = getSharedPreferences("YOUR_PREFERENCE_NAME", Context.MODE_PRIVATE);
        simpleSwitch.setChecked(pref.getBoolean("isChecked", false));

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    SharedPreferences.Editor editor = getSharedPreferences("YOUR_PREFERENCE_NAME", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("isChecked", b);
                    editor.apply();
                    startProjection();
                }
                else{
                    SharedPreferences.Editor editor = getSharedPreferences("YOUR_PREFERENCE_NAME", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("isChecked", b);
                    editor.apply();
                    stopProjection();
                }
            }
        });
        ImageView v = findViewById(R.id.stn_btn);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context wrapper = new ContextThemeWrapper(HomeActivity.this, R.style.MyPopupOtherStyle);
                PopupMenu men = new PopupMenu(wrapper,v);
                men.inflate(R.menu.settings_menu);
                men.show();
            }
        });
    }
    private void showMenu(View v){


    }
    /*Button Cap_Activity;
    Button Change_Settings;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
//        String urlString = "chrome://flags/";
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse((urlString)));
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setPackage("com.android.chrome");
//        try {
//            startActivity(intent);
//        } catch (ActivityNotFoundException ex) {
//            // Chrome browser presumably not installed so allow user to choose instead
//            intent.setPackage(null);
//            startActivity(intent);
//        }

        Cap_Activity=findViewById(R.id.ScreenCapActivityBtn);
        Change_Settings=findViewById(R.id.ChangeSettings);
        Cap_Activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ScreenCaptureActivity.class);
                startActivity(i);
            }
        });
        Change_Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ChangeSettings.class);
                startActivity(i);
            }
        });



        // start projection

        // stop projection

    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override

    */
    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==500) {
            if (Settings.canDrawOverlays(this))
            {
                Toast.makeText(this,"Premission Granted",Toast.LENGTH_LONG).show();
            }
            else {
                requestOverlayPermission();
            }
        }
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                startService(com.GuardianAngel.ScreenCaptureService.getStartIntent(this, resultCode, data));
            }
        }
    }
    private void requestOverlayPermission() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            return;
        }

        Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        myIntent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(myIntent, 500);

    }
    private void startProjection() {
        MediaProjectionManager mProjectionManager =
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
    }

    private void stopProjection() {
        startService(com.GuardianAngel.ScreenCaptureService.getStopIntent(this));
    }
}
