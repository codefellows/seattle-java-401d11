package com.edy.buystuff.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.edy.buystuff.R;
import com.google.android.material.snackbar.Snackbar;

public class UserSettingsActivity extends AppCompatActivity
{
    public final static String TAG =  "edy_buystuff_usersettingsactivity";
    public final static String USER_ADDRESS_KEY = "userAddress";
    public final static String USER_NICKNAME_KEY = "userNickname";
    protected static SharedPreferences sharedPreferences;
    protected static SharedPreferences.Editor sharedPreferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferencesEditor = sharedPreferences.edit();

        EditText postalAddressEditText = findViewById(R.id.postalAddressEditText);
        String postalAddress = sharedPreferences.getString(USER_ADDRESS_KEY, "");
        postalAddressEditText.setText(postalAddress);

        EditText userNicknameEditText = findViewById(R.id.userNicknameEditText);
        String userNickname = sharedPreferences.getString(USER_NICKNAME_KEY, "");
        userNicknameEditText.setText(userNickname);

        Button saveUserSettingsButton = findViewById(R.id.saveUserSettingsButton);
        saveUserSettingsButton.setOnClickListener( view ->
            {
                String postalAddress2 = postalAddressEditText.getText().toString();
                String userNickname2 = userNicknameEditText.getText().toString();
                sharedPreferencesEditor.putString(USER_ADDRESS_KEY, postalAddress2);
                sharedPreferencesEditor.putString(USER_NICKNAME_KEY, userNickname2);
                sharedPreferencesEditor.apply();
                //Snackbar.make(findViewById(R.id.userSettingsActivity), R.string.SettingsSaved, Snackbar.LENGTH_SHORT).show();
                Toast.makeText(this, R.string.SettingsSaved, Toast.LENGTH_SHORT).show();
                Log.i(TAG, userNickname2);
            }
        );
    }
}