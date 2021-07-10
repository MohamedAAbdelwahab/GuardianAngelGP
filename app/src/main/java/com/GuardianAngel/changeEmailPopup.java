package com.GuardianAngel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.GuardianAngel.FileSystemModule.FileReader;

public class changeEmailPopup extends Activity {
    Button submit;
    EditText password;
    EditText email;
    EditText newEmail;
    FileReader file;
    Context context;
    private static final String PasswordFileName="PasswordFile.txt";
    private static final String EmailFileName="EmailFile.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_email);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*0.8),(int)(height*0.3));
        email=findViewById(R.id.email_box);
        newEmail=findViewById(R.id.email_box2);
        password=findViewById(R.id.password_box2);
        submit=findViewById(R.id.log_btn2);
        file=new FileReader(this);;
        context=this;

        submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String mail = email.getText().toString();
                    String actualmail = file.ReadFile(context,EmailFileName);
                    String actualpassword = file.ReadFile(context,PasswordFileName);
                    String pass = password.getText().toString();
                    String newmail = newEmail.getText().toString();
                    if(TextUtils.isEmpty(email.getText()) || TextUtils.isEmpty(newEmail.getText()) || TextUtils.isEmpty(password.getText()) )
                    {
                        Toast.makeText(getApplicationContext(),"Please fill in all fields",Toast.LENGTH_LONG).show();
                        finish();

                    }else if(!mail.equals(actualmail)){
                        Toast.makeText(getApplicationContext(),"Email entered is wrong",Toast.LENGTH_LONG).show();
                        finish();
                    }else if(!pass.equals(actualpassword)) {
                        Toast.makeText(getApplicationContext(),"Password entered is wrong",Toast.LENGTH_LONG).show();
                        finish();
                    }else if(newmail.equals(mail)) {
                        Toast.makeText(getApplicationContext(),"new Email matches the old one",Toast.LENGTH_LONG).show();
                        finish();
                    }else {
                        file.writeFile(context,newmail,EmailFileName);
                        Toast.makeText(getApplicationContext(),"Email changed successfully",Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            });
        }


    }
