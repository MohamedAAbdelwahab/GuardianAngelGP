package com.GuardianAngel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.room.Room;

import com.GuardianAngel.FileSystemModule.AppDatabase;

public class EnterNewPasswordActivity extends Activity {
    EditText newPassword;
    EditText ConfirmPassword;
    Button submit;
    PasswordHash hasher=new PasswordHash();
    AppDatabase db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restore_password);
        newPassword=findViewById(R.id.Password_box7);
        ConfirmPassword=findViewById(R.id.Password_box8);
        submit=findViewById(R.id.submit5);
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name").build();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread=new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if(TextUtils.isEmpty(newPassword.getText()) || TextUtils.isEmpty(ConfirmPassword.getText()) )
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"Please fill in all fields",Toast.LENGTH_LONG).show();

                                }
                            });
                        }
                        else if (newPassword.getText().length()<8){

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"Password Should be more than 8 characters",Toast.LENGTH_LONG).show();

                                }
                            });
                        }
                            else if(!newPassword.getText().toString().equals(ConfirmPassword.getText().toString())) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"New Password and confirmation doesn't match",Toast.LENGTH_LONG).show();

                                }
                            });
                        }
                        else {
                            db.userDao().updatePassword(hasher.hashPassword(newPassword.getText().toString()),0);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"Password changed successfully",Toast.LENGTH_LONG).show();
                                }
                            });
                            Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
                            startActivity(intent);
                        }
                    }
                });
                thread.start();
            }
        });
    }
}
