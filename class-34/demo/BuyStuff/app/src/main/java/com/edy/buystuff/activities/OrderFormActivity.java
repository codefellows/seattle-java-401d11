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
import com.amplifyframework.datastore.generated.model.BusinessUnit;
import com.amplifyframework.datastore.generated.model.ShoppingItem;
import com.edy.buystuff.R;
import com.edy.buystuff.models.ProductCategoryEnum;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

        CompletableFuture<ShoppingItem> shoppingItemCompletableFuture = new CompletableFuture<>();  // Using this to handle async query below in a (sort of) synchronous manner

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
                        Spinner productCategorySpinner = findViewById(R.id.productCategorySpinner);
                        int spinnerPosition = getSpinnerIndex(productCategorySpinner, thisShoppingItem2.getProductCategory());
                        productCategorySpinner.setSelection(spinnerPosition);
                    });
                    shoppingItemCompletableFuture.complete(thisShoppingItem2);  // WARNING: Make sure CompletableFuture code is never in a call to runOnUiThread()!
                },
                failure -> {
                    Log.i(TAG, "Failed");
                    shoppingItemCompletableFuture.complete(null);
                }
        );

        TextView productNameOrderFormTextView = findViewById(R.id.productNameOrderFormTextView);
        productNameOrderFormTextView.setText("");

        Spinner productCategorySpinner = findViewById(R.id.productCategorySpinner);
        // TODO: values are not the nice human-readable values, fix this
        productCategorySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ProductCategoryEnum.values()));

        ShoppingItem shoppingItem = null;
        try
        {
            shoppingItem = shoppingItemCompletableFuture.get();  // This blocks until the future is complete
        }
        catch (InterruptedException ie)
        {
            Log.e(TAG, "InterruptedException while getting shopping item: " + ie.getMessage());
            Thread.currentThread().interrupt();
        }
        catch (ExecutionException ee)
        {
            Log.e(TAG, "ExecutionException while getting shopping item:  " + ee.getMessage());
        }

        Button saveShoppingItemButton = findViewById(R.id.saveShoppingItemButton);
        ShoppingItem shoppingItem2 = shoppingItem;
        saveShoppingItemButton.setOnClickListener(button ->
            {
                ShoppingItem updatedShoppingItem = ShoppingItem.builder()
                        .businessUnit(shoppingItem2.getBusinessUnit())
                        .id(shoppingItem2.getId())  // required for updates and deletes, but not creates
                        .itemName(shoppingItem2.getItemName())
                        .timeAdded(shoppingItem2.getTimeAdded())
                        .productCategory(ProductCategoryEnum.fromString(productCategorySpinner.getSelectedItem().toString()).toString())
                        .build();
                Amplify.API.mutate(
                        ModelMutation.update(updatedShoppingItem),
                        success -> Log.i(TAG, "Succeeded"),
                        failure -> Log.i(TAG, "Failed")
                );
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