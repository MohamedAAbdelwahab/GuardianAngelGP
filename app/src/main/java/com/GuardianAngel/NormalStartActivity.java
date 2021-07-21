package com.GuardianAngel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.room.Room;

import com.GuardianAngel.FileSystemModule.AppDatabase;
import com.GuardianAngel.FileSystemModule.Global;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


//The activity that opens everytime the user taps on the application icon to verify his password and let him into the parent mode



public class NormalStartActivity extends Activity {
    Button submit;
    EditText password;
    PasswordHash hasher=new PasswordHash();
    AppDatabase db;
    TextView forgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.normal_start_activity);
        final String[] ActualPassword = {""};

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name").build();

        Thread thread2=new Thread(new Runnable() {
            @Override
            public void run() {
                Global.email = db.userDao().GetUserEmail();
                ActualPassword[0] =db.userDao().GetUserPassword();

            }
        });
            thread2.start();
        try {
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        submit=findViewById(R.id.log_btn);
        password=findViewById(R.id.password_box);
        forgetPassword=findViewById(R.id.textView8);
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),PasswordRestore.class);
                startActivity(intent);
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        final SharedPreferences  pref = this.getSharedPreferences("Sample", Context.MODE_PRIVATE);
        final int[] no_attempt = {pref.getInt("ATTEMPTs", 0)};
        if(no_attempt[0] >5)
        {
            submit.setEnabled(false);
        }
        Timer timer = new Timer();

        timer.schedule( new TimerTask() {
            public void run() {
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

                        if(hasher.checkPassword(ExpectedPassword, ActualPassword[0]))
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
                        if(no_attempt[0] >5)
                        {
                            submit.setEnabled(false);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putLong("TimeofLock",System.currentTimeMillis());
                            editor.apply();
                            MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                            multipartBodyBuilder.addFormDataPart("receiver", Global.email);
                            multipartBodyBuilder.addFormDataPart("APIpwd", "94Vtwn0iHVQSpBT");
                            RequestBody postBody = multipartBodyBuilder.build();
                            OkHttpClient okHttpClient = new OkHttpClient();
                            Request request = new Request.Builder().url("http://"+HomeActivity.IP+":8000/warn").post(postBody).build();
                            okHttpClient.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),"server error",Toast.LENGTH_LONG).show();

                                        }
                                    });
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    if(response.isSuccessful())
                                    {

                                    }
                                }
                            });
                        }








            }
        });
    }
}
