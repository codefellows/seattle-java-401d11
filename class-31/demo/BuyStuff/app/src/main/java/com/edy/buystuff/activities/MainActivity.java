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
import android.widget.Toast;

import com.edy.buystuff.R;
import com.edy.buystuff.adapters.ShoppingRecyclerViewAdapter;
import com.edy.buystuff.models.ShoppingItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    public final static String TAG =  "edy_buystuff_mainactivity";
    public final static String PRODUCT_ID_EXTRA_STRING = "productId";
    public final static String DATABASE_INSTANCE_NAME = "edy_buystuff_db";

    protected static SharedPreferences sharedPreferences;
    protected static Resources res;

    ShoppingRecyclerViewAdapter shoppingRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView shoppingRecyclerView = findViewById(R.id.shoppingRecyclerView);

        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);  // vertical layout
        shoppingRecyclerView.setLayoutManager(lm);

        //List<ShoppingItem> shoppingItemList = buyStuffDatabase.shoppingItemDao().findAll();
        List<ShoppingItem> shoppingItemList = new ArrayList<>();

        shoppingRecyclerViewAdapter = new ShoppingRecyclerViewAdapter(this, shoppingItemList);
        shoppingRecyclerView.setAdapter(shoppingRecyclerViewAdapter);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        res = getResources();

        ImageView userSettingsLinkImageView = findViewById(R.id.userSettingsLinkImageView);

        userSettingsLinkImageView.setOnClickListener(view ->
            {
                Intent userSettingsActivityIntent = new Intent(MainActivity.this, UserSettingsActivity.class);
                startActivity(userSettingsActivityIntent);
            });

        Button submitProductButton = findViewById(R.id.submitProductButton);

        submitProductButton.setOnClickListener(view ->
            {
                EditText productNameEditText = findViewById(R.id.productNameEditText);

                /*Intent orderFormIntent = new Intent(MainActivity.this, OrderFormActivity.class);
                orderFormIntent.putExtra(PRODUCT_NAME_EXTRA_STRING, productNameEditText.getText());
                startActivity(orderFormIntent);*/

                String productNamePlainTextString = productNameEditText.getText().toString();
                ShoppingItem shoppingItem = new ShoppingItem(productNamePlainTextString, new Date());

                //long newShoppingItemId = buyStuffDatabase.shoppingItemDao().insert(shoppingItem);
                long newShoppingItemId = 0;

                //List<ShoppingItem> shoppingItemList2 = buyStuffDatabase.shoppingItemDao().findAll();
                List<ShoppingItem> shoppingItemList2 = new ArrayList<>();

                shoppingRecyclerViewAdapter.setShoppingItemList(shoppingItemList2);
                shoppingRecyclerViewAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Shopping Item saved! Id: " + newShoppingItemId, Toast.LENGTH_SHORT).show();
            }
        );
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        //List<ShoppingItem> shoppingItemList2 = buyStuffDatabase.shoppingItemDao().findAll();
        List<ShoppingItem> shoppingItemList2 = new ArrayList<>();
        shoppingRecyclerViewAdapter.setShoppingItemList(shoppingItemList2);
        shoppingRecyclerViewAdapter.notifyDataSetChanged();
        String userNickname = sharedPreferences.getString(USER_NICKNAME_KEY, "");

        if (!userNickname.equals(""))
        {
            ((TextView) findViewById(R.id.userNicknameMainTextView)).setText(res.getString(R.string.WelcomeUsername, userNickname));
        }
    }
}