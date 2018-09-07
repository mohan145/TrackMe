package com.example.sivaprasad.trackme;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.provider.Telephony;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.security.Policy;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Main2Activity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    DatabaseHelper mydb;
    public WifiConfiguration conf;
    public String ssid;
    public String password;
    private static final int REQUEST_WRITE_SETTINGS=116,REQUEST_SMS = 110 , REQUEST_CALL=111,REQUEST_READ_PHONE_STATE=112,REQUEST_COARSE_LOCATION=113,REQUEST_FINE_LOCATION=114;
    private static final int REQUEST_BLUETOOTH=115;

    //Battery variables
    private IntentFilter ifilter;
    private Intent batteryStatus;

    //Wallpaper Change variable's
    DisplayMetrics displayMetrics;
    int width, height;

    //Audio variables
    AudioManager am;

    private TextToSpeech tts;

    // Location variables
    GPSTracker gps;
    double latitude,longitude;
    String mPermission = android.Manifest.permission.ACCESS_FINE_LOCATION;
    int REQUEST_CODE_PERMISSION = 2;

    //@RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        boolean hasPermissionFineLocation =(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED);
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
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Context context = getApplicationContext();
            boolean settingsCanWrite = hasWriteSettingsPermission(context);
            if (!settingsCanWrite) {
                changeWriteSettingsPermission(context);
            }
        }

        Button testbtn=findViewById(R.id.on_btn);

        testbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(getApplicationContext(), com.example.sivaprasad.trackme.SampleService.class));
                Toast.makeText(getApplicationContext(), "Service Started", Toast.LENGTH_LONG).show();
            }
        });



        Button stopbtn=findViewById(R.id.off_btn);

        stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(getApplicationContext(), com.example.sivaprasad.trackme.SampleService.class));
                Toast.makeText(getApplicationContext(), "Service Stoped", Toast.LENGTH_LONG).show();
            }
        });

        //Battery variable's intilization
        ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = getApplicationContext().registerReceiver(null, ifilter);

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        tts = new TextToSpeech(this, this);

        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText, String sender) {
                String message="";
                String[] words=messageText.split("\\s");
                String hashcode="";

                mydb = new DatabaseHelper(getApplicationContext());
                Cursor res = mydb.getAllData();

                if(res==null){

                }
                else {
                    while (res.moveToNext()) {
                        hashcode = res.getString(1);
                    }
                }

                if(words[1].equals(hashcode))
                {

                    switch (words[2].toLowerCase())
                    {
                        case "call":
                            makeCall(words[3]);
                            break;
                        case "imei":
                            getImei(sender);
                            break;
                        case "wifi":
                            invokerWifi(getApplicationContext());
                            break;
                        case "bluetooth":
                            manageBluetooth();
                            break;
                        case "notify":
                            lockScreenNotification(words[3]);
                            break;
                        case "gpson":
                            manageLoction();
                            break;
                        case "hotspot":
                            manageHotspot(words[3],words[4]);
                            break;
                        case "brightness":
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                setScreenBrightess(words[3]);
                            }
                            else
                            {
                                int range = 0;
                                String mode=words[4];
                                if (mode.toLowerCase() == "low") {
                                    range = 0;
                                } else if (mode.toLowerCase() == "high") {
                                    range = 255;
                                }

                                Settings.System.putInt(getApplicationContext().getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,range);
                            }
                            break;
                        case "help":
                            String helpMsg="defualt SMS FORMAT is \n XXXXXX functionality \n";
                            sendSms(sender,helpMsg);
                            break;
                        case "battery":
                            getBatteryDetails(sender);
                            break;
                        case "unmute":
                            unmutePhone();
                            break;
                        case "mute":
                            mutePhone();
                            break;
                        case "ring":
                            playRingtone();
                            break;
                        case "lost":
                            setWallpaper(words[3]);
                            playRingtone();
                            break;
                        case "alert":
                            for(int i=0;i<Integer.parseInt(words[4]);i++) {
                                speechAlert(words[3]);
                                break;
                            }
                        case "location":
                            getLatLong(sender);
                            break;
                        default:
                            sendSms(sender,"InValid Operation!! text Help to get the list of operations.");
                    }
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setScreenBrightess(String mode){

        int range=0;
        if(mode.toLowerCase()=="low"){
            range=0;
        }
        else if (mode.toLowerCase()=="high"){
            range=255;
        }

        Settings.System.putInt(getApplicationContext().getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,range);
        Context context = getApplicationContext();
        boolean settingsCanWrite = hasWriteSettingsPermission(context);

        // If do not have then open the Can modify system settings panel.
        if(!settingsCanWrite) {
            changeWriteSettingsPermission(context);
        }else {
            changeScreenBrightness(context, 1);
        }
    }

    public void manageHotspot(String wifiName,String Password){

        WifiConfiguration conf = new WifiConfiguration();
        Main2Activity.this.ssid = wifiName;
        Main2Activity.this.password = Password;

        //WifiAccessManager.set_open(Main2Activity.this.getApplicationContext(), MainActivity.this.ssid, conf);
        WifiAccessManager.set_wpa2(Main2Activity.this.getApplicationContext(), Main2Activity.this.ssid, Main2Activity.this.password, conf);
    }

    public boolean isApOn(Context context){
        WifiManager wm=(WifiManager)context.getSystemService(context.WIFI_SERVICE);
        try{
            Method method=wm.getClass().getDeclaredMethod("isWifiEnabled");
            method.setAccessible(true);
            return (Boolean)method.invoke(wm);
        }
        catch (Throwable ignored)
        {
            return false;
        }
    }

    public void manageLoction(){
        LocationManager locationManager =(LocationManager)getApplicationContext().getSystemService(getApplication().LOCATION_SERVICE);
        boolean status=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(status==true){
            Toast.makeText(getApplicationContext(),"location is on",Toast.LENGTH_LONG);

        }
        else
        {
            Intent i=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(i);
        }
    }

    @SuppressLint("MissingPermission")
    public void makeCall(String sender) {

        Intent in = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
        startActivity(in);

        Intent call = new Intent(Intent.ACTION_CALL);
        call.setData(Uri.parse("tel:"+sender));

        call.putExtra("com.android.phone.extra.slot", 1); //For sim 2
        startActivity(call);
    }

    public void manageBluetooth(){
        BluetoothAdapter adapter;
        adapter=BluetoothAdapter.getDefaultAdapter();
        adapter.enable();
    }


    public void invokerWifi(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (wifi.isWifiEnabled())
            wifi.setWifiEnabled(false);
        else
            wifi.setWifiEnabled(true);
    }

    @SuppressLint("MissingPermission")
    public void getImei( String sender) {

        String imei = "";
        TelephonyManager tm = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        imei = "IMEI: "+tm.getDeviceId().toString();
        sendSms(sender, imei);
        Toast.makeText(getApplicationContext(), imei, Toast.LENGTH_LONG);
    }

    public void sendSms(String sender,String msg)
    {
        try{
            SmsManager smsmanager=SmsManager.getDefault();
            smsmanager.sendTextMessage(sender,null,msg,null,null);
            Toast.makeText(getApplicationContext(),"SENT",Toast.LENGTH_SHORT);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    finish();
                    startActivity(getIntent());
                } else
                {
                }
            }

            case REQUEST_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                     finish();
                    startActivity(getIntent());
                } else
                {
                }
            }

            case REQUEST_READ_PHONE_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    finish();
                    startActivity(getIntent());
                } else
                {
                }
            }

            case REQUEST_COARSE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    finish();
                    startActivity(getIntent());
                } else
                {
                }
            }

            case REQUEST_BLUETOOTH: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    finish();
                    startActivity(getIntent());
                } else
                {
                }
            }


            case REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    finish();
                    startActivity(getIntent());
                } else
                {
                }
            }

        }

    }


    void lockScreenNotification(String mes)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(android.R.drawable.ic_dialog_alert);
        builder.setContentTitle(mes);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }


    public void getLatLong(String sender) {
        GPSTracker gps = new GPSTracker(getApplicationContext());
        double latitude, longitude;

        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            String latLongsms = "latitude :" + latitude + "\nlongitude :" + longitude;
            sendSms(sender, latLongsms);
            Toast.makeText(getApplicationContext(), "Latitude:" + latitude + "\nLongitude" + longitude, Toast.LENGTH_LONG).show();
        } else {
            gps.showSettingsAlert();
        }
    }

    void getBatteryDetails(String sender) {
        //Battery Percentage and Battery Temperature
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = (level / (float) scale) * 100;
        float BatteryTemp = (float) (batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)) / 10;
        String BatteryDetails = "Percentage :" + Float.toString(batteryPct) + "%\nTemporature :" + Float.toString(BatteryTemp) + " " + (char) 0x00B0 + "C";
        sendSms(sender, BatteryDetails);
        Toast.makeText(getApplicationContext(), BatteryDetails, Toast.LENGTH_LONG).show();
    }

    void setWallpaper(String gText) {
        //Wallpaper change code
        //Uses GetScreenWidthHeight() which is implemented in below
        GetScreenWidthHeight();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wall);
        //---------------------------
        Resources resources = getApplicationContext().getResources();
        float scale = resources.getDisplayMetrics().density;
        android.graphics.Bitmap.Config bitmapConfig =
                bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }

        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.YELLOW);
        paint.setTextSize((int) (100 * scale));
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width()) / 2;
        int y = (bitmap.getHeight() + bounds.height()) / 2;

        canvas.drawText(gText, x, y + 150, paint);

        //---------------------------
        WallpaperManager manger = WallpaperManager.getInstance(getApplicationContext());
        Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, width, height, true);
        //Code for Home Screen
        try {
            manger.suggestDesiredDimensions(width, height);
            manger.setBitmap(bitmap1);

            Toast.makeText(getApplicationContext(), width + "Home Wallpaper Changed" + height, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
        }
        //Code for Lock Screen works only with api level 24 and above
        if (Build.VERSION.SDK_INT > 23) {
            try {
                manger.setBitmap(bitmap1, null, true, WallpaperManager.FLAG_LOCK);
                //manger.setBitmap(bitmap1);
                manger.suggestDesiredDimensions(width, height);
                Toast.makeText(getApplicationContext(), "Lock Wallpaper Changed", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //This is Function is used in Changing WallPaper
    public void GetScreenWidthHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;
    }

    public void playRingtone() {

        am.setRingerMode(2);// Enable Normal Mode
        //For Alarm,Media,Ring,Notification------->Volume to 100%
        am.setStreamVolume(AudioManager.STREAM_MUSIC,100, 0);
        am.setStreamVolume(AudioManager.STREAM_RING, 100, 0);
        am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 100, 0);
        am.setStreamVolume(AudioManager.STREAM_ALARM, 100, 0);
        am.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        MediaPlayer thePlayer = MediaPlayer.create(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));

        try{
            thePlayer.setVolume((float) (audioManager.getStreamVolume(AudioManager.STREAM_RING) / 7.0),
                    (float) (audioManager.getStreamVolume(AudioManager.STREAM_RING) / 7.0));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        thePlayer.start();
    }

    public void unmutePhone()
    {
        am.setRingerMode(2);// Enable Normal Mode
        //For Alarm,Media,Ring,Notification------->Volume to 100%
        am.setStreamVolume(AudioManager.STREAM_MUSIC,100, 0);
        am.setStreamVolume(AudioManager.STREAM_RING, 100, 0);
        am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 100, 0);
        am.setStreamVolume(AudioManager.STREAM_ALARM, 100, 0);
        am.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
    }

    public void mutePhone()
    {
        am.setRingerMode(0);
    }

    public void speechAlert(String number) {
        //For Text to Speech
        //For this the CLASS should implement  TextToSpeech.OnInitListener and include onInIt() + onDestroy() functions which is mentioned below
        String text = "i lost my phone please contact to " + number;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text,TextToSpeech.QUEUE_ADD,null,null);
        } else {
            tts.speak(text, TextToSpeech.QUEUE_ADD, null);
        }
    }
    //This is Function is used in Text2Speech
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            //tts.shutdown();
        }
        super.onDestroy();
    }

    //This is Function is used in Text2Speech
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            tts.setSpeechRate((float)0.75);
            tts.setPitch(1);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }else {
                //btnSpeak.setEnabled(true);
                //speakOut();
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    // Check whether this app has android write settings permission.
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasWriteSettingsPermission(Context context)
    {
        boolean ret = true;
        // Get the result from below code.
        ret = Settings.System.canWrite(context);
        return ret;
    }

    // Start can modify system settings panel to let user change the write settings permission.
    private void changeWriteSettingsPermission(Context context)
    {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        context.startActivity(intent);
    }

    private void changeScreenBrightness(Context context, int screenBrightnessValue) {
        // Change the screen brightness change mode to manual.
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        // Apply the screen brightness value to the system, this will change the value in Settings ---> Display ---> Brightness level.
        // It will also change the screen brightness for the device.
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, screenBrightnessValue);
    }
    }