package com.GuardianAngel;
//After entering the password and validating it
//requestOverlayPermission asking for opening overlay permission on mobile phone or tablet

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import androidx.room.Room;

import com.GuardianAngel.FileSystemModule.AppDatabase;
import com.GuardianAngel.FileSystemModule.FileReader;
import com.GuardianAngel.FileSystemModule.Global;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public static  Date todDate;
    private ImageView face;
    private TextView faceText;
    public static String IP="192.168.1.9";
    AppDatabase db;
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
                    long diff = current_date.getTime() - startDate.getTime();
                    diff += previousTime;
                    totalDate = new Date(diff+totalDate.getTime());
                    startDate = new Date();
                    previousTime = 0;
                }
                long diff = current_date.getTime() - startDate.getTime();
                diff += previousTime;
                todDate = new Date(diff);
                long total = totalDate.getTime() + diff;
                long Days = total / (24 * 60 * 60 * 1000);
                long Hours = diff / (60 * 60 * 1000) % 24;
                long Minutes = diff / (60 * 1000) % 60;
                long Seconds = diff / 1000 % 60;
                long tHours = total / (60 * 60 * 1000) % 24;
                long tMinutes = total / (60 * 1000) % 60;
                long tSeconds = total / 1000 % 60;
                timer1TextView.setText(String.format("%02d:%02d:%02d", Hours,Minutes,Seconds));
                timer2TextView.setText(String.format("%02d:%02d:%02d:%02d",Days,tHours,tMinutes,tSeconds));

            }catch (Exception e) {
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
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name").build();

        timer1TextView = findViewById(R.id.textView19);
        timer2TextView = findViewById(R.id.textView20);
        ProtectiveDoneAllTime=findViewById(R.id.textView18);
        ProtectiveDoneToday=findViewById(R.id.textView16);
        face = findViewById(R.id.imageView10);
        faceText = findViewById(R.id.textView10);
        if(!(Settings.canDrawOverlays(this)))
            requestOverlayPermission();
        simpleSwitch = (SwitchCompat) findViewById(R.id.swOnOff);
        boolean my_service=isMyServiceRunning(ScreenCaptureService.class);

        final SharedPreferences pref = getSharedPreferences("YOUR_PREFERENCE_NAME", Context.MODE_PRIVATE);
        simpleSwitch.setChecked(my_service);
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
                Global.mailbox = item.isChecked();
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
                            case "Change Server Ip":
                                Intent Changeip=new Intent(getApplicationContext(),ChangeIpActivity.class);
                                startActivity(Changeip);
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
                Global.mailbox = item.isChecked();
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
                Intent i = com.GuardianAngel.ScreenCaptureService.getStartIntent(this, resultCode, data);
                i.putExtra("mail",Global.email);
                startService(i);
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
        Intent i = mProjectionManager.createScreenCaptureIntent();
        startActivityForResult(i, REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void stopProjection() {
        startService(com.GuardianAngel.ScreenCaptureService.getStopIntent(this));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        long total = 0;
        try{
            total = reader.CountStatsAllTime("statistics.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        long today = reader.CountStatsOFDay("statistics.txt");
        ProtectiveDoneAllTime.setText(String.valueOf(total));
        ProtectiveDoneToday.setText(String.valueOf(today));
        long totalMinutes = (totalDate.getTime()+todDate.getTime())/(1000*60);
        if (total != 0){
            double score = totalMinutes/(double)total;
            if(score > 60){
                face.setImageResource(R.drawable.good);
                faceText.setText("Good");
                faceText.setTextColor(Color.parseColor("#00C48C"));
            }else if(score > 10){
                face.setImageResource(R.drawable.average);
                faceText.setText("Average");
                faceText.setTextColor(Color.parseColor("#FFCF5C"));
            }else{
                face.setImageResource(R.drawable.bad);
                faceText.setText("Bad");
                faceText.setTextColor(Color.parseColor("#FF647C"));
            }
        }



    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        timerHandler.removeCallbacks(runnable);
        SharedPreferences.Editor editor = getSharedPreferences("YOUR_PREFERENCE_NAME", Context.MODE_PRIVATE).edit();
        if(simpleSwitch.isChecked()) {
            editor.putLong("startTime", startDate.getTime());
        }else{
            editor.remove("startTime");
        }
        writeTime();
        editor.apply();
    }
    public void readTime(){
        Pair<Long,Long> time = reader.readTime();
        long t = time.first+time.second;
        todDate = new Date(time.first);
        long Days = t / (24 * 60 * 60 * 1000);
        long Hours = time.first / (60 * 60 * 1000) % 24;
        long Minutes = time.first / (60 * 1000) % 60;
        long Seconds = time.first / 1000 % 60;
        long tHours = t / (60 * 60 * 1000) % 24;
        long tMinutes = t / (60 * 1000) % 60;
        long tSeconds = t / 1000 % 60;
        timer1TextView.setText(String.format("%02d:%02d:%02d", Hours,Minutes,Seconds));
        timer2TextView.setText(String.format("%02d:%02d:%02d:%02d",Days,tHours,tMinutes,tSeconds));
        totalDate = new Date(time.second);
        if(time.first==0){
            todDate = new Date(0);
            previousTime = 0;
        }
    }
    public void writeTime(){
        SimpleDateFormat format=new SimpleDateFormat("yyyy/MM/dd");

//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
//        LocalDateTime now = LocalDateTime.now();
        Date c = Calendar.getInstance().getTime();

        reader.writeTime(this,""+todDate.getTime()+" "+format.format(c),""+totalDate.getTime());
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
