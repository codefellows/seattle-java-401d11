package com.edy.buystuff.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.edy.buystuff.R;

public class MainActivity extends AppCompatActivity
{
    public final static String TAG =  "edy_buystuff_mainactivity";
    public final static String PRODUCT_NAME_EXTRA_STRING = "productName";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Step 1: select the element
        TextView productNameTextView = (TextView) findViewById(R.id.productNameTextView);
        // Step 2/3: set the text
        //productNameTextView.setText(R.string.TestingText);
        // One-line version
        //((TextView) findViewById(R.id.productNameTextView)).setText("some text");

        // Step 1: select the element
        Button submitProductButton = (Button) findViewById(R.id.submitProductButton);
        // Step 2: set up an event listener
        // Method 1 (for step 2): override using a class instance
        /*submitProductButton.setOnClickListener(new View.OnClickListener()
           {
               // Step 3: go to the function you've written and do something
               @Override
               public void onClick(View view)
               {
                   Log.i(TAG, "button clicked");
               }
           }
        );*/

        // Method 2 (for step 2): use a lambda function
        // Step 3: go to the function you've written and do something
        submitProductButton.setOnClickListener(view ->
            {
                //TextView submittedStatusTextView = (TextView) findViewById(R.id.submittedStatusTextView);
                //submittedStatusTextView.setText(R.string.Submitted);
                // If you want to get some input text instead
                EditText productNamePlainText = (EditText) findViewById(R.id.productNamePlainText);
                //submittedStatusTextView.setText(productNamePlainText.getText());

                // Go to order form activity / page
                // Make Intent with context of where you are starting and class of where you are going
                Intent orderFormIntent = new Intent(MainActivity.this, OrderFormActivity.class);
                orderFormIntent.putExtra(PRODUCT_NAME_EXTRA_STRING, productNamePlainText.getText());
                startActivity(orderFormIntent);
            }
        );
    }
}