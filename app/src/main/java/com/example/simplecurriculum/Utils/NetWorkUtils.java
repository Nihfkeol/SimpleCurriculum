package com.example.simplecurriculum.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressWarnings("ConstantConditions")
public class NetWorkUtils {
    OkHttpClient client;
    private final static String LOGIN_URL = "http://210.36.80.160/jsxsd/xk/LoginToXk";
    private final static String COURSE_URL = "http://210.36.80.160/jsxsd/xskb/xskb_list.do";
    private final static String MAIN_URL = "http://210.36.80.160/jsxsd/framework/xsMain.jsp";
    private final static String USER_AGENT = "User-Agent";
    private final static String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36";

    public NetWorkUtils() {
        this.client = new OkHttpClient.Builder().build();
    }

    public NetWorkUtils(CookieJar cookieJar) {
        this.client = new OkHttpClient.Builder().cookieJar(cookieJar).build();
    }

    public String isLogin(String studentId, String password) {
        try {

            FormBody.Builder builder = new FormBody.Builder();
            FormBody formBody = builder.add("USERNAME", studentId)
                    .add("PASSWORD", password)
                    .build();
            Request request = new Request.Builder()
                    .url(LOGIN_URL)
                    .post(formBody)
                    .addHeader(USER_AGENT, USER_AGENT_VALUE)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String isLogin(String cookie) {
        try {
            Request request = new Request.Builder()
                    .url(MAIN_URL)
                    .addHeader("Cookie", cookie)
                    .addHeader(USER_AGENT, USER_AGENT_VALUE)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getCourseHTML(String cookie){
        try {
            Request request = new Request.Builder()
                    .url(COURSE_URL)
                    .addHeader(USER_AGENT, USER_AGENT_VALUE)
                    .addHeader("Cookie", cookie)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
