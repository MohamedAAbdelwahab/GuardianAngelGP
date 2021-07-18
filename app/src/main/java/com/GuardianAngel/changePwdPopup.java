package com.GuardianAngel;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.GuardianAngel.FileSystemModule.FileReader;

public class changePwdPopup extends Activity {
    Button submit;
    EditText password;
    EditText newPassword;
    EditText newPasswordConf;
    FileReader file;
    Context context;
    PasswordHash hasher=new PasswordHash();
    private static final String PasswordFileName="PasswordFile.txt";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_password);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*0.8),(int)(height*0.3));
        newPassword=findViewById(R.id.password_box4);
        newPasswordConf=findViewById(R.id.password_box6);
        password=findViewById(R.id.password_box3);
        submit=findViewById(R.id.log_btn3);
        file=new FileReader(this);;
        context=this;

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass = password.getText().toString();
                String newpass = newPassword.getText().toString();
                String actualpassword = file.ReadFile(context,PasswordFileName);
                String newpassconf = newPasswordConf.getText().toString();
                if(TextUtils.isEmpty(newPassword.getText()) || TextUtils.isEmpty(newPasswordConf.getText()) || TextUtils.isEmpty(password.getText()) )
                {
                    Toast.makeText(getApplicationContext(),"Please fill in all fields",Toast.LENGTH_LONG).show();
                    finish();

                }else if(!hasher.checkPassword(pass,actualpassword)){
                    Toast.makeText(getApplicationContext(),"Password entered is wrong",Toast.LENGTH_LONG).show();
                    finish();
                }else if(!newpass.equals(newpassconf)) {
                    Toast.makeText(getApplicationContext(),"New Password and confirmation doesn't match",Toast.LENGTH_LONG).show();
                    finish();
                }else if(newpass.equals(pass)) {
                    Toast.makeText(getApplicationContext(),"new Password matches the old one",Toast.LENGTH_LONG).show();
                    finish();
                }else {
                    file.writeFile(context,hasher.hashPassword(newpass),PasswordFileName);
                    Toast.makeText(getApplicationContext(),"Password changed successfully",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }
}

