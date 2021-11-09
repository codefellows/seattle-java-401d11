package com.edy.buystuff.activities;

import static com.edy.buystuff.activities.UserSettingsActivity.USER_NICKNAME_KEY;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.datastore.generated.model.BusinessUnit;
import com.amplifyframework.datastore.generated.model.ShoppingItem;
import com.edy.buystuff.R;
import com.edy.buystuff.adapters.ShoppingRecyclerViewAdapter;
import com.edy.buystuff.models.ProductCategoryEnum;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
{
    public final static String TAG =  "edy_buystuff_mainactivity";
    public final static String PRODUCT_ID_EXTRA_STRING = "productId";
    public final static String BUSINESS_UNIT_UNKNOWN_NAME = "Business Unit Unknown";

    protected static SharedPreferences sharedPreferences;
    protected static Resources res;

    ShoppingRecyclerViewAdapter shoppingRecyclerViewAdapter;
    ActivityResultLauncher<Intent> activityResultLauncher;

    // Need these to save our shopping item to the db
    String newProductName;
    String unknownProductCategoryType = ProductCategoryEnum.UNKNOWN.toString();
    BusinessUnit businessUnitUnknown;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // TODO: Figure out how to make this not flicker before redirecting

        activityResultLauncher = getImagePickingActivityResultLauncher();

        AuthUser currentUser = Amplify.Auth.getCurrentUser();

        if (currentUser == null)
        {
            Intent goToLoginActivityIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(goToLoginActivityIntent);
        }

        getShoppingItemList();

        // This is testing S3 file upload manually
        /*
        File testFile = new File(getApplicationContext().getFilesDir(), "testFileName");  // Creating File on the phone

        try (BufferedWriter testFileBufferedWriter = new BufferedWriter(new FileWriter(testFile)))  // using try-with-resources to not have to remember to close
        {
            testFileBufferedWriter.append("this is a test");
        }
        catch (IOException ioe)
        {
            Log.i(TAG, "Error writing test file: " + ioe.getMessage());
        }

        Amplify.Storage.uploadFile(
            "testFileKey",
                testFile,
                success -> {Log.i(TAG, "S3 test file upload succeeded! Key is: " + success.getKey());},
                failure -> {Log.i(TAG, "S3 test file upload failed! " + failure.getMessage(), failure);}
        );*/

        RecyclerView shoppingRecyclerView = findViewById(R.id.shoppingRecyclerView);

        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);  // vertical layout
        shoppingRecyclerView.setLayoutManager(lm);

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

        CompletableFuture<BusinessUnit> businessUnitCompletableFuture = getUnknownBusinessUnitFuture();
        businessUnitUnknown = null;
        try
        {
            businessUnitUnknown = businessUnitCompletableFuture.get();
        }
        catch (InterruptedException ie)
        {
            Log.e(TAG, "InterruptedException while getting business unit: " + ie.getMessage());
            Thread.currentThread().interrupt();
        }
        catch (ExecutionException ee)
        {
            Log.e(TAG, "ExecutionException while getting business unit:  " + ee.getMessage());
        }

        submitProductButton.setOnClickListener(view ->
            {
                EditText productNameEditText = findViewById(R.id.productNameEditText);
                newProductName = productNameEditText.getText().toString();
                selectImageAndSaveToS3AndSaveToDb();
            }
        );

        Button signUpButton = findViewById(R.id.signupButton);
        signUpButton.setOnClickListener( onClick ->
            {
                Intent goToSignUpActivityIntent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(goToSignUpActivityIntent);
            }
        );

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener( onClick ->
            {
                Amplify.Auth.signOut(
                        () -> {Log.i(TAG, "Logout succeeded!");},
                        failure -> {Log.e(TAG, "Logout failed: " + failure.toString());}
                );
                Intent goToLoginActivityIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(goToLoginActivityIntent);
            }
        );
    }

    protected void selectImageAndSaveToS3AndSaveToDb()
    {
        // An intent to grab the file using a file picker
        // Part 1: Launch activity to pick file
        Intent imageFilePickingIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageFilePickingIntent.setType("*/*");  // only allows one kind or category of file
        imageFilePickingIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg", "image/png"});
        // ActivityResultLauncher<Intent> activityResultLauncher = getImagePickingActivityResultLauncher();  Don't do this here! It doesn't work
        activityResultLauncher.launch(imageFilePickingIntent);  // opens the file picker
    }

    protected ActivityResultLauncher<Intent> getImagePickingActivityResultLauncher()
    {
        ActivityResultLauncher<Intent> imagePickingActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                // Part 2: Take the result of the image picking activity and turn it into an input stream
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if (result.getResultCode() == Activity.RESULT_OK)
                    {
                        if (result.getData() != null)
                        {
                            Uri pickedImageFileUri = result.getData().getData();

                            try
                            {
                                InputStream pickedImageInputStream = getContentResolver().openInputStream(pickedImageFileUri);
                                String pickedImageFilename = getFilenameFromUri(pickedImageFileUri);
                                Log.i(TAG, "Succeeded in getting input stream from file on phone! Filename is: " + pickedImageFilename);
                                // Part 3: Upload our InputStream for our picked image file to S3
                                uploadInputStreamToS3(pickedImageInputStream, pickedImageFilename);

                            } catch (FileNotFoundException fnfe)
                            {
                                Log.e(TAG, "Could not get file from file picker! " + fnfe.getMessage(), fnfe);
                            }

                        }
                    }
                }
            }
        );

        return imagePickingActivityResultLauncher;
    }

    // Taken from https://stackoverflow.com/a/25005243/16889809
    @SuppressLint("Range")
    protected String getFilenameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null))
            {
                if (cursor != null && cursor.moveToFirst())
                {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    protected void uploadInputStreamToS3(InputStream pickedImageFileInputStream, String pickedImageFilename)
    {
        Amplify.Storage.uploadInputStream(
            pickedImageFilename,
            pickedImageFileInputStream,
            success ->
            {
                Log.i(TAG, "Succeeded in getting uploading file to S3! Key is: " + success.getKey());
                // Step 4: Save item to db with image key
                saveShoppingItemToDb(success.getKey());
            },
            failure ->
            {
                Log.e(TAG, "Failed in uploading file to S3 with filename: " + pickedImageFilename + " with error: " + failure.getMessage(), failure);
            }
        );
    }

    protected void saveShoppingItemToDb(String awsImageKey)
    {
        ShoppingItem shoppingItem = ShoppingItem.builder()
            .businessUnit(businessUnitUnknown)
            .itemName(newProductName)
            .timeAdded(new Temporal.DateTime(new Date(), 0))  // UTC time
            .productCategory(unknownProductCategoryType)
            .productImageKey(awsImageKey)
            .build();
            Amplify.API.mutate(
                ModelMutation.create(shoppingItem),
                success -> {
                    Log.i(TAG, "Created new shopping item in db! Name is: " + newProductName);
                },
                failure -> {
                    Log.e(TAG, "Failed to create new shopping item in db with name: " + newProductName + " and cause: " + failure.getMessage(), failure);
                }
            );

        List<ShoppingItem> shoppingItemList2 = shoppingRecyclerViewAdapter.getShoppingItemList();
        shoppingItemList2.add(shoppingItem);

        shoppingRecyclerViewAdapter.notifyDataSetChanged();
        Toast.makeText(MainActivity.this, "Shopping Item saved!", Toast.LENGTH_SHORT).show();
    }

    protected void getShoppingItemList()
    {
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
    }

    protected CompletableFuture<BusinessUnit> getUnknownBusinessUnitFuture()
    {
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

        return businessUnitCompletableFuture;
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        getShoppingItemList();

        //String userNickname = sharedPreferences.getString(USER_NICKNAME_KEY, "");
        String userNickname = "";

        AuthUser currentUser = Amplify.Auth.getCurrentUser();  // TODO: Use actual nickname here instead

        if (currentUser != null)
        {
            userNickname = currentUser.getUsername();
        }

        /*if (currentUser != null)
        {
            String username = currentUser.getUsername();

            Amplify.Auth.fetchUserAttributes(
                    success -> {
                        String nickname = "";

                        for (AuthUserAttribute userAttribute : success)
                        {
                            if (userAttribute.getKey().getKeyString().equals("nickname"))
                            {
                                nickname = userAttribute.getValue();
                            }
                        }
                        Log.i(TAG, "Fetch user attributes succeeded, with username: " + username + " and nickname: " + nickname);},
                    failure -> {Log.i(TAG, "Fetch user attributes failed: " + failure.toString());});
        }*/

        if (!userNickname.equals(""))
        {
            ((TextView) findViewById(R.id.userNicknameMainTextView)).setText(res.getString(R.string.WelcomeUsername, userNickname));
        }
    }
}