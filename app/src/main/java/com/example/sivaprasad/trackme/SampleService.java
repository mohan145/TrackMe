package com.example.sivaprasad.trackme;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.Service;
import android.app.WallpaperManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.os.IBinder;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Locale;

import static java.security.AccessController.getContext;


public class SampleService extends Service implements TextToSpeech.OnInitListener{

    MediaPlayer mp;
    DatabaseHelper mydb;

    @Nullable
    @Override

    public IBinder onBind(Intent intent) {
        return null;
    }

    public WifiConfiguration conf;
    public String ssid;
    public String password;

    //Battery variables
    private IntentFilter ifilter;
    private Intent batteryStatus;

    //Wallpaper Change variable's
    DisplayMetrics displayMetrics;
    int width, height;

    //Audio variables
    AudioManager am;

    private TextToSpeech tts;

    public void onCreate() {

        mydb = new DatabaseHelper(this);

        //Battery variable's intilization
        ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = getApplicationContext().registerReceiver(null, ifilter);

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        tts = new TextToSpeech(this, this);

        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText, String sender) {
                String tempSt = messageText.substring(0, 10);

                String message = "";
                String hashcode = "";
                mydb = new DatabaseHelper(getApplicationContext());
                Cursor res = mydb.getAllData();

                while (res.moveToNext()) {
                    hashcode = res.getString(1);
                }

                Toast.makeText(getApplicationContext(), hashcode, Toast.LENGTH_LONG).show();
                //ed.getText().toString();

                String[] words = messageText.split("\\s");
                if (words[1].equals(hashcode)) {
                    switch (words[2].toLowerCase()) {
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
                            manageHotspot(words[3], words[4]);
                            break;
                        case "brightness":
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                setScreenBrightess(words[3]);
                            }
                            else
                            {
                                String mode=words[4];
                                int range = 0;
                                if (mode.toLowerCase() == "low") {
                                    range = 0;
                                } else if (mode.toLowerCase() == "high") {
                                    range = 255;
                                }
                                Settings.System.putInt(getApplicationContext().getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,range);
                            }
                            break;
                        case "location":
                            getLatLong(sender);
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
                        default:
                            sendSms(sender,"InValid Operation!! text Help to get the list of operations.");
                    }
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {

        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText, String sender) {

                String tempSt = messageText.substring(0, 10);

                String message = "";
                String hashcode = "";
                mydb = new DatabaseHelper(getApplicationContext());

                Cursor res = mydb.getAllData();

                while (res.moveToNext()) {
                    hashcode = res.getString(1);
                }

                String[] words = messageText.split("\\s");

                if (words[1].equals(hashcode)) {
                    switch (words[2].toLowerCase()) {
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
                            manageHotspot(words[3], words[4]);
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
                        case "location":
                            getLatLong(sender);
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
                            for(int i=0;i<Integer.parseInt(words[4]);i++)
                            {
                                speechAlert(words[3]);
                            }
                            break;
                        default:
                            sendSms(sender,"InValid Operation!! text Help to get the list of operations.");
                    }
                }
            }
        });

        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setScreenBrightess(String mode) {

        int range = 0;
        if (mode.toLowerCase() == "low") {
            range = 0;
        } else if (mode.toLowerCase() == "high") {
            range = 255;
        }

        // Get app context object.
        Context context = getApplicationContext();

        // Check whether has the write settings permission or not.
        boolean settingsCanWrite = hasWriteSettingsPermission(context);

        // If do not have then open the Can modify system settings panel.
        if(!settingsCanWrite) {
            changeWriteSettingsPermission(context);
        }else {
            changeScreenBrightness(context, range);
        }
    }


    public void manageHotspot(String wifiName, String Password) {

        WifiConfiguration conf = new WifiConfiguration();
        SampleService.this.ssid = wifiName;
        SampleService.this.password = Password;

        //WifiAccessManager.set_open(MainActivity.this.getApplicationContext(), MainActivity.this.ssid, conf);
        WifiAccessManager.set_wpa2(SampleService.this.getApplicationContext(), SampleService.this.ssid, SampleService.this.password, conf);
    }

    public boolean isApOn(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        try {
            Method method = wm.getClass().getDeclaredMethod("isWifiEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wm);
        } catch (Throwable ignored) {
            return false;
        }
    }


    public void manageLoction() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(getApplication().LOCATION_SERVICE);
        boolean status = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);


        if (status == true) {
            Toast.makeText(getApplicationContext(), "location is on", Toast.LENGTH_LONG);

        } else {
            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(i);
        }
    }

    @SuppressLint("MissingPermission")
    public void makeCall(String sender) {


        Intent in = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
        startActivity(in);

        Intent call = new Intent(Intent.ACTION_CALL);
        call.setData(Uri.parse("tel:" + sender));

        call.putExtra("com.android.phone.extra.slot", 1); //For sim 2
        startActivity(call);
    }


    public void manageBluetooth() {

        BluetoothAdapter adapter;
        adapter = BluetoothAdapter.getDefaultAdapter();
        adapter.enable();
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


    public void invokerWifi(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (wifi.isWifiEnabled())
            wifi.setWifiEnabled(false);
        else
            wifi.setWifiEnabled(true);
    }

    @SuppressLint("MissingPermission")
    public void getImei(String sender) {

        String imei = "";
        TelephonyManager tm = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        imei = "IMEI: " + tm.getDeviceId().toString();
        sendSms(sender, imei);
        Toast.makeText(getApplicationContext(), imei, Toast.LENGTH_LONG);
    }

    public void sendSms(String sender, String msg) {
        try {
            SmsManager smsmanager = SmsManager.getDefault();
            smsmanager.sendTextMessage(sender, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "SENT", Toast.LENGTH_SHORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void lockScreenNotification(String mes) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(android.R.drawable.ic_dialog_alert);
        builder.setContentTitle(mes);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());

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
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.YELLOW);
        // text size in pixels
        paint.setTextSize((int) (100 * scale));
        // text shadow
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
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, screenBrightnessValue);
    }
}
