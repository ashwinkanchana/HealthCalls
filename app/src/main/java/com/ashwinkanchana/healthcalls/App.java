package com.ashwinkanchana.healthcalls;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";
    @SuppressLint("StaticFieldLeak")





    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            @SuppressLint("WrongConstant") NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_MAX

            );
            channel1.setDescription("channel 1");

            @SuppressLint("WrongConstant") NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Service",
                    NotificationManager.IMPORTANCE_MAX

            );
            channel2.setDescription("Service");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);

            manager.createNotificationChannel(channel2);
        }
    }
}
