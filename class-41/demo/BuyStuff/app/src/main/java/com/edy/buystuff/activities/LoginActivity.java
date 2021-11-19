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

public class LoginActivity extends AppCompatActivity
{
    public final static String TAG =  "edy_buystuff_loginactivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button logInButton = findViewById(R.id.logInButton);
        logInButton.setOnClickListener( onClick ->
            {
                EditText usernameLogInEditText = findViewById(R.id.usernameLogInEditText);
                String username = usernameLogInEditText.getText().toString();
                EditText passwordLogInEditText = findViewById(R.id.passwordLogInEditText);
                String password = passwordLogInEditText.getText().toString();

                Amplify.Auth.signIn(username,
                    password,
                    success ->
                    {
                        Log.i(TAG, "Login succeeded: " + success.toString());
                        Intent goToMainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(goToMainActivityIntent);
                    },
                    failure ->
                    {
                        Log.i(TAG, "Login failed: " + failure.toString());
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Could not log in that user!", Toast.LENGTH_SHORT).show());
                    }
                );
            }
        );
    }
}