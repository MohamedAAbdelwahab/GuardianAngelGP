package com.GuardianAngel;
//After entering the password and validating it
//requestOverlayPermission asking for opening overlay permission on mobile phone or tablet

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.annotation.RequiresApi;

public class HomeActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        ImageView v = findViewById(R.id.stn_btn);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context wrapper = new ContextThemeWrapper(HomeActivity.this, R.style.MyPopupOtherStyle);
                PopupMenu men = new PopupMenu(wrapper,v);
                men.inflate(R.menu.settings_menu);
                men.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getTitle().toString()){
                            case "Change Email":
                                Intent chngmail = new Intent(getApplicationContext(),changeEmailPopup.class);
                                startActivity(chngmail);
                                break;
                            case "Change Password":
                                Intent chngpwd = new Intent(getApplicationContext(),changePwdPopup.class);
                                startActivity(chngpwd);
                                break;
                            case "Receive Emails":
                                Intent entrpwd = new Intent(getApplicationContext(),enterPwdPopup.class);
                                startActivity(entrpwd);
                                break;
                            case "Language":
                                break;
                            default:
                                break;
                        }
                        return true;
                    }

                });
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
        if(!(Settings.canDrawOverlays(this)))
        requestOverlayPermission();
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

    private void requestOverlayPermission() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            return;
        }

        Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        myIntent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(myIntent, 500);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
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
    }
    */
}
