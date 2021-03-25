package com.oman.sayakil;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;


public class App extends MultiDexApplication {


    public static final String CHANNEL_ID = "default-channel";
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(getApplicationContext());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "LocationChannel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);

        }
    }


}
