package com.teamig.whatswap.utils;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import java.util.Locale;

public abstract class PrefUtil {
    //keys
    public static final String USERNAME = "username";
    public static final String AGE = "userId";
    public static final String GENDER = "profilePic";
    public static final String COMMUNITY = "site";
    public static final String URL = "isStaff";

    public static void clearPreference(Context context){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = settings.edit();
        editor.clear();
        editor.apply();
    }


    public static void setBooleanPreference(String key,boolean info, Context context){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = settings.edit();
        editor.putBoolean(key, info);
        editor.apply();
    }

    public static boolean getBooleanPreference(String key,Context context){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getBoolean(key,false);
    }

    public static void setStringPreference(String key,String info, Context context){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = settings.edit();
        editor.putString(key, info);
        editor.apply();
    }

    public static String getStringPreference(String key,Context context){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(key, null);
    }

    public static void setLongPreference(String key,long info, Context context){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = settings.edit();
        editor.putLong(key, info);
        editor.apply();
    }

    public static long getLongPreference(String key,Context context){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getLong(key, -1);
    }

    public static void setIntPreference(String key,int info, Context context){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = settings.edit();
        editor.putInt(key, info);
        editor.apply();
    }

    public static int getIntPreference(String key,Context context){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getInt(key, -1);
    }

    public static void setDouble(String key, double value, Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = settings.edit();
        editor.putLong(key, Double.doubleToRawLongBits(value));
        editor.apply();
    }

    public static double getDouble(String key, Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return Double.longBitsToDouble(settings.getLong(key, -1));
    }
}