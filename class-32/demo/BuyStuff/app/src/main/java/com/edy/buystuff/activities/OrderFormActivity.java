package com.edy.buystuff.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.datastore.generated.model.ShoppingItem;
import com.edy.buystuff.R;
import com.edy.buystuff.models.ProductCategoryEnum;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderFormActivity extends AppCompatActivity
{
    public final static String TAG =  "edy_buystuff_orderformactivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_form);

        Intent intent = getIntent();
        String productId = intent.getStringExtra(MainActivity.PRODUCT_ID_EXTRA_STRING);

        Amplify.API.query(
                ModelQuery.list(ShoppingItem.class),
                success -> {
                    ShoppingItem thisShoppingItem = null;
                    for (ShoppingItem shoppingItem : success.getData())
                    {
                        if(productId.equals(shoppingItem.getId()))
                        {
                            thisShoppingItem = shoppingItem;
                            break;
                        }
                        Log.i(TAG, "Succeeded read of ShoppingItem: " + shoppingItem.getItemName());
                    }
                    ShoppingItem thisShoppingItem2 = thisShoppingItem;  // TODO: investigate how to make this better
                    runOnUiThread(() ->
                    {
                        TextView productNameOrderFormTextView = findViewById(R.id.productNameOrderFormTextView);
                        productNameOrderFormTextView.setText(thisShoppingItem2.getItemName());
                    });
                },
                failure -> {
                    Log.i(TAG, "Failed");
                }
        );

        //ShoppingItem shoppingItem = buyStuffDatabase.shoppingItemDao().findById(productId);
        ShoppingItem shoppingItem = null;

        TextView productNameOrderFormTextView = findViewById(R.id.productNameOrderFormTextView);
        productNameOrderFormTextView.setText("");

        Spinner productCategorySpinner = findViewById(R.id.productCategorySpinner);
        // TODO: values are not the nice human-readable values, fix this
        productCategorySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ProductCategoryEnum.values()));
        // TODO: Update the spinner to actually take data from db
        //int spinnerPosition = getSpinnerIndex(productCategorySpinner, shoppingItem.getProductCategory().toString());
        //productCategorySpinner.setSelection(spinnerPosition);

        Button saveShoppingItemButton = findViewById(R.id.saveShoppingItemButton);
        saveShoppingItemButton.setOnClickListener(button ->
            {
                //shoppingItem.productCategory = ProductCategoryEnum.valueOf(productCategorySpinner.getSelectedItem().toString());
                //buyStuffDatabase.shoppingItemDao().update(shoppingItem);
            }
        );
    }

    private int getSpinnerIndex(Spinner spinner, String stringValueToCheck){
        for (int i = 0;i < spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(stringValueToCheck)){
                return i;
            }
        }

        return 0;
    }
}