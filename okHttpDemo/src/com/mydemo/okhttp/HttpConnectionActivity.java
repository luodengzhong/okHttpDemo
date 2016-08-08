package com.mydemo.okhttp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Response;

public abstract class HttpConnectionActivity extends Activity {
    OkHttpEngine okHttpInstance;

    protected Handler mHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        okHttpInstance = OkHttpEngine.getInstance();
    }

    public <T> T findView(int id) {
        View view = findViewById(id);
        if (view == null || view.toString().equals("")) {
            return null;
        }
        return (T) view;
    }

    public void getAsynHttp(String url, Map<String, Object> params) {
        okHttpInstance.getAsynHttp(this, url, params);
    }

    public Map<String, Object> getSyncHttp(String url, Map<String, Object> params) {
        try {
            Response response = okHttpInstance.getSyncHttp(this, url, params);
            Map<String, Object> result = new HashMap<>();
            return result;
        } catch (IOException e) {
            return null;
        }
    }

    public void downloadFile(final String url, final String destFileDir) {
        okHttpInstance.downloadFile(this, url, destFileDir);
    }

    public void displayImage(final Handler mHandler, final ImageView view, final String imgUrl, final int errorResId) {
        okHttpInstance.displayImage(mHandler, view, imgUrl, errorResId);
    }

    abstract void httpResponse(String url, Map<String, Object> params, Object result);

    public void completeDownload(String abstractPath) {
    }

    void httpError(String url, Map<String, Object> params, final Exception e) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(HttpConnectionActivity.this, "网络请求失败:" + e.getMessage(), 4000).show();
            }
        });
    }

    void httpError(String url, Map<String, Object> params) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(HttpConnectionActivity.this, "网络请求失败", 300).show();
            }
        });
    }

}
