package com.example.simplecurriculum.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.simplecurriculum.R;

import java.util.List;

import okhttp3.Cookie;

public class CookieUtils {

    public void saveCookieToSHP(Context context, Cookie cookie) {
        SharedPreferences sp = context.getSharedPreferences(context.getResources().getString(R.string.COOKIE_SHP_NAME), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(context.getResources().getString(R.string.COOKIE_KEY),cookie.toString());
        editor.apply();
    }

    public String getCookieToSHP(Context context) {
        SharedPreferences sp = context.getSharedPreferences(context.getResources().getString(R.string.COOKIE_SHP_NAME), Context.MODE_PRIVATE);
        String defValue = context.getResources().getString(R.string.defStringValue);
        return sp.getString(context.getResources().getString(R.string.COOKIE_KEY),defValue);
    }

}
