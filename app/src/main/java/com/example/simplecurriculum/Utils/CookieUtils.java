package com.example.simplecurriculum.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.simplecurriculum.R;


import okhttp3.Cookie;

public class CookieUtils {

    /**
     * 保存cookie到shp文件
     * @param context 上下文
     * @param cookie cookie
     */
    public void saveCookieToSHP(Context context, Cookie cookie) {
        SharedPreferences sp = context.getSharedPreferences(context.getResources().getString(R.string.COOKIE_SHP_NAME), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String[] split = cookie.toString().split(";");
        editor.putString(context.getResources().getString(R.string.COOKIE_KEY),split[0]);
        editor.apply();
    }

    /**
     * 从shp文件中获取cookie
     * @param context 上下文
     * @return 返回cookie值
     */
    public String getCookieToSHP(Context context) {
        SharedPreferences sp = context.getSharedPreferences(context.getResources().getString(R.string.COOKIE_SHP_NAME), Context.MODE_PRIVATE);
        String defValue = context.getResources().getString(R.string.defStringValue);
        return sp.getString(context.getResources().getString(R.string.COOKIE_KEY),defValue);
    }

}
