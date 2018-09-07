package com.example.sivaprasad.trackme;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.lang.reflect.Method;

/**
 * Created by mohan on 3/9/2018.
 */
public class WifiAccessManager {
    private static final String SSID = "Portable Wifi Hotspot";

    WifiAccessManager() {
    }

    public static boolean setWifiApState(Context context, boolean enabled, WifiConfiguration conf) {
        try {
            WifiManager mWifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
            if (enabled) {
                mWifiManager.setWifiEnabled(false);
            }
            mWifiManager.addNetwork(conf);
            return ((Boolean) mWifiManager.getClass().getMethod("setWifiApEnabled", new Class[]{WifiConfiguration.class, Boolean.TYPE}).invoke(mWifiManager, new Object[]{conf, Boolean.valueOf(enabled)})).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void set_wpa2(Context context, String ssid, String password, WifiConfiguration conf) {
        conf.SSID = ssid;
        conf.preSharedKey = password;
        conf.hiddenSSID = false;
        conf.allowedAuthAlgorithms.set(1);
        conf.allowedProtocols.set(1);
        conf.allowedKeyManagement.set(4);
        conf.allowedPairwiseCiphers.set(2);
        conf.allowedGroupCiphers.set(3);
        setWifiApState(context, true, conf);
    }

    public static void set_wpa(Context context, String ssid, String password, WifiConfiguration conf) {
        conf.SSID = ssid;
        conf.preSharedKey = password;
        conf.allowedAuthAlgorithms.set(1);
        conf.allowedProtocols.set(1);
        conf.allowedProtocols.set(0);
        conf.allowedKeyManagement.set(1);
        setWifiApState(context, true, conf);
    }

    public static void set_open(Context context, String ssid, WifiConfiguration conf) {
        conf.SSID = ssid;
        conf.allowedKeyManagement.set(0);
        setWifiApState(context, true, conf);
    }

    public static WifiConfiguration getWifiApConfiguration() {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = SSID;
        conf.allowedKeyManagement.set(0);
        return conf;
    }

    public static boolean isApOn(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled", new Class[0]);
            method.setAccessible(true);
            return ((Boolean) method.invoke(wifimanager, new Object[0])).booleanValue();
        } catch (Throwable th) {
            return false;
        }
    }
}