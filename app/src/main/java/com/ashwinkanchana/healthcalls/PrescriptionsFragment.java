package com.ashwinkanchana.healthcalls;


import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.ashwinkanchana.healthcalls.App.CHANNEL_1_ID;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_IMAGE;
import static com.ashwinkanchana.healthcalls.Constants.PREF_MEDICATION;
import static com.ashwinkanchana.healthcalls.Constants.PREF_MEDICATION_LIST;


public class PrescriptionsFragment extends Fragment {


    private RecyclerView mRecyclerView;
    public PrescriptionsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Medication> medicationList;
    protected DataReceiver dataReceiver;
    public static final String REC_DATA = "REC_DATA";
    private ExtendedFloatingActionButton fab;

    public static PrescriptionsFragment newInstance() {
        return new PrescriptionsFragment();
    }

    public PrescriptionsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataReceiver = new DataReceiver();
        IntentFilter intentFilter = new IntentFilter(REC_DATA);

        getActivity().registerReceiver(dataReceiver, intentFilter);
    }
    private class DataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            int option= intent.getIntExtra("option", 1);
            int index= intent.getIntExtra("index", 0);

           update(option,index);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prescriptions, container,false);
        mRecyclerView = view.findViewById(R.id.recyclerview);
        fab = view.findViewById(R.id.add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), AddMedicineActivity.class);
                Objects.requireNonNull(getActivity()).startActivity(myIntent);
            }
        });
        loadData();
        buildRecyclerView();



        return view;
    }


    private void loadData(){
        SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(PREF_MEDICATION,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(PREF_MEDICATION_LIST,null);
        Type type = new TypeToken<ArrayList<Medication>>() {}.getType();
        medicationList = gson.fromJson(json,type);


        if(medicationList==null){
            medicationList = new ArrayList<Medication>();
        }

    }


    private void buildRecyclerView() {
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new PrescriptionsAdapter(medicationList);
        mAdapter.setOnItemClickListener(this::itemClick);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void itemClick(int i) {
        ImageDialog imageDialog = new ImageDialog(medicationList.get(i).getName(),i);
        imageDialog.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "image");
    }

    public void update(int option, int index){
        Log.i("fragment",String.valueOf(option));
        Log.i("fragment",String.valueOf(index));
        if(option==2){
            Log.i("option","2");



            Intent intent = new Intent(getActivity(), AddMedicineActivity.class);
            Bundle args = new Bundle();
            args.putSerializable("ARRAYLIST",(Serializable)medicationList);
            args.putInt("INDEX",index);
            intent.putExtra("BUNDLE",args);
            startActivity(intent);

        }
        if(option==3){
            //remove
            Log.i("size before", String.valueOf(medicationList.size()));

            medicationList.remove(index);
            mAdapter.notifyItemRemoved(index);
            mAdapter.notifyItemRangeChanged(index,medicationList.size());
            store();



        }
    }

    public void store() {
        SharedPreferences prefs = getActivity().getSharedPreferences(PREF_MEDICATION,MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(medicationList);
        editor.putString(PREF_MEDICATION_LIST,json);
        editor.apply();
        buildRecyclerView();
    }


    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}
