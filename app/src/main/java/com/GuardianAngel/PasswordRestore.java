package com.GuardianAngel;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.GuardianAngel.FileSystemModule.Global;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.Random;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class PasswordRestore extends Activity {
    EditText Code;
    Button submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_code_pop_up);
        getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        Code=findViewById(R.id.Confirm_Code);
        submit=findViewById(R.id.submit6);
        int leftLimit = 0; // letter 'a'
        int rightLimit = 9; // letter 'z'
        int targetStringLength = 5;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append(randomLimitedInt);
        }
        final String generatedString = buffer.toString();
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        multipartBodyBuilder.addFormDataPart("receiver", Global.email);
        multipartBodyBuilder.addFormDataPart("APIpwd", "94Vtwn0iHVQSpBT");
        multipartBodyBuilder.addFormDataPart("code",generatedString);
        RequestBody postBody = multipartBodyBuilder.build();

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("http://"+HomeActivity.IP+":8000/restore").post(postBody).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"server error",Toast.LENGTH_LONG).show();

                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful())
                {

                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Code.getText().toString().equals(generatedString))
                {
                    Intent intent=new Intent(getApplicationContext(),EnterNewPasswordActivity.class);
                    startActivity(intent);

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"You Entered Wrong code",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
