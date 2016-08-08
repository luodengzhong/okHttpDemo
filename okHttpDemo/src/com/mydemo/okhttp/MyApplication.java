package com.mydemo.okhttp;

import java.io.File;

import android.app.Application;
import android.os.Environment;

public class MyApplication extends Application {

    static MyApplication application = new MyApplication();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private MyApplication() {

    }

    public static MyApplication getApplication() {
        return application;
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);// 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
        }
        return sdDir.toString();
    }

    public static String mkdirs(String path) {
        String sdcard = getSDPath();
        if (path.indexOf(getSDPath()) == -1) {
            path = sdcard + (path.indexOf("/") == 0 ? "" : "/") + path;
        }
        File destDir = new File(path);
        if (!destDir.exists()) {
            path = createDir(path);
            if (path == null) {
                return null;
            }
        }
        return path;
    }

    private static String createDir(String path) {
        String sdPath = getSDPath();
        String[] dirs = path.replace(sdPath, "").split("/");
        StringBuffer filePath = new StringBuffer(sdPath);
        for (String dir : dirs) {
            if (!"".equals(dir) && !dir.equals(sdPath)) {
                filePath.append("/").append(dir);
                File destDir = new File(filePath.toString());
                if (!destDir.exists()) {
                    boolean b = destDir.mkdirs();
                    if (!b) {
                        return null;
                    }
                }
            }
        }
        return filePath.toString();
    }
}