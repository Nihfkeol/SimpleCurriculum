package com.example.simplecurriculum;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.simplecurriculum.R;

public class AccountViewModel extends AndroidViewModel {

    private SavedStateHandle handle;
    //储存在SharedPreferences文件中获取学号的key
    private final String STUDENT_ID_KEY = getApplication().getResources().getString(R.string.STUDENT_ID);
    //储存在SharedPreferences文件中获取密码的key
    private final String PASSWORD_KEY = getApplication().getResources().getString(R.string.PASSWORD);
    private final String ISAUTO_KEY = getApplication().getString(R.string.IS_AUTO);
    private final String ISSAVE_KEY = getApplication().getString(R.string.IS_SAVE);

    private SharedPreferences shp;

    public AccountViewModel(@NonNull Application application, SavedStateHandle handle) {
        super(application);
        this.handle = handle;
        //从string资源中获取的SharedPreferences的文件名
        String SHP_NAME = getApplication().getResources().getString(R.string.SHP_NAME);
        shp = getApplication().getSharedPreferences(SHP_NAME, Context.MODE_PRIVATE);
        //从string资源中获取的默认String的值的文件名
        String defValue = getApplication().getResources().getString(R.string.defStringValue);
        //判断是否有学号的key,没有就加载
        if (!handle.contains(STUDENT_ID_KEY)) {
            this.handle.set(STUDENT_ID_KEY, shp.getString(STUDENT_ID_KEY, defValue));
        }
        if (!handle.contains(PASSWORD_KEY)) {
            this.handle.set(PASSWORD_KEY, shp.getString(PASSWORD_KEY, defValue));
        }
        //从string资源中获取的默认Boolean的值的文件名
        boolean defBool = getApplication().getResources().getBoolean(R.bool.defBool);
        if (!handle.contains(ISAUTO_KEY)) {
            this.handle.set(ISAUTO_KEY, shp.getBoolean(ISAUTO_KEY, defBool));
        }
        if (!handle.contains(ISSAVE_KEY)) {
            this.handle.set(ISSAVE_KEY, shp.getBoolean(ISSAVE_KEY, defBool));
        }
    }

    public void saveCheck() {
        SharedPreferences.Editor editor = shp.edit();
        editor.putBoolean(ISAUTO_KEY, getIsAuto().getValue());
        editor.putBoolean(ISSAVE_KEY, getIsSave().getValue());
        editor.apply();
    }

    public void saveAccount() {
        SharedPreferences.Editor editor = shp.edit();
        editor.putString(STUDENT_ID_KEY, getStudentId().getValue());
        editor.putString(PASSWORD_KEY, getPassword().getValue());
        editor.apply();
    }

    public MutableLiveData<String> getStudentId() {
        return handle.getLiveData(STUDENT_ID_KEY);
    }

    public MutableLiveData<String> getPassword() {
        return handle.getLiveData(PASSWORD_KEY);
    }

    public void setStudentId(String studentId) {
        handle.set(STUDENT_ID_KEY, studentId);
    }

    public void setPassword(String password) {
        handle.set(PASSWORD_KEY, password);
    }

    public MutableLiveData<Boolean> getIsAuto() {
        return handle.getLiveData(ISAUTO_KEY);
    }

    public MutableLiveData<Boolean> getIsSave() {
        return handle.getLiveData(ISSAVE_KEY);
    }

    public void setIsAuto(boolean isAuto) {
        handle.set(ISAUTO_KEY, isAuto);
    }

    public void setIsSave(boolean isSave) {
        handle.set(ISSAVE_KEY, isSave);
    }

    public void clearAccount() {
        SharedPreferences.Editor edit = shp.edit();
        edit.clear();
        edit.apply();
    }

}
