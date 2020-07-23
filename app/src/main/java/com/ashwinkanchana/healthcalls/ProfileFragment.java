package com.ashwinkanchana.healthcalls;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.ashwinkanchana.healthcalls.Constants.PACKAGE_NAME;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_DOB;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_DP;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_GENDER;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_IMAGE;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_IS_FIRST_TIME;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_IS_LOGGED_IN;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_NAME;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_PHONE;
import static com.ashwinkanchana.healthcalls.Constants.PREF_MEDICATION;
import static com.ashwinkanchana.healthcalls.Constants.PREF_MEDICATION_LIST;
import static com.ashwinkanchana.healthcalls.R.drawable.outline_person_black_48dp;


public class ProfileFragment extends Fragment {


    private TextView name,age,gender,phone;
    private CircleImageView dp;
    private SharedPreferences prefs;
    private String currentPhotoPath;
    private ExtendedFloatingActionButton logout;



    public static ProfileFragment newInstance() { return new ProfileFragment(); }

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            Calendar calendar = Calendar.getInstance();
            int cur = calendar.get(Calendar.YEAR);


            View view = inflater.inflate(R.layout.fragment_profile, container, false);
            setHasOptionsMenu(true);
            prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(PACKAGE_NAME, MODE_PRIVATE);
            name = view.findViewById(R.id.name_text_view);
            age = view.findViewById(R.id.age_text_view);
            gender = view.findViewById(R.id.gender_text_view);
            phone = view.findViewById(R.id.phone_text_view);
            dp = view.findViewById(R.id.profile_image);
            logout = view.findViewById(R.id.logout_fab);
            name.setText(prefs.getString(PREF_KEY_NAME,""));
            age.setText(String.valueOf(cur-prefs.getInt(PREF_KEY_DOB,65)));
            gender.setText(prefs.getString(PREF_KEY_GENDER,"1"));
            phone.setText(prefs.getString(PREF_KEY_PHONE,""));


            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(PACKAGE_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();


                        editor.putString(PREF_KEY_NAME,null);
                        editor.putInt(PREF_KEY_DOB,-1);
                        editor.putInt(PREF_KEY_GENDER,-1);
                        editor.putInt(PREF_KEY_GENDER,-1);
                        editor.putBoolean(PREF_KEY_IS_LOGGED_IN,false);
                        editor.putBoolean(PREF_KEY_IS_FIRST_TIME,true);

                        editor.apply();
                        removeData();

                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);

                }
            });
            return view;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(PACKAGE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();


            editor.putString(PREF_KEY_NAME,null);
            editor.putInt(PREF_KEY_DOB,-1);
            editor.putInt(PREF_KEY_GENDER,-1);
            editor.putInt(PREF_KEY_GENDER,-1);
            editor.putBoolean(PREF_KEY_IS_LOGGED_IN,false);
            editor.putBoolean(PREF_KEY_IS_FIRST_TIME,true);

            editor.apply();
            removeData();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void removeData() {
        ArrayList<Medication> medicationList = new ArrayList<>();
        SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(PREF_MEDICATION,MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(medicationList);
        editor.putString(PREF_MEDICATION_LIST,json);
        editor.apply();

    }



}
