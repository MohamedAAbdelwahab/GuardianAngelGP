package com.GuardianAngel;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.GuardianAngel.FileSystemModule.FileReader;

public class changePwdPopup extends Activity {
    Button submit;
    EditText password;
    EditText newPassword;
    EditText newPasswordConf;
    FileReader file;
    UserDataHandler userDataHandler;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_password);
        userDataHandler=new UserDataHandler(getApplicationContext());
        getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        newPassword=findViewById(R.id.password_box4);
        newPasswordConf=findViewById(R.id.password_box6);
        password=findViewById(R.id.password_box3);
        submit=findViewById(R.id.log_btn3);
        file=new FileReader(this);;

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String pass = password.getText().toString();
                final String newpass = newPassword.getText().toString();
                final String newpassconf = newPasswordConf.getText().toString();
                Thread thread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String actualpassword =userDataHandler.getUserPassword();

                        if(TextUtils.isEmpty(newPassword.getText()) || TextUtils.isEmpty(newPasswordConf.getText()) || TextUtils.isEmpty(password.getText()) )
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"Please fill in all fields",Toast.LENGTH_LONG).show();

                                }
                            });
                            finish();

                        }else if(!userDataHandler.CheckPassword(pass,actualpassword)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"Password entered is wrong",Toast.LENGTH_LONG).show();

                                }
                            });
                            finish();
                        }
                        else if (newpass.length()<8){

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"Password Should be more than 8 characters",Toast.LENGTH_LONG).show();

                                }
                            });
                        }
                        else if(!newpass.equals(newpassconf)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"New Password and confirmation doesn't match",Toast.LENGTH_LONG).show();

                                }
                            });
                            finish();
                        }else if(newpass.equals(pass)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"new Password matches the old one",Toast.LENGTH_LONG).show();
                                }
                            });
                            finish();
                        }else {
                            userDataHandler.updatePassword((newpass),0);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"Password changed successfully",Toast.LENGTH_LONG).show();
                                }
                            });
                            finish();
                        }
                    }
                });
                thread.start();

            }
        });
    }
}

