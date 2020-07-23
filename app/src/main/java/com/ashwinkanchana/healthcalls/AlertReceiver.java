package com.ashwinkanchana.healthcalls;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.ashwinkanchana.healthcalls.App.CHANNEL_1_ID;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_IMAGE;
import static com.ashwinkanchana.healthcalls.Constants.PREF_MEDICATION;
import static com.ashwinkanchana.healthcalls.Constants.PREF_MEDICATION_LIST;

public class AlertReceiver extends BroadcastReceiver {
    ArrayList<Medication> medicationList;
    private NotificationManagerCompat notificationManager;
    private Context context;
    private Bitmap bitmap;
    private int id;
    private int reqCode;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.i("alarm",context.toString());
        id  = intent.getIntExtra("index",0);
        reqCode  = intent.getIntExtra("code",0);
        notificationManager = NotificationManagerCompat.from(context);
        loadData();
    }





    private void loadData(){
        SharedPreferences prefs = context.getSharedPreferences(PREF_MEDICATION,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(PREF_MEDICATION_LIST,null);
        Type type = new TypeToken<ArrayList<Medication>>() {}.getType();
        medicationList = gson.fromJson(json,type);


        if(medicationList==null){
            medicationList = new ArrayList<Medication>();
        }






        loadImage(id);
        sendNotifiation(id);



    }

    private void loadImage(int index) {
        SharedPreferences pref = Objects.requireNonNull(context.getSharedPreferences(medicationList.get(index).getName(),MODE_PRIVATE));
        String previouslyEncodedImage = pref.getString(PREF_KEY_IMAGE, "");
        if( !previouslyEncodedImage.equalsIgnoreCase("") ){
            byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        }
    }




    private void sendNotifiation(int index) {
        Intent intent = new Intent(context,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                reqCode,intent,0);

        Intent broadcastIntent = new Intent(context,NotificationService.class);
        //Intent broadcastIntent = new Intent(context,NotificationReceiver.class);
        //broadcastIntent.putExtra("index",index);
        /*Bundle args = new Bundle();
        args.putSerializable("ARRAYLIST",(Serializable)medicationList);
        args.putInt("INDEX",id);
        args.putInt("CODE",reqCode);
        broadcastIntent.putExtra("BUNDLE",args);

        PendingIntent actionIntent = PendingIntent.getBroadcast(context,reqCode,
                broadcastIntent,PendingIntent.FLAG_UPDATE_CURRENT);*/
        Bundle args = new Bundle();
        args.putSerializable("ARRAYLIST",(Serializable)medicationList);
        args.putInt("INDEX",id);
        args.putInt("CODE",reqCode);
        broadcastIntent.putExtra("BUNDLE",args);

        PendingIntent actionIntent = PendingIntent.getService(context,reqCode,
                broadcastIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(),R.drawable.logo_small);


        Notification notification = new NotificationCompat.Builder(
                context,CHANNEL_1_ID)
                .setSmallIcon(R.drawable.outline_add_alert_black_48dp)
                .setContentTitle(medicationList.get(index).getName())
                .setContentText(medicationList.get(index).getDoseage()+" "+medicationList.get(index).getUnit())
                .setLargeIcon(largeIcon)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(null)
                )
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setOngoing(true)
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE)
                //.setOnlyAlertOnce(true)
                .addAction(R.mipmap.ic_launcher,"DONE",actionIntent)
                .build();
        notificationManager.notify(reqCode,notification);

    }
}
