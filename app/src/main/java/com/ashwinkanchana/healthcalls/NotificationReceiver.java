package com.ashwinkanchana.healthcalls;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int index = intent.getIntExtra("index",0);
        Toast.makeText(context,String.valueOf(index),Toast.LENGTH_SHORT).show();


    }
}
