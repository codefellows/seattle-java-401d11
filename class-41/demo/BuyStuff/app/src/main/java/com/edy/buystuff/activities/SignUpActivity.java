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

public class SignUpActivity extends AppCompatActivity
{
    public final static String TAG =  "edy_buystuff_signupactivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button signUpButton = findViewById(R.id.submitSignUpButton);
        signUpButton.setOnClickListener( onClick ->
            {
                EditText usernameSignUpEditText = findViewById(R.id.usernameSignUpEditText);
                String username = usernameSignUpEditText.getText().toString();
                EditText passwordSignUpEditText = findViewById(R.id.passwordSignUpEditText);
                String password = passwordSignUpEditText.getText().toString();

                Amplify.Auth.signUp(username,
                    password,
                    AuthSignUpOptions.builder()
                            .userAttribute(AuthUserAttributeKey.email(), username)
                            .userAttribute(AuthUserAttributeKey.nickname(), "No Nickname")
                            .build(),
                    success ->
                    {
                        Log.i(TAG, "Signup succeeded: " + success.toString());
                        // TODO: Be nicer to the user and hand the email address to verify activity
                        Intent goToSignUpVerifyActivityIntent = new Intent(SignUpActivity.this, SignUpVerifyActivity.class);
                        startActivity(goToSignUpVerifyActivityIntent);
                    },
                    failure ->
                    {
                        Log.i(TAG, "Signup failed: " + failure.toString());
                        runOnUiThread(() -> Toast.makeText(SignUpActivity.this, "Could not sign up that user!", Toast.LENGTH_SHORT).show());
                    }
                );
            }
        );
    }
}