package com.ashwinkanchana.healthcalls;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.ashwinkanchana.healthcalls.Constants.PACKAGE_NAME;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_DOB;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_GENDER;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_NAME;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_PHONE;


public class HomeFragment extends Fragment {

    private int permissionCheck;
    private String coordinates,address;
    private static final int REQUEST_CODE_LOCATION_PERMISSION =1;
    private static final int REQUEST_CODE_SMS =2;
    private TextView textView1,textView2;
    private Button btn;
    private ResultReceiver resultReceiver;
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public HomeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        resultReceiver = new AddressResultReceiver(new Handler());
        textView1 = view.findViewById(R.id.latlong);
        textView2 = view.findViewById(R.id.address);
        btn = view.findViewById(R.id.panic_button);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION_PERMISSION
                    );
                }


                if(ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.SEND_SMS},REQUEST_CODE_SMS);
                    }

                getLocation();






            }
        });

        return view;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION&& grantResults.length>0){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){

            }else{
                Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == REQUEST_CODE_SMS&& grantResults.length>0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }

    }


    private void getLocation(){


        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(getActivity())
                .requestLocationUpdates(locationRequest,new LocationCallback(){

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getActivity())
                                .removeLocationUpdates(this);

                        if(locationResult != null &&locationResult.getLocations().size()>0){
                            int latestLocationIndex = locationResult.getLocations().size()-1;
                            double latitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            coordinates = String.format("Latitude: %s\nLongitude: %s", latitude, longitude);
                            Location location = new Location("providerNA");
                            textView1.setText(coordinates);
                            location.setLatitude(latitude);
                            location.setLongitude(longitude);
                            fetchAddressFromLatLong(location);
                        }

                    }
                }, Looper.getMainLooper());



    }


    private void fetchAddressFromLatLong(Location location){
        Intent intent = new Intent(getActivity(),FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER,resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA,location);
        Objects.requireNonNull(getActivity()).startService(intent);
    }
    private class AddressResultReceiver extends ResultReceiver{
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if(resultCode==Constants.SUCCESS_RESULT){
                address = resultData.getString(Constants.RESULT_DATA_KEY);
                textView2.setText(address);
                SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(PACKAGE_NAME, MODE_PRIVATE);
                String name = prefs.getString(PREF_KEY_NAME,"");
                String phone = prefs.getString(PREF_KEY_PHONE,"").trim();
                String message ="ALERT MESSAGE FROM: "+name+"\nCurrent Location:\n"+coordinates+"\nAddress:"+address;
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone,null,message,null,null);
                Toast.makeText(getActivity(),"Message sent",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getActivity(), resultData.getString(Constants.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
            }
        }
    }






}

