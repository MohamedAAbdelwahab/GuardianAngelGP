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
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;

import com.GuardianAngel.FileSystemModule.FileReader;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeActivity extends Activity {
    private static final int REQUEST_CODE = 100;
    private MenuItem item;
    private SwitchCompat simpleSwitch;
    static Boolean isTouched = false;
    FileReader reader;
    TextView ProtectiveDoneAllTime;
    TextView ProtectiveDoneToday;
    static TextView timer1TextView;
    static TextView timer2TextView;
    public static long previousTime;
    public static Date startDate;
    public static  Date totalDate;
    //runs without a timer by reposting this handler at the end of the runnable
   static Handler timerHandler = new Handler();
    static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {

                timerHandler.postDelayed(this, 1000);
                Date current_date = new Date();
                Calendar cal1 = Calendar.getInstance();
                Calendar cal2 = Calendar.getInstance();
                cal1.setTime(startDate);
                cal2.setTime(current_date);
                if (!(cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                        cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))){
                    startDate = new Date();
                    previousTime = 0;
                }
                long diff = current_date.getTime() - startDate.getTime();
                diff += previousTime;
                long total = totalDate.getTime() + diff;
                long Days = diff / (24 * 60 * 60 * 1000);
                long Hours = diff / (60 * 60 * 1000) % 24;
                long Minutes = diff / (60 * 1000) % 60;
                long Seconds = diff / 1000 % 60;
                long tHours = total / (60 * 60 * 1000) % 24;
                long tMinutes = total / (60 * 1000) % 60;
                long tSeconds = total / 1000 % 60;
                timer1TextView.setText(String.format("%02d:%02d:%02d", Hours,Minutes,Seconds));
                timer2TextView.setText(String.format("%02d:%02d:%02d:%02d",Days,tHours,tMinutes,tSeconds));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        reader=new FileReader(this);
        timer1TextView = findViewById(R.id.textView19);
        timer2TextView = findViewById(R.id.textView20);
        ProtectiveDoneAllTime=findViewById(R.id.textView18);
        ProtectiveDoneToday=findViewById(R.id.textView16);
        if(!(Settings.canDrawOverlays(this)))
            requestOverlayPermission();
        simpleSwitch = (SwitchCompat) findViewById(R.id.swOnOff);
        final SharedPreferences pref = getSharedPreferences("YOUR_PREFERENCE_NAME", Context.MODE_PRIVATE);
        simpleSwitch.setChecked(pref.getBoolean("isChecked", false));
        if(simpleSwitch.isChecked()){
            timerHandler.postDelayed(runnable, 0);
        }
        startDate = new Date(pref.getLong("startTime",(new Date()).getTime()));
        previousTime = pref.getLong("previousTime",0);
        readTime();
        simpleSwitch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isTouched = true;
                return false;
            }
        });


        try {
            ProtectiveDoneAllTime.setText(String.valueOf(reader.CountStatsAllTime("statistics.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ProtectiveDoneToday.setText(String.valueOf(reader.CountStatsOFDay("statistics.txt")));

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    SharedPreferences.Editor editor = getSharedPreferences("YOUR_PREFERENCE_NAME", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("isChecked", b);
                    editor.apply();
//                    if(startDate == null){
//                        startDate = new Date();
//                    }
//                    timerHandler.postDelayed(runnable, 0);
                    startProjection();
                }
                else if(!b && isTouched){
                    isTouched = false;
                    compoundButton.setChecked(true);
                    Intent entrpwd = new Intent(getApplicationContext(),enterPwdPopup.class);
                    startActivityForResult(entrpwd,300);
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
                item = men.getMenu().findItem(R.id.app_bar_switch);
                item.setChecked(pref.getBoolean("Checked", true));
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
                                if(item.getItemId() == R.id.app_bar_switch){
                                    Intent entrpwd = new Intent(getApplicationContext(),enterPwdPopup.class);
                                    startActivityForResult(entrpwd,200);
                                }
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

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        }else if(requestCode==200 && resultCode == RESULT_OK){
            if(data.getIntExtra("SUCCESS",0) == 100){
                item.setChecked(!item.isChecked());
                SharedPreferences.Editor editor = getSharedPreferences("YOUR_PREFERENCE_NAME", Context.MODE_PRIVATE).edit();
                editor.putBoolean("Checked", item.isChecked());
                editor.apply();
            }
        }else if(requestCode==300 && resultCode == RESULT_OK){
            simpleSwitch.setChecked(false);
            SharedPreferences.Editor editor = getSharedPreferences("YOUR_PREFERENCE_NAME", Context.MODE_PRIVATE).edit();
            editor.putBoolean("isChecked", false);
            timerHandler.removeCallbacks(runnable);
            Date current_date = new Date();
            previousTime += current_date.getTime() - startDate.getTime();
            editor.putLong("previousTime", previousTime);
            startDate = null;
            editor.apply();
            stopProjection();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void stopProjection() {
        startService(com.GuardianAngel.ScreenCaptureService.getStopIntent(this));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        try {
            ProtectiveDoneAllTime.setText(String.valueOf(reader.CountStatsAllTime("statistics.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ProtectiveDoneToday.setText(String.valueOf(reader.CountStatsOFDay("statistics.txt")));

    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onStop(){
        super.onStop();
        timerHandler.removeCallbacks(runnable);
        SharedPreferences.Editor editor = getSharedPreferences("YOUR_PREFERENCE_NAME", Context.MODE_PRIVATE).edit();
        if(simpleSwitch.isChecked()) {
            editor.putLong("startTime", startDate.getTime());
        }else{
            editor.remove("startTime");
        }
        reader.writeTime(this,timer1TextView.getText().toString(),timer2TextView.getText().toString());
        editor.apply();
    }
    public void readTime(){
        Pair<String,String> time = reader.readTime();
        timer1TextView.setText(time.first);
        timer2TextView.setText(time.second);
        String[] total = time.second.split(":");
        int days  = Integer.parseInt(total[0]);
        int hours  = Integer.parseInt(total[1]);
        int minutes = Integer.parseInt(total[2]);
        int seconds = Integer.parseInt(total[3]);
        long t = seconds + 60 * minutes + 3600 * hours + 24 * 3600 *days;
        totalDate = new Date(t);

    }
}
