package com.example.sivaprasad.trackme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static com.example.sivaprasad.trackme.R.layout.activity_login;
import static com.example.sivaprasad.trackme.R.layout.activity_main;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_WRITE_SETTINGS=116,REQUEST_SMS = 110 , REQUEST_CALL=111,REQUEST_READ_PHONE_STATE=112,REQUEST_COARSE_LOCATION=113,REQUEST_FINE_LOCATION=114;
    private static final int REQUEST_BLUETOOTH=115;

    String mPermission = android.Manifest.permission.ACCESS_FINE_LOCATION;
    int REQUEST_CODE_PERMISSION = 2;

    DatabaseHelper mydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean hasPermissionFineLocation =(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED);
        if(!hasPermissionFineLocation){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_FINE_LOCATION);
        }
        boolean hasPermissionCoarseLocation =(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED);
        if(!hasPermissionCoarseLocation){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_COARSE_LOCATION);
        }
        boolean hasPermissionSms = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionSms) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    REQUEST_SMS);
        }

        boolean hasBluetoth = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionSms) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_ADMIN},
                    REQUEST_SMS);
        }

        boolean hasPermissionCall = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionCall) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CALL);
        }

        boolean hasPermissionReadPhoneState = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionCall) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        }

        boolean hasPermissionWrite = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionWrite) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_SETTINGS},
                    REQUEST_WRITE_SETTINGS);
        }

        // Permissions for Location
        try{
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{mPermission},REQUEST_CODE_PERMISSION);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        mydb=new DatabaseHelper(this);

        Cursor res = mydb.getAllData();

        if (res.getCount()>0) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
            finish();
        }
    }
}