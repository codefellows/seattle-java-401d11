package com.edy.buystuff.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.edy.buystuff.R;

public class SignUpVerifyActivity extends AppCompatActivity
{
    public final static String TAG =  "edy_buystuff_signupverifyactivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_verify);

        Button signUpVerifyButton = findViewById(R.id.submitSignUpVerifyButton);
        signUpVerifyButton.setOnClickListener( onClick ->
            {
                EditText usernameSignUpVerifyEditText = findViewById(R.id.usernameSignUpVerifyEditText);
                String username = usernameSignUpVerifyEditText.getText().toString();
                EditText verificationCodeSignUpVerifyEditText = findViewById(R.id.verificationCodeSignUpVerifyEditText);
                String verificationCode = verificationCodeSignUpVerifyEditText.getText().toString();

                Amplify.Auth.confirmSignUp(username,
                    verificationCode,
                    success ->
                    {
                        Log.i(TAG, "Verification succeeded: " + success.toString());
                        Intent goToLoginActivityIntent = new Intent(SignUpVerifyActivity.this, LoginActivity.class);
                        startActivity(goToLoginActivityIntent);
                    },
                    failure ->
                    {
                        Log.i(TAG, "Verification failed: " + failure.toString());
                        runOnUiThread(() -> Toast.makeText(SignUpVerifyActivity.this, "Could not verify that user!", Toast.LENGTH_SHORT).show());
                    }
                );
            }
        );
    }
}