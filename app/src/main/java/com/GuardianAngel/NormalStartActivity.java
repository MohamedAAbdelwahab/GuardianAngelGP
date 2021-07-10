package com.GuardianAngel;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.GuardianAngel.FileSystemModule.FileReader;


//The activity that opens everytime the user taps on the application icon to verify his password and let him into the parent mode



public class NormalStartActivity extends Activity {
    Button submit;
    EditText password;
    FileReader file;
    Context context;
    private static final String PasswordFileName="PasswordFile.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.normal_start_activity);
        context=this;
        file=new FileReader(this);
        submit=findViewById(R.id.log_btn);
        password=findViewById(R.id.password_box);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ExpectedPassword=password.getText().toString();
                String ActualPassword=file.ReadFile(context,PasswordFileName);
                if(ActualPassword.equals(ExpectedPassword))
                    {
                        Intent i = new Intent(getApplicationContext(), HomeActivity.class); ///// edit
                        startActivity(i);
                         finish();
                    }
                else
                    Toast.makeText(getApplicationContext(),"Wrong Password .. please check your password again",Toast.LENGTH_LONG).show();

            }
        });
        // start projection

        // stop projection

    }
}
