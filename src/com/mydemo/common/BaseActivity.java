package com.mydemo.common;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

public abstract class BaseActivity extends Activity {

    public static final String TAG = BaseActivity.class.getSimpleName();

    protected Handler mHandler = null;

    public BaseUtil baseUtil;

    public HttpUtil httpUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        baseUtil = new BaseUtil(this);
        httpUtil = new HttpUtil(this);
        initHandler();
    }

    protected void initHandler() {
        mHandler = new Handler();
    }

    public <T> T findView(int id) {

        View view = findViewById(id);
        if (view == null || view.toString().equals("")) {
            return null;
        }
        return (T) view;
    }

    /**
     * 通过类名启动Activity
     * 
     * @param pClass
     */
    protected void openActivity(Class<?> pClass) {
        openActivity(pClass, null);
    }

    /**
     * 通过类名启动Activity，并且含有Bundle数据
     * 
     * @param pClass
     * @param pBundle
     */
    protected void openActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(this, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    /**
     * 通过Action启动Activity
     * 
     * @param pAction
     */
    protected void openActivity(String pAction) {
        openActivity(pAction, null);
    }

    /**
     * 通过Action启动Activity，并且含有Bundle数据
     * 
     * @param pAction
     * @param pBundle
     */
    protected void openActivity(String pAction, Bundle pBundle) {
        Intent intent = new Intent(pAction);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    protected void httpResponse(String url, Map<String, Object> params, Object result) {

    }

    protected void completeDownload(String abstractPath) {
    }

    protected void httpError(String url, Map<String, Object> params, final Exception e) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, "网络请求失败:" + e.getMessage(), 4000).show();
            }
        });
    }

    void httpError(String url, Map<String, Object> params) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, "网络请求失败", 300).show();
            }
        });
    }

}
