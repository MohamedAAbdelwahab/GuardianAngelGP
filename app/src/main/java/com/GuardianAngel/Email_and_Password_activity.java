package com.GuardianAngel;
//First Activity in the application where the user enter for the first time the password and email

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.GuardianAngel.FileSystemModule.FileReader;

public class Email_and_Password_activity extends Activity {
    EditText EditTextemail;
    EditText EditTextpassword;
    Button registerbtn;
    Context context=null;
    private static final String PasswordFileName="PasswordFile.txt";
    private static final String EmailFileName="EmailFile.txt";
    public FileReader file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        EditTextemail=findViewById(R.id.Email);
        EditTextpassword=findViewById(R.id.passwrd);
        registerbtn=findViewById(R.id.Register);
        file=new FileReader(this);
        if(file.isExists(this))
        {
            Intent i = new Intent(getApplicationContext(), NormalStartActivity.class);
            startActivity(i);
            finish();
        }
        else{

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            context=this;
            if(TextUtils.isEmpty(EditTextemail.getText()) || TextUtils.isEmpty(EditTextpassword.getText()) )
            {
                if( TextUtils.isEmpty(EditTextemail.getText())){
                    EditTextemail.setError( "Email is required!" );
                }
                if (TextUtils.isEmpty(EditTextpassword.getText())){
                    EditTextpassword.setError( "password is required!" );
                }
            }
            registerbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(TextUtils.isEmpty(EditTextemail.getText()) || TextUtils.isEmpty(EditTextpassword.getText()) )
                    {
                        if( TextUtils.isEmpty(EditTextemail.getText())){
                            EditTextemail.setError( "Email is required!" );
                        }
                        if (TextUtils.isEmpty(EditTextpassword.getText())){
                            EditTextpassword.setError( "password is required!" );
                        }
                    }else {
                        String Password=EditTextpassword.getText().toString();
                        file.writeFile(context,Password,PasswordFileName);
                        String Email=EditTextemail.getText().toString();
                        file.writeFile(context,Email,EmailFileName);
                        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            });
        }


    }

}
