package com.mydemo.common;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.os.Handler;
import android.widget.ImageView;

import com.squareup.okhttp.Response;

public class HttpUtil {

    private BaseActivity activity;

    public HttpUtil(BaseActivity activity) {
        this.activity = activity;
    }

    public void getAsynHttp(String url, Map<String, Object> params) {
        OkHttpEngine.getInstance().getAsynHttp(activity, url);
    }

    public Map<String, Object> getSyncHttp(String url) {
        try {
            Response response = OkHttpEngine.getInstance().getSyncHttp(activity, url);
            Map<String, Object> result = new HashMap<>();
            return result;
        } catch (IOException e) {
            return null;
        }
    }

    public void post(String url, Map<String, Object> params) {
        OkHttpEngine.getInstance().postAsynHttp(activity, url, params);
    }

    public void downloadFile(final String url, final String destFileDir) {
        OkHttpEngine.getInstance().downloadFile(activity, url, destFileDir);
    }

    public void displayImage(final Handler mHandler, final ImageView view, final String imgUrl, final int errorResId) {
        OkHttpEngine.getInstance().displayImage(mHandler, view, imgUrl, errorResId);
    }

    public void fileUpload(final String url, final Map<String, Object> params, File[] files, String[] fileKeys) {
        OkHttpEngine.getInstance().fileUpload(activity, url, params, files, fileKeys);
    }

}
