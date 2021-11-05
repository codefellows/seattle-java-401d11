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
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.datastore.generated.model.BusinessUnit;
import com.amplifyframework.datastore.generated.model.ShoppingItem;
import com.edy.buystuff.R;
import com.edy.buystuff.adapters.ShoppingRecyclerViewAdapter;
import com.edy.buystuff.models.ProductCategoryEnum;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
{
    // Steps to convert your app from Room to using Amplify and DynamoDb:
    // 1. Delete all your database classes (database and DAOs)
    // 2. Remove the Room additions to your build.gradle file
    // 3. Remove all the Room annotations and imports from your files
    // 4. Add AWS Amplify dependencies to your build.gradle files
    // 5. Add an TaskStuffThingThatIMadeApplication file which extends Application in your main project area
    // 6. Put your Amplify configuration in that application file
    // 7. Update your schema.graphql file to model your data model correctly
    // 8. Run amplify codegen models to generate those models
    // 9. Delete your old model and convert every usage in your application to use the amplify models
    //    Specifically, every DAO usage needs to be converted to Amplify.API usage
    // 10. Change your RecyclerViewAdapter to have better string output

    public final static String TAG =  "edy_buystuff_mainactivity";
    public final static String PRODUCT_ID_EXTRA_STRING = "productId";
    public final static String DATABASE_INSTANCE_NAME = "edy_buystuff_db";
    public final static String BUSINESS_UNIT_UNKNOWN_NAME = "Business Unit Unknown";

    protected static SharedPreferences sharedPreferences;
    protected static Resources res;

    ShoppingRecyclerViewAdapter shoppingRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This is the "manual creation" part of your lab

        /*BusinessUnit businessUnit1 = BusinessUnit.builder()
                .businessUnitName("Business Unit 1")
                .build();
        Amplify.API.mutate(
                ModelMutation.create(businessUnit1),
                success -> Log.i(TAG, "Succeeded"),
                failure -> Log.i(TAG, "Failed")
        );
        BusinessUnit businessUnit2 = BusinessUnit.builder()
                .businessUnitName("Business Unit 2")
                .build();
        Amplify.API.mutate(
                ModelMutation.create(businessUnit2),
                success -> Log.i(TAG, "Succeeded"),
                failure -> Log.i(TAG, "Failed")
        );
        BusinessUnit businessUnit3 = BusinessUnit.builder()
                .businessUnitName("Business Unit 3")
                .build();
        Amplify.API.mutate(
                ModelMutation.create(businessUnit3),
                success -> Log.i(TAG, "Succeeded"),
                failure -> Log.i(TAG, "Failed")
        );*/

        Amplify.API.query(
            ModelQuery.list(ShoppingItem.class),
                potato -> {
                    List<ShoppingItem> shoppingItemList = new ArrayList<>();
                    if (potato.hasData())
                    {
                        for (ShoppingItem shoppingItem : potato.getData())
                        {
                            // Example of filtering for a specific business unit
                            if (shoppingItem.getBusinessUnit().getBusinessUnitName().equals(BUSINESS_UNIT_UNKNOWN_NAME))
                            {
                                shoppingItemList.add(shoppingItem);
                            }
                            Log.i(TAG, "Succeeded read of ShoppingItem: " + shoppingItem.getItemName());
                        }
                        runOnUiThread(() ->
                        {
                            shoppingRecyclerViewAdapter.setShoppingItemList(shoppingItemList);
                            shoppingRecyclerViewAdapter.notifyDataSetChanged();
                        });
                    }
                },
                failure -> {
                    Log.i(TAG, "Failed");
                }
        );

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

                CompletableFuture<BusinessUnit> businessUnitCompletableFuture = new CompletableFuture<>();

                Amplify.API.query(
                        ModelQuery.list(BusinessUnit.class),
                        success -> {
                            if (success.hasData())
                            {
                                for (BusinessUnit businessUnit : success.getData())
                                {
                                    if (businessUnit.getBusinessUnitName().equals(BUSINESS_UNIT_UNKNOWN_NAME))
                                    {
                                        businessUnitCompletableFuture.complete(businessUnit);
                                        Log.i(TAG, "Succeeded read of business unit: " + businessUnit.getBusinessUnitName());
                                    }
                                }
                            }
                        },
                        failure -> {
                            Log.i(TAG, "Failed");
                            businessUnitCompletableFuture.complete(null);
                        }
                );

                BusinessUnit businessUnitUnknown = null;
                try
                {
                    businessUnitUnknown = businessUnitCompletableFuture.get();
                }
                catch (InterruptedException ie)
                {
                    Log.i(TAG, "InterruptedException while getting business unit: " + ie.getMessage());
                    Thread.currentThread().interrupt();
                }
                catch (ExecutionException ee)
                {
                    Log.i(TAG, "ExecutionException while getting business unit:  " + ee.getMessage());
                }

                String productNamePlainTextString = productNameEditText.getText().toString();
                ShoppingItem shoppingItem = ShoppingItem.builder()
                        .businessUnit(businessUnitUnknown)
                        .itemName(productNamePlainTextString)
                        .timeAdded(new Temporal.DateTime(new Date(), 0))  // UTC time
                        .productCategory(ProductCategoryEnum.UNKNOWN.toString())
                        .build();
                Amplify.API.mutate(
                    ModelMutation.create(shoppingItem),
                    success -> {
                        Log.i(TAG, "Succeeded");
                    },
                    failure -> {
                        Log.i(TAG, "Failed");
                    }
                );

                List<ShoppingItem> shoppingItemList2 = shoppingRecyclerViewAdapter.getShoppingItemList();
                shoppingItemList2.add(shoppingItem);

                shoppingRecyclerViewAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Shopping Item saved!", Toast.LENGTH_SHORT).show();
            }
        );
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        Amplify.API.query(
                ModelQuery.list(ShoppingItem.class),
                success -> {
                    List<ShoppingItem> shoppingItemList = new ArrayList<>();
                    if (success.hasData())
                    {
                        for (ShoppingItem shoppingItem : success.getData())
                        {
                            // Example of filtering for a specific business unit
                            if (shoppingItem.getBusinessUnit().getBusinessUnitName().equals(BUSINESS_UNIT_UNKNOWN_NAME))
                            {
                                shoppingItemList.add(shoppingItem);
                            }
                            Log.i(TAG, "Succeeded read of ShoppingItem: " + shoppingItem.getItemName());
                        }
                        runOnUiThread(() ->
                        {
                            shoppingRecyclerViewAdapter.setShoppingItemList(shoppingItemList);
                            shoppingRecyclerViewAdapter.notifyDataSetChanged();
                        });
                    }
                },
                failure -> {
                    Log.i(TAG, "Failed");
                }
        );

        String userNickname = sharedPreferences.getString(USER_NICKNAME_KEY, "");

        if (!userNickname.equals(""))
        {
            ((TextView) findViewById(R.id.userNicknameMainTextView)).setText(res.getString(R.string.WelcomeUsername, userNickname));
        }
    }
}