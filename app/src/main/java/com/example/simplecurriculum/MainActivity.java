package com.example.simplecurriculum;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProvider;

import com.example.simplecurriculum.Utils.CookieUtils;
import com.example.simplecurriculum.Utils.NetWorkUtils;
import com.example.simplecurriculum.Utils.ParseUtils;
import com.example.simplecurriculum.databinding.ActivityMainBinding;

import androidx.databinding.DataBindingUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {

    //视图绑定
    private ActivityMainBinding binding;
    //cookie列标
    private List<Cookie> cookieStore;
    private MyHandle myHandle;
    private String TAG = "MainActivity：";
    private AccountViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModelProvider(this, new SavedStateViewModelFactory(getApplication(), this)).get(AccountViewModel.class);
        binding.setData(viewModel);
        binding.setLifecycleOwner(this);

        boolean isCheckeAuto = viewModel.getIsAuto().getValue();
        if (isCheckeAuto){
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, ShowCurriculumActivity.class);
            startActivity(intent);
            finish();
        }

        boolean isCheckSave = viewModel.getIsSave().getValue();
        if (isCheckSave) {
            //如果保存了，传值到输入框
            binding.editTextStudentId.setText(String.valueOf(viewModel.getStudentId()));
            binding.editTextPassword.setText(String.valueOf(viewModel.getPassword()));
        }


        binding.checkBoxAuto.setOnClickListener(v -> {
            boolean isCheck = binding.checkBoxAuto.isChecked();
            viewModel.setIsAuto(isCheck);
            //当点击自动登录的时候，保存密码也被打勾
            if (isCheck) {
                viewModel.setIsSave(isCheck);
            }
        });
        binding.checkBoxSave.setOnClickListener(v -> {
            boolean isCheck = binding.checkBoxSave.isChecked();
             viewModel.setIsSave(isCheck);
             if (!isCheck){
                 viewModel.setIsAuto(isCheck);
             }
        });

        //监听输入框输入状态存入数据
        binding.editTextStudentId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                viewModel.setStudentId(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                viewModel.setPassword(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //cookie
        CookieJar cookieJar = new CookieJar() {
            @Override
            public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
                cookieStore = list;
            }

            @NotNull
            @Override
            public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                return cookieStore != null ? cookieStore : new ArrayList<>();
            }
        };
        myHandle = new MyHandle();

        binding.buttonLogin.setOnClickListener(v -> {
            //保存勾选状态
            viewModel.saveCheck();
            new Thread(() -> {
                NetWorkUtils netWorkUtils = new NetWorkUtils(cookieJar);
                //返回结果是否登录成功
                String loginHtml = netWorkUtils.isLogin(viewModel.getStudentId().getValue(), viewModel.getPassword().getValue());
                ParseUtils parseUtils = new ParseUtils(loginHtml);
                Boolean isLogin = parseUtils.parseIsLogin();
                Message message = new Message();
                message.obj = isLogin;
                myHandle.sendMessage(message);
            }).start();


        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!viewModel.getIsSave().getValue()){
            viewModel.clearAccount();
        }
    }

    @SuppressLint("HandlerLeak")
    private class MyHandle extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            boolean isLogin = (boolean) msg.obj;

            if (isLogin) {
                Cookie cookie = cookieStore.get(0);
                Intent intent = new Intent();
                if (viewModel.getIsSave().getValue()) {
                    //如果勾选了保存，那么就保存账户
                    viewModel.saveAccount();
                    //获取是否自动登录
                    boolean isAuto = viewModel.getIsAuto().getValue();
                    intent.putExtra(getResources().getString(R.string.IS_AUTO), isAuto);
                    //勾选了自动登录，保存cookie
                    if (isAuto){
                        CookieUtils cookieUtils = new CookieUtils();
                        cookieUtils.saveCookieToSHP(getApplicationContext(),cookie);
                    }
                }else {
                    //没有勾选保存，则把储存文件中的所有数据清除
                    viewModel.clearAccount();
                }
                intent.setClass(MainActivity.this, ShowCurriculumActivity.class);
                intent.putExtra(getResources().getString(R.string.COOKIE_KEY), String.valueOf(cookie));
                startActivity(intent);
                finish();
            } else {
                //登录失败，清除保存的帐号密码
                viewModel.clearAccount();
                Toast.makeText(getApplicationContext(), "帐号或用户名错误 ", Toast.LENGTH_SHORT).show();
            }
        }

    }

}