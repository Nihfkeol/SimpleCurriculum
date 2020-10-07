package com.example.simplecurriculum.Utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileUtils {

    private static FileUtils fileUtils;

    private FileUtils() {
    }

    public static FileUtils getInstance(){
        if (fileUtils == null){
            synchronized (FileUtils.class){
                if (fileUtils == null){
                    fileUtils = new FileUtils();
                }
            }
        }
        return fileUtils;
    }

    /**
     * 写入课程表数据
     * @param html 课程表内容
     * @param file 文件路径
     */
    public void writeHtml(String html, File file){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            try {
                FileOutputStream fos = new FileOutputStream(file.toString());
                byte[] bytes = html.getBytes();
                fos.write(bytes);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 读取本地储存的课程表数据
     * @param file 文件路径
     * @return
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public String readHtml(File file){
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            fis.close();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
