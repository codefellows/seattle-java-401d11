package com.edy.buystuff;

import android.app.Application;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.core.Amplify;

public class BuyStuffApplication extends Application
{
    public final static String TAG =  "edy_buystuff_application";

    @Override
    public void onCreate() {
        super.onCreate();
        try
        {
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());
        } catch (AmplifyException ae)
        {
            Log.e(TAG, "Error initializing Amplify: " + ae.getMessage(), ae);
        }
    }
}
