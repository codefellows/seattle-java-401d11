package com.edy.buystuff.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.edy.buystuff.R;

public class OrderFormActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_form);

        Intent intent = getIntent();
        String productName = intent.getExtras().get(MainActivity.PRODUCT_NAME_EXTRA_STRING).toString();

        TextView productNameOrderFormTextView = (TextView) findViewById(R.id.productNameOrderFormTextView);
        productNameOrderFormTextView.setText(productName);

    }
}