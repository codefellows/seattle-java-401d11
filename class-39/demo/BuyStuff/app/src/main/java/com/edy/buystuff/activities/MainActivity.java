package com.edy.buystuff.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
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
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.datastore.generated.model.BusinessUnit;
import com.amplifyframework.datastore.generated.model.ShoppingItem;
import com.edy.buystuff.R;
import com.edy.buystuff.adapters.ShoppingRecyclerViewAdapter;
import com.edy.buystuff.models.ProductCategoryEnum;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
{
    public final static String TAG = "edy_buystuff_mainactivity";
    public final static String PRODUCT_ID_EXTRA_STRING = "productId";
    public final static String BUSINESS_UNIT_UNKNOWN_NAME = "Business Unit Unknown";
    public final static int GET_FINE_LOCATION_PERMISSION_CODE = 1;  // This is arbitrary, needs to be below 65536
    public final static int LOCATION_POLLING_INTERVAL = 5 * 1000;  // 5 seconds

    protected static SharedPreferences sharedPreferences;
    protected static Resources res;

    ShoppingRecyclerViewAdapter shoppingRecyclerViewAdapter;
    ActivityResultLauncher<Intent> activityResultLauncher;
    FusedLocationProviderClient fusedLocationProviderClient;
    Geocoder geocoder;

    // Need these to save our shopping item to the db
    String newProductName;
    String unknownProductCategoryType = ProductCategoryEnum.UNKNOWN.toString();
    BusinessUnit businessUnitUnknown;
    String productLatitude = "";
    String productLongitude = "";
    String productCity = "Unknown City";

    Uri pickedImageFileUri;
    String pickedImageFilename;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // TODO: Figure out how to make this not flicker before redirecting

        activityResultLauncher = getImagePickingActivityResultLauncher();

        Intent intent = getIntent();

        if((intent.getType() != null) && (intent.getType().startsWith("image/")))  // launching intent from other app with image
        {
            Uri incomingFileUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (incomingFileUri != null)
            {
                try
                {
                    pickedImageFileUri = incomingFileUri;
                    pickedImageFilename = getFilenameFromUri(incomingFileUri);
                    InputStream incomingImageFileInputStream = getContentResolver().openInputStream(incomingFileUri);
                    ImageView previewImageMainActivityImageView = findViewById(R.id.previewImageMainActivityImageView);
                    previewImageMainActivityImageView.setImageBitmap(BitmapFactory.decodeStream(incomingImageFileInputStream));
                }
                catch (FileNotFoundException fnfe)
                {
                    Log.e(TAG, "Could not get file from file picker! " + fnfe.getMessage(), fnfe);
                }
            }
        }

        AuthUser currentUser = Amplify.Auth.getCurrentUser();

        if (currentUser == null)
        {
            Intent goToLoginActivityIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(goToLoginActivityIntent);
        }

        getShoppingItemList();

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
                saveToS3AndSaveToDb();
            }
        );

        Button selectImageButton = findViewById(R.id.selectImageButton);
        selectImageButton.setOnClickListener( onClick ->
            {
                selectImage();
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

        // Part 2: Request location permission
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GET_FINE_LOCATION_PERMISSION_CODE);
        // Part 3: Set up a FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        // Not part of lab: geocoding
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        // Bonus Part: Check permission before we use it
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        // Part 4: Actually get the current location
        fusedLocationProviderClient.flushLocations();
        // This will grab current location which is more up to date
        fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken()
        {
            @Override
            public boolean isCancellationRequested()
            {
                return false;
            }

            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener)
            {
                return null;
            }
        }).addOnSuccessListener(location ->
        {
            Log.i(TAG, "Our latitude: " + location.getLatitude());
            productLatitude = Double.toString(location.getLatitude());
            Log.i(TAG, "Our longitude: " + location.getLongitude());
            productLongitude = Double.toString(location.getLongitude());
            try
            {
                List<Address> addressGuesses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);  // want the most accurate result
                Address bestAddressGuess = addressGuesses.get(0);
                productCity = bestAddressGuess.getLocality();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        });
        // This will grab the last location the app last fetched, which is less up-to-date
        /*fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location ->
        {
            Log.i(TAG, "Our latitude: " + location.getLatitude());
            Log.i(TAG, "Our longitude: " + location.getLongitude());
        });*/

        // Not in lab, here is how to use location subscriptions to get locations repeatedly

        /*LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(LOCATION_POLLING_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback locationCallback = new LocationCallback()
        {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult)
            {
                super.onLocationResult(locationResult);

                try
                {
                    String address = geocoder.getFromLocation(
                        locationResult.getLastLocation().getLatitude(),
                        locationResult.getLastLocation().getLongitude(),
                        1).get(0).getAddressLine(0);

                    Log.i(TAG, "Current address: " + address);
                } catch (IOException ioe)
                {
                    Log.e(TAG, "Could not get subscribed location; " + ioe.getMessage(), ioe);
                }

            }

            // Don't need for our purposes really
            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability)
            {
                super.onLocationAvailability(locationAvailability);
            }
        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());*/
    }

    protected void selectImage()
    {
        // An intent to grab the file using a file picker
        // Part 1: Launch activity to pick file
        Intent imageFilePickingIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageFilePickingIntent.setType("*/*");  // only allows one kind or category of file
        imageFilePickingIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg", "image/png"});
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
                            pickedImageFileUri = result.getData().getData();

                            try
                            {
                                pickedImageFilename = getFilenameFromUri(pickedImageFileUri);
                                InputStream pickedImageInputStream = getContentResolver().openInputStream(pickedImageFileUri);
                                Log.i(TAG, "Succeeded in getting input stream from file on phone! Filename is: " + pickedImageFilename);
                                ImageView previewImageMainActivityImageView = findViewById(R.id.previewImageMainActivityImageView);
                                previewImageMainActivityImageView.setImageBitmap(BitmapFactory.decodeStream(pickedImageInputStream));

                                // BONUS: Launch an implicit intent that other apps can use (not in your lab)
                                /*Intent outgoingIntent = new Intent();
                                outgoingIntent.setType("image/*");
                                outgoingIntent.setAction(Intent.ACTION_SEND);
                                outgoingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                outgoingIntent.putExtra(Intent.EXTRA_STREAM, pickedImageFileUri);
                                startActivity(outgoingIntent);*/

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

    protected void saveToS3AndSaveToDb()
    {
        if (pickedImageFilename == null)
        {
            runOnUiThread(() ->
                {
                    Toast.makeText(MainActivity.this, "Please select a file before submitting!", Toast.LENGTH_SHORT).show();
                });
        }
        else
        {
            InputStream pickedImageInputStream = null;
            try
            {
                pickedImageInputStream = getContentResolver().openInputStream(pickedImageFileUri);
            } catch (FileNotFoundException fnfe)
            {
                Log.e(TAG, "Could not get input stream from preview image! " + fnfe.getMessage(), fnfe);
            }

            uploadInputStreamToS3(pickedImageInputStream, pickedImageFilename);
        }
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
            .productLatitude(productLatitude)
            .productLongitude(productLongitude)
            .productCity(productCity)
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