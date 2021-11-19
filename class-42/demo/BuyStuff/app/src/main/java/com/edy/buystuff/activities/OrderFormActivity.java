package com.edy.buystuff.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.BusinessUnit;
import com.amplifyframework.datastore.generated.model.ShoppingItem;
import com.edy.buystuff.R;
import com.edy.buystuff.models.ProductCategoryEnum;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

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
    private Resources resources;

    private InterstitialAd orderFormInterstitialAd;
    private RewardedAd orderFormRewardedAd;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_form);

        resources = getResources();

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

        // Ads section

        // Banner ad

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView orderFormAdView = findViewById(R.id.adView);
        AdRequest bannerAdRequest = new AdRequest.Builder().build();
        orderFormAdView.loadAd(bannerAdRequest);

        // Interstitial ad

        AdRequest interstitialAdRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", interstitialAdRequest,
            new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    orderFormInterstitialAd = interstitialAd;
                    Log.i(TAG, "onAdLoaded");
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error
                    Log.i(TAG, loadAdError.getMessage());
                    orderFormInterstitialAd = null;
                }
            });

        Button showInterstitialAdButton = findViewById(R.id.showInterstitialAdButton);
        showInterstitialAdButton.setOnClickListener(click ->
            {
                if (orderFormInterstitialAd != null) {
                    orderFormInterstitialAd.show(OrderFormActivity.this);
                } else {
                    Log.d(TAG, "The interstitial ad wasn't ready yet.");
                }
            });

        // Rewarded ad

        AdRequest rewardedAdRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
            rewardedAdRequest, new RewardedAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error.
                    Log.d(TAG, loadAdError.getMessage());
                    orderFormRewardedAd = null;
                }

                @Override
                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                    orderFormRewardedAd = rewardedAd;
                    Log.d(TAG, "Ad was loaded.");
                }
            });

        Button showRewardedAdButton = findViewById(R.id.showRewardedAdButton);
        showRewardedAdButton.setOnClickListener(click ->
            {
                if (orderFormRewardedAd != null) {
                    setFullScreenCallback();
                    Activity activityContext = OrderFormActivity.this;
                    orderFormRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.
                            Log.d(TAG, "The user earned the reward.");
                            int rewardAmount = rewardItem.getAmount();
                            String rewardType = rewardItem.getType();
                            runOnUiThread( () ->
                                {
                                    TextView rewardTextView = findViewById(R.id.rewardTextView);
                                    rewardTextView.setText(resources.getString(R.string.RewardText, rewardAmount + " " + rewardType));
                                }
                            );
                        }
                    });
                } else {
                    Log.d(TAG, "The rewarded ad wasn't ready yet.");
                }
            });
    }

    protected void setFullScreenCallback()
    {
        orderFormRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad was shown.");
                runOnUiThread(() -> Toast.makeText(OrderFormActivity.this, "Rewarded ad was shown!", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                Log.d(TAG, "Ad failed to show.");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad was dismissed.");
                orderFormRewardedAd = null;
                runOnUiThread(() -> Toast.makeText(OrderFormActivity.this, "Rewarded ad was dismissed!", Toast.LENGTH_SHORT).show());
            }
        });
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