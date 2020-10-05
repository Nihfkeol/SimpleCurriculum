package com.example.simplecurriculum;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.simplecurriculum.Utils.CookieUtils;
import com.example.simplecurriculum.Utils.NetWorkUtils;
import com.example.simplecurriculum.Utils.ParseUtils;
import com.example.simplecurriculum.databinding.ActivityShowCurriculumBinding;
import com.example.simplecurriculum.pojo.Course;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;


public class ShowCurriculumActivity extends AppCompatActivity {

    private ActivityShowCurriculumBinding binding;
    //RecyclerView中每个item的间隔
    private int MARGIN_LENGTH = 10;
    private MyHandle myHandle;
    private String courseHTML;
    //每个TextView的宽度
    private int width;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_curriculum);
        binding.setLifecycleOwner(this);
        //获取屏幕宽度，好设置RecyclerView中每个TextView的宽度
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels / 6 - MARGIN_LENGTH;

        Intent intent = getIntent();
        CookieUtils cookieUtils = new CookieUtils();
        String cookie;
        String COOKIE_KEY = getResources().getString(R.string.COOKIE_KEY);
        boolean isAuto;
        //是否是自动登录跳转来的
        if (intent.hasExtra(COOKIE_KEY)) {
            isAuto = false;
            cookie = intent.getStringExtra(COOKIE_KEY);
        } else {
            isAuto = true;
            cookie = cookieUtils.getCookieToSHP(getApplicationContext());
        }

        myHandle = new MyHandle();

        new Thread(() -> {
            NetWorkUtils netWorkUtils = new NetWorkUtils();
            Message msg = new Message();
            if (isAuto){
                //根据cookie登录
                String loginHTML = netWorkUtils.isLogin(cookie);
                ParseUtils parseUtils = new ParseUtils(loginHTML);
                //判断cookie有没有过期or失效
                boolean isLogin = parseUtils.parseIsLogin();
                if (isLogin) {
                    courseHTML = netWorkUtils.getCourseHTML(cookie);
                    msg.what = 1;
                }else {
                    msg.what = 0;
                }
            }else {
                courseHTML = netWorkUtils.getCourseHTML(cookie);
                msg.what = 1;
            }
            myHandle.sendMessage(msg);
        }).start();

    }

    @SuppressLint("HandlerLeak")
    private class MyHandle extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                ParseUtils parseUtils = new ParseUtils(courseHTML);
                String version = parseUtils.parseVersion();
                List<Course> courseList = parseUtils.parseCourse(version);
                //recyclerView的布局样式
                GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 49, GridLayoutManager.HORIZONTAL, false) {
                    @Override
                    public boolean canScrollHorizontally() {
                        return false;
                    }
                };
                manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (position % 7 == 0)
                            return 1;
                        return 8;
                    }
                });
                CourseAdapter.SpacesItemDecoration decoration = new CourseAdapter.SpacesItemDecoration(MARGIN_LENGTH);
                binding.RecyclerView.setLayoutManager(manager);
                binding.RecyclerView.addItemDecoration(decoration);
                binding.RecyclerView.setAdapter(new CourseAdapter(ShowCurriculumActivity.this, courseList, width));

            } else {
                Toast.makeText(getApplicationContext(), "cookie失效请重新登录", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(ShowCurriculumActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}