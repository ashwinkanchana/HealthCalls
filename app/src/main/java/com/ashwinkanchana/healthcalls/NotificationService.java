package com.ashwinkanchana.healthcalls;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import static com.ashwinkanchana.healthcalls.App.CHANNEL_2_ID;
import static com.ashwinkanchana.healthcalls.Constants.PACKAGE_NAME;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_IMAGE;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_NAME;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_PHONE;
import static com.ashwinkanchana.healthcalls.Constants.PREF_MEDICATION;
import static com.ashwinkanchana.healthcalls.Constants.PREF_MEDICATION_LIST;

public class NotificationService extends Service {
    private ArrayList<Medication> medicationList;
    private int index;
    private int reqCode;
    private Notification notification;
    @Override
    public void onCreate() {
        super.onCreate();
        //firstcall
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        if(intent.hasExtra("BUNDLE")) {
            Bundle args = intent.getBundleExtra("BUNDLE");
            index = args.getInt("INDEX");
            reqCode = args.getInt("CODE");
            medicationList = (ArrayList<Medication>) args.getSerializable("ARRAYLIST");
        }
        medicationList.get(index).setInventory(medicationList.get(index).getInventory()-medicationList.get(index).getDoseage());
        storeData();
        if(medicationList.get(index).getInventory()<5 && medicationList.get(index).getInventory()>0) {
            SharedPreferences prefs = this.getSharedPreferences(PACKAGE_NAME, MODE_PRIVATE);
            String name = prefs.getString(PREF_KEY_NAME, "");
            String phone = prefs.getString(PREF_KEY_PHONE, "").trim();
            String message = String.format("HealthCalls: Time to refill %s for %s", medicationList.get(index).getName(),name);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, message, null, null);
            Toast.makeText(this, "Message sent", Toast.LENGTH_LONG).show();
        }
        notification = new NotificationCompat.Builder(this,CHANNEL_2_ID)
                .setContentTitle("Processing...")
                .setContentText("Updating data")
                .setSmallIcon(R.drawable.outline_calendar_today_black_48dp)
                .setAutoCancel(true)
                .build();

        startForeground(reqCode,notification);

        return START_NOT_STICKY;
        //every call

    }



    private void loadData() {
        SharedPreferences prefs = getSharedPreferences(PREF_MEDICATION,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(PREF_MEDICATION_LIST,null);
        Type type = new TypeToken<ArrayList<Medication>>() {}.getType();
        medicationList = gson.fromJson(json,type);


        if(medicationList==null){
            medicationList = new ArrayList<Medication>();
        }

    }



    private void storeData() {
        SharedPreferences prefs = getSharedPreferences(PREF_MEDICATION,MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(medicationList);
        editor.putString(PREF_MEDICATION_LIST,json);
        editor.apply();
        Toast.makeText(getApplicationContext(),"Inventory updated",Toast.LENGTH_SHORT).show();
        NotificationManager notifyMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        notifyMgr.cancel(reqCode);
        stopSelf();

    }








    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
