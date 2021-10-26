package com.edy.buystuff.activities;

import static com.edy.buystuff.activities.UserSettingsActivity.USER_NICKNAME_KEY;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.edy.buystuff.R;

public class MainActivity extends AppCompatActivity
{
    public final static String TAG =  "edy_buystuff_mainactivity";
    public final static String PRODUCT_NAME_EXTRA_STRING = "productName";

    protected static SharedPreferences sharedPreferences;
    protected static Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        res = getResources();

        ImageView userSettingsLinkImageView = findViewById(R.id.userSettingsLinkImageView);

        userSettingsLinkImageView.setOnClickListener(view ->
            {
                Intent userSettingsActivityIntent = new Intent(MainActivity.this, UserSettingsActivity.class);
                startActivity(userSettingsActivityIntent);
            });

        // Step 1: select the element
        Button submitProductButton = findViewById(R.id.submitProductButton);

        // Method 2 (for step 2): use a lambda function
        // Step 3: go to the function you've written and do something
        submitProductButton.setOnClickListener(view ->
            {
                // If you want to get some input text instead
                EditText productNamePlainText = findViewById(R.id.productNameEditText);

                // Go to order form activity / page
                // Make Intent with context of where you are starting and class of where you are going
                Intent orderFormIntent = new Intent(MainActivity.this, OrderFormActivity.class);
                orderFormIntent.putExtra(PRODUCT_NAME_EXTRA_STRING, productNamePlainText.getText());
                startActivity(orderFormIntent);
            }
        );
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        String userNickname = sharedPreferences.getString(USER_NICKNAME_KEY, "");

        if (!userNickname.equals(""))
        {
            ((TextView) findViewById(R.id.userNicknameMainTextView)).setText(res.getString(R.string.WelcomeUsername, userNickname));
        }
    }
}