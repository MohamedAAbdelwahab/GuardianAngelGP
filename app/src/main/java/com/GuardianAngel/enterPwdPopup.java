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
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.room.Room;

import com.GuardianAngel.FileSystemModule.AppDatabase;
import com.GuardianAngel.FileSystemModule.FileReader;

public class enterPwdPopup extends Activity {
    Button submit;
    EditText password;
    FileReader file;
    PasswordHash hasher= new PasswordHash();
    AppDatabase db ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_pwd);
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name").build();
        getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        file=new FileReader(this);
        submit=findViewById(R.id.log_btn4);
        password=findViewById(R.id.pwd_box);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String ExpectedPassword=password.getText().toString();
                final String[] ActualPassword = {""};
                Thread thread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ActualPassword[0] =db.userDao().GetUserPassword();
                        if(hasher.checkPassword(ExpectedPassword, ActualPassword[0]))
                        {
                            Intent i = new Intent(getApplicationContext(),enterPwdPopup.class);
                            i.putExtra("SUCCESS",100);
                            setResult(RESULT_OK,i);
                            finish();
                        }
                        else
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"Wrong Password .. please check your password again",Toast.LENGTH_LONG).show();

                                }
                            });
                        finish();
                    }
                });
                thread.start();


            }
        });

    }
}
