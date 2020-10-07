package com.example.simplecurriculum;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.simplecurriculum.Utils.CookieUtils;
import com.example.simplecurriculum.Utils.FileUtils;
import com.example.simplecurriculum.Utils.NetWorkUtils;
import com.example.simplecurriculum.Utils.ParseUtils;
import com.example.simplecurriculum.databinding.ActivityShowCurriculumBinding;
import com.example.simplecurriculum.pojo.Course;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ShowCurriculumActivity extends AppCompatActivity {

    private ActivityShowCurriculumBinding binding;
    //RecyclerView中每个item的间隔
    private int MARGIN_LENGTH = 10;
    private MyHandle myHandle;
    //网页
    private String courseHTML;
    //每个TextView的宽度
    private int width;
    //文件路径
    private File filePath;
    private FileUtils fileUtils;
    private SharedPreferences shp;
    //课程表版本
    private int version;
    private String VERSION_KEY;
    //recyclerView中每个item的间距
    CourseAdapter.SpacesItemDecoration decoration;

    private boolean isAuto;
    private String cookie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_curriculum);
        binding.setLifecycleOwner(this);
        init();

        Intent intent = getIntent();
        CookieUtils cookieUtils = new CookieUtils();

        String COOKIE_KEY = getResources().getString(R.string.COOKIE_KEY);

        //是否是自动登录跳转来的
        if (intent.hasExtra(COOKIE_KEY)) {
            isAuto = false;
            cookie = intent.getStringExtra(COOKIE_KEY);
        } else {
            isAuto = true;
            cookie = cookieUtils.getCookieToSHP(getApplicationContext());
        }

        myHandle = new MyHandle();

        if (isAuto) {
            //自动登录，就先获取文件中储存的
            courseHTML = fileUtils.readHtml(filePath);
            if (courseHTML != null) {
                //文件内容不为空，展示数据
                showData();
            } else {
                //文件内容为空联网获取数据
                getDataFromNet(cookie, isAuto);
            }
        } else {
            //不是自动登录，联网获取数据
            getDataFromNet(cookie, isAuto);
        }

    }

    /**
     * 定义数据
     */
    private void init() {
        //获取屏幕宽度，好设置RecyclerView中每个TextView的宽度
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels / 6 - MARGIN_LENGTH;
        //定义文件路径
        String fileName = getResources().getString(R.string.FILE_NAME);
        filePath = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);
        fileUtils = FileUtils.getInstance();
        //获取课程表版本的值
        VERSION_KEY = getResources().getString(R.string.VERSION_KEY);
        shp = getSharedPreferences(getResources().getString(R.string.SHP_NAME), MODE_PRIVATE);
        version = shp.getInt(VERSION_KEY, 1);
        //recyclerView中每个item的间距
        decoration = new CourseAdapter.SpacesItemDecoration(MARGIN_LENGTH);
    }

    /**
     * 联网获取数据
     * @param cookie cookie值
     * @param isAuto 是否是自动登录跳转而来的界面
     */
    private void getDataFromNet(String cookie, boolean isAuto) {
        new Thread(() -> {
            NetWorkUtils netWorkUtils = new NetWorkUtils();
            Message msg = new Message();
            Callback callback = new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    msg.what = -1;
                    myHandle.sendMessage(msg);
                }

                @SuppressWarnings("ConstantConditions")
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    courseHTML = response.body().string();
                    if (isAuto) {
                        //根据cookie登录
                        ParseUtils parseUtils = new ParseUtils(courseHTML);
                        //判断cookie有没有过期or失效
                        boolean isLogin = parseUtils.parseIsLogin();
                        if (isLogin) {
                            msg.what = 1;
                        } else {
                            msg.what = 0;
                        }
                    } else {
                        msg.what = 1;
                    }
                    myHandle.sendMessage(msg);
                }
            };
            netWorkUtils.getCourseHTML(cookie, callback);

        }).start();
    }

    /**
     * 展示数据
     */
    private void showData() {
        ParseUtils parseUtils = new ParseUtils(courseHTML);
        String versionStr = parseUtils.parseVersion(version);
        List<Course> courseList = parseUtils.parseCourse(versionStr);
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
        binding.RecyclerView.removeItemDecoration(decoration);
        CourseAdapter adapter = new CourseAdapter(ShowCurriculumActivity.this, courseList, width);
        binding.RecyclerView.setLayoutManager(manager);
        binding.RecyclerView.addItemDecoration(decoration);
        binding.RecyclerView.setAdapter(adapter);
    }

    /**
     * 创建菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 菜单选择器
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.upDataMenu:
                getDataFromNet(cookie,isAuto);
                break;
            case R.id.switchVersionMenu:
                if (version == 1) {
                    version = 0;
                } else {
                    version = 1;
                }
                SharedPreferences.Editor editor = shp.edit();
                editor.putInt(VERSION_KEY, version);

                editor.apply();
                showData();
                break;
            case R.id.aboutMenu:
                Toast.makeText(getApplicationContext(), "暂时没有做这页面~！", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 显示菜单图标
     * @param featureId
     * @param menu
     * @return
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @SuppressLint("HandlerLeak")
    private class MyHandle extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                fileUtils.writeHtml(courseHTML, filePath);
                showData();
            } else if (msg.what == 0) {
                Toast.makeText(getApplicationContext(), "cookie失效请重新登录", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(ShowCurriculumActivity.this, MainActivity.class);
                intent.putExtra("from", true);
                ShowCurriculumActivity.this.startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(ShowCurriculumActivity.this, MainActivity.class);
                intent.putExtra("from", true);
                ShowCurriculumActivity.this.startActivity(intent);
                finish();
            }
        }
    }


}