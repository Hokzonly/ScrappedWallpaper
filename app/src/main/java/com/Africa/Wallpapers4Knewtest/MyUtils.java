package com.Africa.Wallpapers4Knewtest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

import com.Africa.Wallpapers4Knewtest.models.ads.AdsManager;
import com.Africa.Wallpapers4Knewtest.models.ads.AppControl;
import com.Africa.Wallpapers4Knewtest.models.ads.MainResponse;


public class MyUtils {

    // ads and app control data -->
    // IMPORTANT: To enable categories, set "ShowCategories": true in your JSON configuration

    public static String BaseUrl = "https://ismailokhiyi.xyz/Amazon11/com.Africa.Wallpapers4Knewtest/ads.json";
    public static String NicheLink = "https://wallpapercave.com/paw-patrol-phone-wallpapers";
    public static MainResponse mainResponse;
    public static AdsManager adsManager;
    public static AppControl appControl;

    public static String SharedPrefName = "AmazonWallpapers";
    public static String SharedPrefPremium = "AmazonWallpapersPremium";
    public static String SharedPrefPremiumLifetime = "AmazonWallpapersPremiumLifetime";

    public static String OnesignalAccepted = "OnesignalAccepted";
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = connectivityManager.getActiveNetwork();
                return network != null;
            } else {
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            }
        }
        return false;
    }






}
