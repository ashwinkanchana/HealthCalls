package com.ashwinkanchana.healthcalls;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Time;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static com.ashwinkanchana.healthcalls.Constants.ONCE_A_DAY;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_IMAGE;
import static com.ashwinkanchana.healthcalls.Constants.PREF_MEDICATION;
import static com.ashwinkanchana.healthcalls.Constants.PREF_MEDICATION_LIST;
import static com.ashwinkanchana.healthcalls.Constants.THRICE_A_DAY;
import static com.ashwinkanchana.healthcalls.Constants.TWICE_A_DAY;

public class AddMedicineActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private TextInputLayout nameLayout,inventoryLayout,quantityLayout;
    private EditText medicationNameEditText,medicationQuantityEditText,medicationInventoryEditText;
    private Spinner unitSpinner;
    private RadioGroup radioGroup;
    private RadioButton radioButton1,radioButton2,radioButton3;
    private int medicationFrequency;
    private String medicationName;
    private String medicationUnit;
    private int medicationQuantity;
    private int medicationInventory;
    private int expiryMonth,expiryYear;
    private Button timeOne,timeTwo,timeThree,takePhoto,done;
    private ImageView imageView;
    private TextView time1,time2,time3;
    private int clickedTime,editIndex;
    private Calendar c1,c2,c3;
    private boolean t1,t2,t3,photo,newphoto;
    private String currentPhotoPath;
    private boolean isEdit;
    private ArrayList<Medication> medicationList;
    private String previouslyEncodedImage;
    private Bitmap bitmap;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meds);
        Intent intent = getIntent();
        //If there is a bundle then edit existing medication
        if(intent.hasExtra("BUNDLE")){
            Bundle args = intent.getBundleExtra("BUNDLE");
            editIndex = args.getInt("INDEX");
            isEdit = true;
            medicationList =  (ArrayList<Medication>)args.getSerializable("ARRAYLIST");
            medicationFrequency = medicationList.get(editIndex).getFrequency();
            try {
                c1 = medicationList.get(editIndex).getC1();
                c2 = medicationList.get(editIndex).getC2();
                c3 = medicationList.get(editIndex).getC3();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            loadData();
            isEdit = false;
        }


        expiryYear = 2030;
        expiryMonth = 12;
        c1 = Calendar.getInstance();
        c2 = Calendar.getInstance();
        c3 = Calendar.getInstance();
        timeOne = findViewById(R.id.time_one);
        timeTwo = findViewById(R.id.time_two);
        timeThree = findViewById(R.id.time_three);
        time1 = findViewById(R.id.time1);
        time2 = findViewById(R.id.time2);
        time3 = findViewById(R.id.time3);
        takePhoto  =findViewById(R.id.take_photo);
        imageView = findViewById(R.id.image_view);
        done = findViewById(R.id.done);
        //addExpiry = findViewById(R.id.expiryButton);
        medicationNameEditText = findViewById(R.id.medication_name_edit_text);
        medicationQuantityEditText = findViewById(R.id.quantity_edittext);
        medicationInventoryEditText = findViewById(R.id.inventory_edit_text);
        nameLayout = findViewById(R.id.medication_name_layout);
        quantityLayout = findViewById(R.id.quantity_layout);
        inventoryLayout = findViewById(R.id.inventory_layout);
        unitSpinner = findViewById(R.id.unit_spinner);
        radioGroup = findViewById(R.id.radio_group);
        radioButton1 = findViewById(R.id.radio_one);
        radioButton2 = findViewById(R.id.radio_two);
        radioButton3 = findViewById(R.id.radio_three);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == radioButton1.getId()) {
                    medicationFrequency = ONCE_A_DAY;
                    time2.setVisibility(View.GONE);
                    time3.setVisibility(View.GONE);
                    timeTwo.setVisibility(View.GONE);
                    timeThree.setVisibility(View.GONE);

                }
                if(checkedId == radioButton2.getId()) {
                    medicationFrequency = TWICE_A_DAY;
                    time2.setVisibility(View.VISIBLE);
                    time3.setVisibility(View.GONE);
                    timeTwo.setVisibility(View.VISIBLE);
                    timeThree.setVisibility(View.GONE);

                }
                if(checkedId == radioButton3.getId()) {
                    medicationFrequency = THRICE_A_DAY;
                    time2.setVisibility(View.VISIBLE);
                    time3.setVisibility(View.VISIBLE);
                    timeTwo.setVisibility(View.VISIBLE);
                    timeThree.setVisibility(View.VISIBLE);

                }
            }
        });
        photo = false;
        if(isEdit){
            photo = true;
            if(medicationList.get(editIndex).getFrequency()==2) {
                radioButton2.setChecked(true);
                c1=medicationList.get(editIndex).getC1();
                c2=medicationList.get(editIndex).getC2();
                t1 = true;
                t2 = true;
                t3 = false;
            }
            else if(medicationList.get(editIndex).getFrequency()==3) {
                radioButton3.setChecked(true);
                c1=medicationList.get(editIndex).getC1();
                c2=medicationList.get(editIndex).getC2();
                c3=medicationList.get(editIndex).getC3();
                t1 = true;
                t2 = true;
                t3 = true;
            }
            else {
                radioButton1.setChecked(true);
                t1 = true;
                t2 = false;
                t3 = false;
            }
        }
        else{
            t1 = false;
            t2 = false;
            t3 = false;
            radioButton1.setChecked(true);
        }

        timeOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedTime = 1;
                DialogFragment timePicker1;
                if(isEdit)
                    timePicker1= new TimePickerFragment(c1);
                else
                    timePicker1 = new TimePickerFragment();
                timePicker1.show(getSupportFragmentManager(),"TIME_1");

            }
        });
        timeTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedTime = 2;
                DialogFragment timePicker2;
                if(isEdit)
                    timePicker2= new TimePickerFragment(c2);
                else
                    timePicker2 = new TimePickerFragment();
                timePicker2.show(getSupportFragmentManager(),"TIME_2");

            }
        });
        timeThree.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickedTime = 3;
                DialogFragment timePicker3;
                if(isEdit)
                    timePicker3= new TimePickerFragment(c3);
                else
                    timePicker3 = new TimePickerFragment();
                timePicker3.show(getSupportFragmentManager(),"TIME_3");

            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateName() | !validateQuantity() | !validateInventory())
                    return;
                else
                {
                    addMedication();
                    if(photo&& (medicationFrequency==1&&t1||medicationFrequency==2&&t1&&t2||medicationFrequency==3&&t1&&t2&&t3) )
                        storeData();
                    else if(!photo){
                            LinearLayout layout = findViewById(R.id.add_medication_layout);
                            Snackbar snackbar;
                            snackbar = Snackbar.make(layout, "Add a photo", Snackbar.LENGTH_LONG);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(getResources().getColor(R.color.colorRed));
                            snackbar.show();

                    }else{
                        LinearLayout layout = findViewById(R.id.add_medication_layout);
                        Snackbar snackbar;
                        snackbar = Snackbar.make(layout, "Set time to get reminder", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(getResources().getColor(R.color.colorRed));
                        snackbar.show();
                    }
                }
            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateName())
                    return;
                else
                    takePhoto();
            }
        });


        SharedPreferences pref = getSharedPreferences(medicationName,MODE_PRIVATE);
        previouslyEncodedImage = pref.getString(PREF_KEY_IMAGE, "");

        if( !previouslyEncodedImage.equalsIgnoreCase("") ){
            byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            imageView.setImageBitmap(bitmap);

        }

        if(isEdit){
           pref = getSharedPreferences(medicationList.get(editIndex).getName(),MODE_PRIVATE);
           previouslyEncodedImage = pref.getString(PREF_KEY_IMAGE, "");

            if( !previouslyEncodedImage.equalsIgnoreCase("") ){
                byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                imageView.setImageBitmap(bitmap);
            }
            medicationNameEditText.setText(medicationList.get(editIndex).getName());
            int i,u=0;
            String[] arr = getResources().getStringArray(R.array.medication_type);
            for(i=0; i<4;i++){
                if(arr[i].equals(medicationList.get(editIndex).getUnit()))
                    u=i;

            }
            unitSpinner.setSelection(u);
            medicationQuantityEditText.setText(String.valueOf(medicationList.get(editIndex).getDoseage()));
            medicationInventoryEditText.setText(String.valueOf(medicationList.get(editIndex).getInventory()));
            time1.setText(medicationList.get(editIndex).getRem1());
            time2.setText(medicationList.get(editIndex).getRem2());
            time3.setText(medicationList.get(editIndex).getRem3());

        }
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

    private void takePhoto() {
        String filename = "photo";
        File fileDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File imageFile = File.createTempFile(filename,".jpg",fileDirectory);
            Uri imageUri = FileProvider.getUriForFile(AddMedicineActivity.this,
                    "com.ashwinkanchana.healthcalls.fileproviders",
                    imageFile);

            currentPhotoPath =  imageFile.getAbsolutePath();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
            startActivityForResult(intent,1);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            imageView.setImageBitmap(bitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 12, baos);
            byte[] b = baos.toByteArray();

            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            medicationName = medicationNameEditText.getText().toString();
            SharedPreferences pref = getSharedPreferences(medicationName,MODE_PRIVATE);
            SharedPreferences.Editor edit=pref.edit();
            edit.putString(PREF_KEY_IMAGE,encodedImage);
            edit.apply();
            photo = true;

            if(isEdit)
                newphoto = true;
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(clickedTime==1){
            c1.set(Calendar.HOUR_OF_DAY,hourOfDay);
            c1.set(Calendar.MINUTE,minute);
            c1.set(Calendar.SECOND,0);
            t1 = true;
            if(isEdit)
                medicationList.get(editIndex).setC1(c1);
            String  s = "Reminder 1 - ";
            s += DateFormat.getTimeInstance(DateFormat.SHORT).format(c1.getTime());
            time1.setText(s);

        }
        if(clickedTime==2){
            c2.set(Calendar.HOUR_OF_DAY,hourOfDay);
            c2.set(Calendar.MINUTE,minute);
            c2.set(Calendar.SECOND,0);
            t2 = true;
            if(isEdit)
                medicationList.get(editIndex).setC2(c2);
            String  s = "Reminder 2 - ";
            s += DateFormat.getTimeInstance(DateFormat.SHORT).format(c2.getTime());
            time2.setText(s);

        }
        if(clickedTime==3){
            c3.set(Calendar.HOUR_OF_DAY,hourOfDay);
            c3.set(Calendar.MINUTE,minute);
            c3.set(Calendar.SECOND,0);
            t3 = true;
            if(isEdit)
                medicationList.get(editIndex).setC3(c3);
            String  s = "Reminder 3 - ";
            s += DateFormat.getTimeInstance(DateFormat.SHORT).format(c3.getTime());
            time3.setText(s);

        }
    }

    private void addMedication() {
        medicationName = medicationNameEditText.getText().toString().trim();
        medicationQuantity = Integer.parseInt(medicationQuantityEditText.getText().toString());
        medicationInventory = Integer.parseInt(medicationInventoryEditText.getText().toString());
        medicationUnit = unitSpinner.getSelectedItem().toString();

        if(medicationFrequency == 1 && t1)
            addMedicationItem();
        else if(medicationFrequency == 2 && t1 && t2)
            addMedicationItem();
        else if(medicationFrequency == 3 && t1 && t2 && t3)
            addMedicationItem();
    }

    private void addMedicationItem() {
        if(!isEdit) {
            if (medicationFrequency == 1)
                medicationList.add(new Medication(medicationName,
                        medicationUnit,
                        medicationFrequency,
                        medicationQuantity,
                        medicationInventory,
                        expiryMonth,
                        expiryYear,
                        medicationName,
                        c1));
            else if (medicationFrequency == 2)
                medicationList.add(new Medication(medicationName,
                        medicationUnit,
                        medicationFrequency,
                        medicationQuantity,
                        medicationInventory,
                        expiryMonth,
                        expiryYear,
                        medicationName,
                        c1, c2));
            else if (medicationFrequency == 3)
                medicationList.add(new Medication(medicationName,
                        medicationUnit,
                        medicationFrequency,
                        medicationQuantity,
                        medicationInventory,
                        expiryMonth,
                        expiryYear,
                        medicationName,
                        c1, c2, c3));
        }
        else {

            if (medicationFrequency == 1) {
                Log.i("edit","here");
                medicationList.get(editIndex).setName(medicationName);
                medicationList.get(editIndex).setUnit(medicationUnit);
                medicationList.get(editIndex).setFrequency(medicationFrequency);
                medicationList.get(editIndex).setDoseage(medicationQuantity);
                medicationList.get(editIndex).setInventory(medicationInventory);
                medicationList.get(editIndex).setExpiryMonth(expiryMonth);
                medicationList.get(editIndex).setExpiryYear(expiryYear);
                medicationList.get(editIndex).setC1(c1);
                medicationList.get(editIndex).setC2(null);
                medicationList.get(editIndex).setC3(null);
            }
            else if (medicationFrequency == 2){
                medicationList.get(editIndex).setName(medicationName);
                medicationList.get(editIndex).setUnit(medicationUnit);
                medicationList.get(editIndex).setFrequency(medicationFrequency);
                medicationList.get(editIndex).setDoseage(medicationQuantity);
                medicationList.get(editIndex).setInventory(medicationInventory);
                medicationList.get(editIndex).setExpiryMonth(expiryMonth);
                medicationList.get(editIndex).setExpiryYear(expiryYear);
                medicationList.get(editIndex).setC1(c1);
                medicationList.get(editIndex).setC2(c2);
                medicationList.get(editIndex).setC3(null);
            }
            else if (medicationFrequency == 3){
                medicationList.get(editIndex).setName(medicationName);
                medicationList.get(editIndex).setUnit(medicationUnit);
                medicationList.get(editIndex).setFrequency(medicationFrequency);
                medicationList.get(editIndex).setDoseage(medicationQuantity);
                medicationList.get(editIndex).setInventory(medicationInventory);
                medicationList.get(editIndex).setExpiryMonth(expiryMonth);
                medicationList.get(editIndex).setExpiryYear(expiryYear);
                medicationList.get(editIndex).setC1(c1);
                medicationList.get(editIndex).setC2(c2);
                medicationList.get(editIndex).setC3(c3);
            }

        }
    }

    private void storeData() {
        String medicationName;
        if(isEdit){
            if(newphoto)
                medicationName = medicationList.get(editIndex).getName();
            else
                medicationName = medicationNameEditText.getText().toString();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 12, baos);
            byte[] b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            SharedPreferences pref = getSharedPreferences(medicationName,MODE_PRIVATE);
            SharedPreferences.Editor edit=pref.edit();
            edit.putString(PREF_KEY_IMAGE,encodedImage);
            edit.apply();
        }
        Log.i("frequency",String.valueOf(medicationFrequency));
        SharedPreferences prefs = getSharedPreferences(PREF_MEDICATION,MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(medicationList);
        editor.putString(PREF_MEDICATION_LIST,json);
        editor.apply();
        Intent intent = new Intent(AddMedicineActivity.this, MainActivity.class);
        startActivity(intent);
    }


    private boolean validateName() {
        if (medicationNameEditText.length() < 1) {
            nameLayout.setError("Enter the name of medication");
            return false;
        } else {
            nameLayout.setError(null);
            return true;
        }

    }

    private boolean validateQuantity() {
        if (medicationQuantityEditText.length() < 1) {
            quantityLayout.setError("Enter a valid dose");
            return false;
        } else {
            quantityLayout.setError(null);
            return true;
        }

    }
    private boolean validateInventory() {
        if (medicationInventoryEditText.length() < 1) {
            inventoryLayout.setError("Enter available quantity of medication");
            return false;
        } else {
            inventoryLayout.setError(null);
            return true;
        }

    }



}
