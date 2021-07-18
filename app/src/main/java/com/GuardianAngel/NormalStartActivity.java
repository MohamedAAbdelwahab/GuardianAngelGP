package com.GuardianAngel;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.GuardianAngel.FileSystemModule.FileReader;

import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;


//The activity that opens everytime the user taps on the application icon to verify his password and let him into the parent mode



public class NormalStartActivity extends Activity {
    Button submit;
    EditText password;
    FileReader file;
    Context context;
    PasswordHash hasher=new PasswordHash();
    private static final String PasswordFileName="PasswordFile.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.normal_start_activity);

        context=this;
        file=new FileReader(this);
        submit=findViewById(R.id.log_btn);
        password=findViewById(R.id.password_box);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        final SharedPreferences  pref = this.getSharedPreferences("Sample", Context.MODE_PRIVATE);
        final int[] no_attempt = {pref.getInt("ATTEMPTs", 0)};
//        Log.i("nom attempts", String.valueOf(no_attempt[0]));
        if(no_attempt[0] >5)
        {
            submit.setEnabled(false);
        }
        Timer timer = new Timer();

        timer.schedule( new TimerTask() {
            public void run() {
//                Log.i("TimeOFLOCK", String.valueOf(pref.getLong("TimeofLock",0)));
//                Log.i("TimeNOW", String.valueOf(System.currentTimeMillis()));
//                Log.i("Second Condition", String.valueOf((System.currentTimeMillis()-pref.getLong("TimeofLock",0)) > 3000));
//                Log.i("First condition", String.valueOf(no_attempt[0]>=5));
//                Log.i("NomOFATTEM",String.valueOf(no_attempt[0]));
                no_attempt[0]=pref.getInt("ATTEMPTs",0);
                if(no_attempt[0]>5 && (System.currentTimeMillis()-pref.getLong("TimeofLock",0)) > 60000 && pref.getLong("TimeofLock",0) != 0)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            submit.setEnabled(true);
                        }
                    });

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("ATTEMPTs",0);
                    editor.apply();
                }

            }
        }, 0, 1000);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit.setEnabled(false);
                password.setEnabled(false);
                final String ExpectedPassword=password.getText().toString();
                final String ActualPassword=file.ReadFile(context,PasswordFileName);
                if(no_attempt[0] >=5)
                {
                    submit.setEnabled(false);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putLong("TimeofLock",System.currentTimeMillis());
                    editor.apply();

                }

//                Log.i("nom attempts", String.valueOf(no_attempt[0]));
                Thread thread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(hasher.checkPassword(ExpectedPassword,ActualPassword))
                        {
                            Intent i = new Intent(getApplicationContext(), HomeActivity.class); ///// edit
                            startActivity(i);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    password.setEnabled(true);
                                    submit.setEnabled(true);
                                }
                            });

                            finish();
                        }
                        else
                        {
                            SharedPreferences.Editor editor = pref.edit();
                            final int temp = ++no_attempt[0];
                            editor.putInt("ATTEMPTs",temp);
                            no_attempt[0]=temp;
                            editor.apply();
//                            Toast.makeText(getApplicationContext(),"Wrong Password .. please check your password again,you have"+(6-temp)+" left",Toast.LENGTH_LONG).show();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    password.setEnabled(true);
                                    submit.setEnabled(true);
                                    Toast.makeText(getApplicationContext(),"Wrong Password .. please check your password again,you have"+(6-temp)+" left",Toast.LENGTH_LONG).show();

                                }
                            });

                        }
                    }
                });
                thread.start();



            }
        });
        // start projection

        // stop projection

    }
}
