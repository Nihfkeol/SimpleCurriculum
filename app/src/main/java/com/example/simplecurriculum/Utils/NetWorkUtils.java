package com.example.simplecurriculum.Utils;

import okhttp3.Callback;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class NetWorkUtils {
    OkHttpClient client;
    private final static String LOGIN_URL = "http://210.36.80.160/jsxsd/xk/LoginToXk";
    private final static String COURSE_URL = "http://210.36.80.160/jsxsd/xskb/xskb_list.do";
    private final static String USER_AGENT = "User-Agent";
    private final static String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36";

    /**
     * 构造方法
     */
    public NetWorkUtils() {
        this.client = new OkHttpClient.Builder().build();
    }

    /**
     * 带cookiejar的构造方法
     * @param cookieJar 持久化cookie
     */
    public NetWorkUtils(CookieJar cookieJar) {
        this.client = new OkHttpClient.Builder().cookieJar(cookieJar).build();
    }

    /**
     * 判断是否登录成功的网络连接方法
     * @param studentId 学号
     * @param password 密码
     * @param callback 回调方法
     */
    public void isLogin(String studentId, String password, Callback callback) {
        FormBody.Builder builder = new FormBody.Builder();
        FormBody formBody = builder.add("USERNAME", studentId)
                .add("PASSWORD", password)
                .build();
        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(formBody)
                .addHeader(USER_AGENT, USER_AGENT_VALUE)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 获取课程表网络连接的方法
     * @param cookie cookie值
     * @param callback 回调方法
     */
    public void getCourseHTML(String cookie, Callback callback){
        Request request = new Request.Builder()
                .url(COURSE_URL)
                .addHeader(USER_AGENT, USER_AGENT_VALUE)
                .addHeader("Cookie", cookie)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
