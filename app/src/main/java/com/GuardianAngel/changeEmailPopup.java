package com.GuardianAngel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.room.Room;

import com.GuardianAngel.FileSystemModule.AppDatabase;
import com.GuardianAngel.FileSystemModule.FileReader;
import com.GuardianAngel.FileSystemModule.Global;

public class changeEmailPopup extends Activity {
    Button submit;
    EditText password;
    EditText email;
    EditText newEmail;
    FileReader file;
    Context context;
    PasswordHash hasher= new PasswordHash();
    AppDatabase db;
    private static final String PasswordFileName="PasswordFile.txt";
    private static final String EmailFileName="EmailFile.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_email);
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name").build();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        email=findViewById(R.id.email_box);
        newEmail=findViewById(R.id.email_box2);
        password=findViewById(R.id.password_box2);
        submit=findViewById(R.id.log_btn2);
//        file=new FileReader(this);;
        context=this;

        submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String mail = email.getText().toString();
                    final String pass = password.getText().toString();
                    final String newmail = newEmail.getText().toString();
                    Thread thread=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String actualmail = db.userDao().GetUserEmail();
                            final String actualpassword = db.userDao().GetUserPassword();

                            if(TextUtils.isEmpty(email.getText()) || TextUtils.isEmpty(newEmail.getText()) || TextUtils.isEmpty(password.getText()) )
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"Please fill in all fields",Toast.LENGTH_LONG).show();

                                    }
                                });
                                finish();

                            }else if(!mail.equals(actualmail)){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"Email entered is wrong",Toast.LENGTH_LONG).show();

                                    }
                                });
                                finish();
                            }else if(!hasher.checkPassword(pass,actualpassword)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"Password entered is wrong",Toast.LENGTH_LONG).show();

                                    }
                                });
                                finish();
                            }else if(newmail.equals(mail)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"new Email matches the old one",Toast.LENGTH_LONG).show();

                                    }
                                });
                                finish();
                            }else if(!Patterns.EMAIL_ADDRESS.matcher(newEmail.getText()).matches()){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"enter a valid Email",Toast.LENGTH_LONG).show();

                                    }
                                });
                                finish();
                            }else {
//                                file.writeFile(context,newmail,EmailFileName);
                                db.userDao().updateEmail(newmail,0);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"Email changed successfully",Toast.LENGTH_LONG).show();

                                    }
                                });
                                Global.email = newmail;
                                finish();
                            }
                        }
                    });
                    thread.start();
                }
            });
        }


    }
