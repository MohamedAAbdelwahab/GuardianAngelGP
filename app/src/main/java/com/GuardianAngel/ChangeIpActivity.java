package com.GuardianAngel;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

public class ChangeIpActivity extends Activity {
    Button submit;
    EditText ip;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changeip);
        submit=findViewById(R.id.ChangeIpBtn);
        ip=findViewById(R.id.ChangeIp);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ipString=ip.getText().toString();
                Log.i("IP",ipString);
                HomeActivity.IP=ipString;
                Log.i("HomeScreen",HomeActivity.IP);
                finish();
            }
        });
    }
}
