package com.watniwat.android.myapplication;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Niwat on 03-Jan-18.
 */

public class MyApp extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public interface SimpleCallback {
        void callback(Object data);
    }
}


