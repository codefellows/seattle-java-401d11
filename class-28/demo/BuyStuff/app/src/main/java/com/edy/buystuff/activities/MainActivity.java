package com.edy.buystuff.activities;

import static com.edy.buystuff.activities.UserSettingsActivity.USER_NICKNAME_KEY;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.edy.buystuff.adapters.CartRecyclerViewAdapter;
import com.edy.buystuff.models.CartItem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

        // Step 1-1: Add a RecyclerView to your layout
        // Step 1-2: Grab the RecyclerView by ID
        RecyclerView shoppingCartRecyclerView = findViewById(R.id.shoppingCartRecyclerView);

        // Step 1-3: Assign a layout manager for this RecyclerView
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);  // vertical layout
        shoppingCartRecyclerView.setLayoutManager(lm);

        // Step 2-1: Create a data model class, and create that data by hand
        List<CartItem> cartItemList = new ArrayList<>();
        cartItemList.add(new CartItem("Shoes", new Date()));
        cartItemList.add(new CartItem("Socks", new Date()));
        cartItemList.add(new CartItem("Shampoo", new Date()));
        cartItemList.add(new CartItem("Shoes 2", new Date()));
        cartItemList.add(new CartItem("Socks 2", new Date()));
        cartItemList.add(new CartItem("Shampoo 2", new Date()));
        cartItemList.add(new CartItem("Shoes 3", new Date()));
        cartItemList.add(new CartItem("Socks 3", new Date()));
        cartItemList.add(new CartItem("Shampoo 3", new Date()));
        cartItemList.add(new CartItem("Shoes 4", new Date()));
        cartItemList.add(new CartItem("Socks 4", new Date()));
        cartItemList.add(new CartItem("Shampoo 4", new Date()));

        // Step 1-4: Make a class whose sole purpose is to manage RecyclerViews and attach it to the RecyclerView
        // Step 2-2: Pass data into RecyclerViewAdapter
        CartRecyclerViewAdapter cartRecyclerViewAdapter = new CartRecyclerViewAdapter(this, cartItemList);
        shoppingCartRecyclerView.setAdapter(cartRecyclerViewAdapter);

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