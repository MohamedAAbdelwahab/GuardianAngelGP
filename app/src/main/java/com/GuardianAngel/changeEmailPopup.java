package com.GuardianAngel;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.GuardianAngel.FileSystemModule.Global;

public class changeEmailPopup extends Activity {
    Button submit;
    EditText password;
    EditText email;
    EditText newEmail;
    UserDataHandler userDataHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_email);
        userDataHandler=new UserDataHandler(getApplicationContext());
        getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        email=findViewById(R.id.email_box);
        newEmail=findViewById(R.id.email_box2);
        password=findViewById(R.id.password_box2);
        submit=findViewById(R.id.log_btn2);
        submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String mail = email.getText().toString();
                    final String pass = password.getText().toString();
                    final String newmail = newEmail.getText().toString();
                    Thread thread=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String actualmail = userDataHandler.getUserEmail();
                            final String actualpassword = userDataHandler.getUserPassword();

                            if(TextUtils.isEmpty(email.getText()) || TextUtils.isEmpty(newEmail.getText()) || TextUtils.isEmpty(password.getText()) )
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"Please fill in all fields",Toast.LENGTH_LONG).show();

                                    }
                                });
                                finish();

                            }else if(!mail.equals(actualmail)){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"Email entered is wrong",Toast.LENGTH_LONG).show();

                                    }
                                });
                                finish();
                            }else if(!userDataHandler.CheckPassword(pass,actualpassword)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"Password entered is wrong",Toast.LENGTH_LONG).show();

                                    }
                                });
                                finish();
                            }else if(newmail.equals(mail)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"new Email matches the old one",Toast.LENGTH_LONG).show();

                                    }
                                });
                                finish();
                            }else if(!Patterns.EMAIL_ADDRESS.matcher(newEmail.getText()).matches()){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"enter a valid Email",Toast.LENGTH_LONG).show();

                                    }
                                });
                                finish();
                            }else {
                                userDataHandler.updateEmail(newmail,0);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"Email changed successfully",Toast.LENGTH_LONG).show();

                                    }
                                });
                                Global.email = newmail;
                                finish();
                            }
                        }
                    });
                    thread.start();
                }
            });
        }


    }
