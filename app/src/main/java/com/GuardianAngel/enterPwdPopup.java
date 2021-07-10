package com.GuardianAngel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.GuardianAngel.FileSystemModule.FileReader;

public class enterPwdPopup extends Activity {
    Button submit;
    EditText password;
    FileReader file;
    Context context;
    private static final String PasswordFileName="PasswordFile.txt";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_pwd);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*0.8),(int)(height*0.2));
        context=this;
        file=new FileReader(this);
        submit=findViewById(R.id.log_btn4);
        password=findViewById(R.id.pwd_box);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ExpectedPassword=password.getText().toString();
                String ActualPassword=file.ReadFile(context,PasswordFileName);
                if(ActualPassword.equals(ExpectedPassword))
                {
                    Intent i = new Intent(getApplicationContext(),enterPwdPopup.class);
                    i.putExtra("SUCCESS",100);
                    setResult(RESULT_OK,i);
                    finish();
                }
                else
                    Toast.makeText(getApplicationContext(),"Wrong Password .. please check your password again",Toast.LENGTH_LONG).show();
                    finish();

            }
        });

    }
}
