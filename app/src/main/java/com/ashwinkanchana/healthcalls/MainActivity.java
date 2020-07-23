package com.ashwinkanchana.healthcalls;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import static com.ashwinkanchana.healthcalls.Constants.PREF_MEDICATION;
import static com.ashwinkanchana.healthcalls.Constants.PREF_MEDICATION_LIST;
import static com.ashwinkanchana.healthcalls.PrescriptionsFragment.REC_DATA;

public class MainActivity extends AppCompatActivity implements ImageDialog.ImageDialogListener {


    private ViewPager viewPager;
    private BottomNavigationView navigation;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private Calendar c1,c2,c3;
    private DialogListener listener;
    private ArrayList<Medication> medicationList;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            toolbar.setTitle(item.getTitle());
            switch (item.getItemId()) {
                case R.id.navHome:
                    toolbar.setTitle(R.string.app_name);
                    viewPager.setCurrentItem(0,false);
                    appBarLayout.setExpanded(true,true);
                    return true;
                case R.id.navPrescription:
                    toolbar.setTitle(navigation.getMenu().getItem(1).getTitle());
                    viewPager.setCurrentItem(1,false);
                    appBarLayout.setExpanded(true,true);
                    return true;
                case R.id.navProfile:
                    toolbar.setTitle(navigation.getMenu().getItem(2).getTitle());
                    viewPager.setCurrentItem(2,false);
                    appBarLayout.setExpanded(true,true);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appBarLayout = findViewById(R.id.app_bar);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = findViewById(R.id.view_pager);
        FragmentPageAdapter adapter = new FragmentPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount() - 1);
        navigation = findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        loadData();


        setAlarms();
    }




    private static class FragmentPageAdapter extends FragmentPagerAdapter {


        public FragmentPageAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }


        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return HomeFragment.newInstance();
                case 1:
                    return PrescriptionsFragment.newInstance();
                case 2:
                    return ProfileFragment.newInstance();

            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }


    }

    private void loadData(){
        SharedPreferences prefs = getSharedPreferences(PREF_MEDICATION,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(PREF_MEDICATION_LIST,null);
        Type type = new TypeToken<ArrayList<Medication>>() {}.getType();
        medicationList = gson.fromJson(json,type);


        if(medicationList==null){
           medicationList = new ArrayList<Medication>();
        }

    }




    private void setAlarms() {
        Medication medication;
        for(int i=0; i<medicationList.size();i++){
           medication = medicationList.get(i);
            if(medication.getFrequency()==1){
                startAlarm(i,medication.getC1(),medication.getId1());
            }else if(medication.getFrequency()==2){
                startAlarm(i,medication.getC1(),medication.getId1());
                startAlarm(i,medication.getC2(),medication.getId2());
            }else if(medication.getFrequency()==3){
                startAlarm(i,medication.getC1(),medication.getId1());
                startAlarm(i,medication.getC2(),medication.getId2());
                startAlarm(i,medication.getC3(),medication.getId3());
            }
        }
    }

    private void startAlarm(int index,Calendar c ,int requestCode){
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this,AlertReceiver.class);
        intent.putExtra("index",index);
        intent.putExtra("code",requestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,requestCode,intent,0);
        if(c.before(Calendar.getInstance())){
            c.add(Calendar.DATE,1);
        }
        //alarmManager.setExact(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);

    }

    private void cancelAlarm(int requestCode){
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this,AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,requestCode,intent,0);
        alarmManager.cancel(pendingIntent);

    }


    @Override
    public void option(int option, int index) {
        //listener.option(option,index);
        Intent retIntent = new Intent(REC_DATA);
        retIntent.putExtra("option", option);
        retIntent.putExtra("index", index);
        sendBroadcast(retIntent);

        /*if(option==2){
            Log.i("option main","2");
            Intent intent = new Intent(this, AddMedicineActivity.class);
            Bundle args = new Bundle();
            args.putSerializable("ARRAYLIST",(Serializable)loadData.medicationList);
            args.putInt("INDEX",index);
            intent.putExtra("BUNDLE",args);
            startActivity(intent);

        }
        if(option==3){
            //remove
            Log.i("option main","3");
            loadData.medicationList.remove(index);
        }*/
    }






    public interface DialogListener {
        void option(int option,int index);
    }
}
