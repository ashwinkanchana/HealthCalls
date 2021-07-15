package com.ashwinkanchana.healthcalls;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.ashwinkanchana.healthcalls.Constants.PREF_MEDICATION;
import static com.ashwinkanchana.healthcalls.Constants.PREF_MEDICATION_LIST;

public class LoadData {
    private Context context;
    public ArrayList<Medication> medicationList;


    public LoadData(Context context) {
        this.context = context;
    }


    //get data from shared preferences
    public ArrayList<Medication> load(){
        SharedPreferences prefs = context.getSharedPreferences(PREF_MEDICATION,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(PREF_MEDICATION_LIST,null);
        Type type = new TypeToken<ArrayList<Medication>>() {}.getType();
        medicationList = gson.fromJson(json,type);


        if(medicationList==null){
            medicationList = new ArrayList<Medication>();
        }

        return medicationList;

    }

}
