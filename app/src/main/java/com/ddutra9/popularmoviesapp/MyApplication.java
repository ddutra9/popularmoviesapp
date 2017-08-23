package com.ddutra9.popularmoviesapp;

import android.app.Application;
import com.facebook.stetho.Stetho;


/**
 * Created by donato on 23/08/17.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }
}
