package com.ashwinkanchana.healthcalls;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Objects;

import static com.ashwinkanchana.healthcalls.Constants.KEY_GENDER_FEMALE;
import static com.ashwinkanchana.healthcalls.Constants.KEY_GENDER_MALE;
import static com.ashwinkanchana.healthcalls.Constants.KEY_GENDER_OTHERS;
import static com.ashwinkanchana.healthcalls.Constants.PACKAGE_NAME;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_DOB;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_GENDER;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_IS_FIRST_TIME;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_IS_LOGGED_IN;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_NAME;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_PHONE;

public class LoginActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {


    private TextInputLayout nameLayout;
    private TextInputLayout phoneLayout;
    private EditText nameEditText,phoneEditText;
    private NumberPicker numberPicker;
    private Spinner genderSpinner;
    private Button startButton;
    private String name;
    private int dob;
    private String gender;
    private SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dob = 1960;
        numberPicker = findViewById(R.id.year_picker);
        genderSpinner = findViewById(R.id.spinner);
        nameLayout = findViewById(R.id.text_input_name);
        phoneLayout = findViewById(R.id.text_input_phone);
        nameEditText = findViewById(R.id.name_edit_text);
        phoneEditText = findViewById(R.id.phone_edit_text);
        startButton = findViewById(R.id.get_started);
        numberPicker.setMinValue(1900);
        numberPicker.setMaxValue(2019);
        numberPicker.setValue(1960);
        numberPicker.setOnValueChangedListener(this);
        genderSpinner.setSelection(1);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                 gender = genderSpinner.getSelectedItem().toString();
                /*if(gender.equals(KEY_GENDER_FEMALE))
                    gender = "Female";
                if(gender.equals(KEY_GENDER_MALE))
                    gender = "Male";
                if(gender.equals(KEY_GENDER_OTHERS))
                    gender = "Non-Binary";*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }

        });
        prefs = this.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);


        findViewById(R.id.name_edit_text).requestFocus();
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateName() |!validatePhone() )
                    return;
                else
                    login();
            }
        });


    }
    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        dob = newVal;
    }
    private void login() {

        name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        SharedPreferences.Editor editor = prefs.edit();


        editor.putString(PREF_KEY_NAME,name);
        editor.putInt(PREF_KEY_DOB,dob);
        editor.putString(PREF_KEY_GENDER,gender);
        editor.putString(PREF_KEY_PHONE,phone);
        editor.putBoolean(PREF_KEY_IS_LOGGED_IN,true);
        editor.putBoolean(PREF_KEY_IS_FIRST_TIME,true);

        editor.apply();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(LoginActivity.this, AddMedicineActivity.class);
                LoginActivity.this.startActivity(intent);
                LoginActivity.this.finish();
            }
        }, 200);



    }
    private boolean validateName() {
        if (nameEditText.length() < 1) {
            nameLayout.setError("Enter your name");
            return false;
        } else {
            nameLayout.setError(null);
            return true;
        }

    }


    private boolean validatePhone() {
        if (phoneEditText.length()!=10) {
            phoneLayout.setError("Enter a valid phone number");
            return false;
        } else {
            phoneLayout.setError(null);
            return true;
        }

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }


    @Override
    protected void onStart() {
        super.onStart();
       // ignoreBatteryOptimizations();
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.SEND_SMS},
                2);
    }

    /*@SuppressLint("BatteryLife")
    private void ignoreBatteryOptimizations(){
        Intent intent = new Intent();
        String packageName = this.getPackageName();
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Objects.requireNonNull(pm).isIgnoringBatteryOptimizations(packageName))
                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            else
            {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
            }
        }
        this.startActivity(intent);
    }*/
}



