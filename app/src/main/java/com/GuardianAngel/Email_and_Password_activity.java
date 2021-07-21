package com.GuardianAngel;
//First Activity in the application where the user enter for the first time the password and email

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.room.Room;
import com.GuardianAngel.FileSystemModule.AppDatabase;
import com.GuardianAngel.FileSystemModule.FileReader;
import com.GuardianAngel.FileSystemModule.Global;
import com.GuardianAngel.FileSystemModule.User;

public class Email_and_Password_activity extends Activity {
    EditText EditTextemail;
    EditText EditTextpassword;
    EditText EditTextconfirm;
    Button registerbtn;
    PasswordHash hasher=new PasswordHash();
    AppDatabase db ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        db= Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name").build();
        SharedPreferences pref = this.getSharedPreferences("Sample", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if (pref.getInt("ATTEMPTs", 0) == 0)
        {
            editor.putInt("ATTEMPTs", 0);
            editor.apply();
        }
        EditTextemail=findViewById(R.id.Email_box);
        EditTextpassword=findViewById(R.id.Password_box);
        EditTextconfirm=findViewById(R.id.conf_box);
        registerbtn=findViewById(R.id.reg_btn);
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                        .getBoolean("isFirstRun", true);

                if(isFirstRun)
                {


                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    registerbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String password = EditTextpassword.getText().toString();
                            String confirmation = EditTextconfirm.getText().toString();
                            if(TextUtils.isEmpty(EditTextemail.getText()) || TextUtils.isEmpty(EditTextpassword.getText()) )
                            {
                                if( TextUtils.isEmpty(EditTextemail.getText())){
                                    EditTextemail.setError( "Email is required!" );
                                }
                                if (TextUtils.isEmpty(EditTextpassword.getText())){
                                    EditTextpassword.setError( "password is required!" );
                                }
                                if (TextUtils.isEmpty(EditTextconfirm.getText())){
                                    EditTextconfirm.setError( "password confirmation required!" );
                                }

                            }else if(!Patterns.EMAIL_ADDRESS.matcher(EditTextemail.getText()).matches()){
                                EditTextemail.setError( "enter a valid email" );
                            }
                            else if (EditTextpassword.getText().length()<8){

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"Password Should be more than 8 characters",Toast.LENGTH_LONG).show();

                                    }
                                });
                            }
                            else if(!password.equals(confirmation)){
                                EditTextconfirm.setError( "password doesn't match!" );

                            }else {
                                String Password=EditTextpassword.getText().toString();
                                String Email=EditTextemail.getText().toString();
                                final User user=new User();
                                user.Email=Email;
                                user.Password=hasher.hashPassword(Password);
                                Thread thread=new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        db.userDao().insertAll(user);

                                    }
                                });
                                thread.start();
                                Global.email = EditTextemail.getText().toString();
                                getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                        .putBoolean("isFirstRun", false).apply();
                                Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }
                    });

                }
                if (!isFirstRun) {
                    //show start activity

                    Intent i = new Intent(getApplicationContext(), NormalStartActivity.class);
                    startActivity(i);
                    finish();

                }


            }
        });
        thread.start();



    }

}
