package com.edy.buystuff.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.BusinessUnit;
import com.amplifyframework.datastore.generated.model.ShoppingItem;
import com.edy.buystuff.R;
import com.edy.buystuff.models.ProductCategoryEnum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class OrderFormActivity extends AppCompatActivity
{
    public final static String TAG =  "edy_buystuff_orderformactivity";
    private final MediaPlayer mp = new MediaPlayer();
    private String shoppingItemName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_form);

        Intent intent = getIntent();

        String productId;
        String productNameFromOtherApplication = "";

        if((intent.getType() != null) && (intent.getType().equals("text/plain")))  // launching intent from other app with text
        {
            productId = "";

            productNameFromOtherApplication = intent.getStringExtra(Intent.EXTRA_TEXT);
        }
        else  // launching this from our own app
        {
            productId = intent.getStringExtra(MainActivity.PRODUCT_ID_EXTRA_STRING);
            if (productId == null)
            {
                productId = "";
            }
        }

        Button productNameSpeechButton = findViewById(R.id.productNameSpeechButton);
        productNameSpeechButton.setOnClickListener(click ->
            {
                Amplify.Predictions.convertTextToSpeech(
                        shoppingItemName,
                        result -> playAudio(result.getAudioData()),
                        error -> Log.e(TAG, "Audio conversion of product name text failed", error)
                );
            });

        CompletableFuture<ShoppingItem> shoppingItemCompletableFuture = new CompletableFuture<>();  // Using this to handle async query below in a (sort of) synchronous manner

        String productId2 = productId;
        Amplify.API.query(
                ModelQuery.list(ShoppingItem.class),
                success ->
                {
                    ShoppingItem thisShoppingItem = null;
                    for (ShoppingItem shoppingItem : success.getData())
                    {
                        if(productId2.equals(shoppingItem.getId()))
                        {
                            thisShoppingItem = shoppingItem;
                            break;
                        }
                        Log.i(TAG, "Succeeded read of ShoppingItem: " + shoppingItem.getItemName());
                    }

                    if (thisShoppingItem != null)
                    {
                        getImageFileFromS3AndSetImageView(thisShoppingItem.getProductImageKey());

                        ShoppingItem thisShoppingItem2 = thisShoppingItem;  // TODO: investigate how to make this better
                        runOnUiThread(() ->
                        {
                            TextView productNameOrderFormTextView = findViewById(R.id.productNameOrderFormTextView);
                            shoppingItemName = thisShoppingItem2.getItemName();
                            productNameOrderFormTextView.setText(shoppingItemName);
                            Spinner productCategorySpinner = findViewById(R.id.productCategorySpinner);
                            int spinnerPosition = getSpinnerIndex(productCategorySpinner, thisShoppingItem2.getProductCategory());
                            productCategorySpinner.setSelection(spinnerPosition);
                        });
                        shoppingItemCompletableFuture.complete(thisShoppingItem2);
                    }
                    else
                    {
                        shoppingItemCompletableFuture.complete(null);
                    }
                },
                failure -> {
                    Log.i(TAG, "Failed");
                    shoppingItemCompletableFuture.complete(null);
                }
        );

        TextView productNameOrderFormTextView = findViewById(R.id.productNameOrderFormTextView);
        productNameOrderFormTextView.setText(productNameFromOtherApplication);

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

        TextView productLatitudeTextView = findViewById(R.id.productLatitudeTextView);
        productLatitudeTextView.setText(shoppingItem.getProductLatitude());
        TextView productLongitudeTextView = findViewById(R.id.productLongitudeTextView);
        productLongitudeTextView.setText(shoppingItem.getProductLongitude());
        TextView productCityTextView = findViewById(R.id.productCityTextView);
        productCityTextView.setText(shoppingItem.getProductCity());

        Button saveShoppingItemButton = findViewById(R.id.saveShoppingItemButton);
        ShoppingItem shoppingItem2 = shoppingItem;
        saveShoppingItemButton.setOnClickListener(button ->
            {
                if (shoppingItem2 != null)
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
            }
        );
    }

    protected void playAudio(InputStream data) {
        File mp3File = new File(getCacheDir(), "audio.mp3");

        try (OutputStream out = new FileOutputStream(mp3File)) {
            byte[] buffer = new byte[8 * 1_024];
            int bytesRead;
            while ((bytesRead = data.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            mp.reset();
            mp.setOnPreparedListener(MediaPlayer::start);
            mp.setDataSource(new FileInputStream(mp3File).getFD());
            mp.prepareAsync();
        } catch (IOException error) {
            Log.e(TAG, "Error writing audio file for text to speech with product name", error);
        }
    }

    protected void getImageFileFromS3AndSetImageView(String s3ImageKey)
    {
        if (s3ImageKey != null)
        {
            Amplify.Storage.downloadFile(
                s3ImageKey,
                new File(getApplicationContext().getFilesDir(), s3ImageKey),
                successCall ->
                {
                    Log.i(TAG, "Image file downloaded from S3 successfully with filename: " + successCall.getFile().getName());
                    runOnUiThread(() ->
                        {
                            ImageView shoppingItemImageView = findViewById(R.id.shoppingItemImageView);
                            shoppingItemImageView.setImageBitmap(BitmapFactory.decodeFile(successCall.getFile().getPath()));
                        }
                    );
                },
                failureCall ->
                {
                    Log.i(TAG, "Image file was not downloaded from S3 successfully! Key is: " + s3ImageKey + " and error: " + failureCall.getMessage(), failureCall);
                }
            );
        }
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