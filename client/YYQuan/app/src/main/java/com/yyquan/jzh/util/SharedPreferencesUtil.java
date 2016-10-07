package com.yyquan.jzh.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/1/1.
 */
public class SharedPreferencesUtil {


    public static void setBoolean(Context context, String type, String key, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(type, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value).commit();


    }

    public static boolean getBoolean(Context context, String type, String key) {
        SharedPreferences preferences = context.getSharedPreferences(type, context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }
    public static boolean getBoolean(Context context, String type, String key,boolean defaults) {
        SharedPreferences preferences = context.getSharedPreferences(type, context.MODE_PRIVATE);
        return preferences.getBoolean(key, defaults);
    }


    public static void setString(Context context, String type, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(type, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value).commit();


    }

    public static String getString(Context context, String type, String key) {
        SharedPreferences preferences = context.getSharedPreferences(type, context.MODE_PRIVATE);
        return preferences.getString(key, "");


    }

    public static void setInt(Context context, String type, String key, int value) {
        SharedPreferences preferences = context.getSharedPreferences(type, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value).commit();


    }

    public static int getInt(Context context, String type, String key) {
        SharedPreferences preferences = context.getSharedPreferences(type, context.MODE_PRIVATE);
        return preferences.getInt(key, 0);


    }


    public static int getFriendMessageNumber_addone(Context context, String user) {
        int before_number = getInt(context, "message_number", user);
        return before_number;
    }

    public static void setFriendMessageNumber_addone(Context context, String user) {
        int before_number = getInt(context, "message_number", user);
        before_number += 1;
        setInt(context, "message_number", user, before_number);
    }

    public static void setFriendMessageNumber_subone(Context context, String user) {
        int before_number = getInt(context, "message_number", user);
        before_number -= 1;
        setInt(context, "message_number", user, before_number);
    }

    public static void setFriendMessageNumber_zero(Context context, String user) {

        setInt(context, "message_number", user, 0);
    }


}
