package com.mydemo.okhttp;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends HttpConnectionActivity {

    private final String BAIDU_INDEX1 = "http://www.baidu.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler(getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                case 1:
                    break;
                }
            }
        };
        Map<String, Object> p1 = new HashMap<>();
        this.getAsynHttp(BAIDU_INDEX1, p1);

        // this.getAsynHttp(BAIDU_INDEX2, p1);
        //
        // this.getAsynHttp(BAIDU_INDEX3, p1);
        // 要调用同步GET方法，但需要另起一个子线程发起请求，而不能直接在主线程发起请求。
        /*
         * Thread t = new Thread(new Runnable() {
         * @Override
         * public void run() {
         * Map<String, Object> p1 = new HashMap<>();
         * p1.put("s", System.currentTimeMillis());
         * MainActivity.this.getSyncHttp(BAIDU_INDEX4, p1);
         * }
         * });
         * t.start();
         */
        ImageView view = findView(R.id.imageview);
        // 加载图片
        this.displayImage(mHandler, view, "https://www.baidu.com/img/bd_logo1.png", R.drawable.no_image);
        String p = MyApplication.mkdirs("/net_image/a/b");
        this.downloadFile("http://a4.att.hudong.com/23/09/01300000165488122547095974400.jpg", p);
    }

    public void httpResponse(String url, Map<String, Object> params, Object result) {
        Message msg = new Message();
        switch (url) {
        case BAIDU_INDEX1:
            msg.what = 1;
            msg.obj = result;
            mHandler.sendMessage(msg);
            break;
        }

    }

    @Override
    public void completeDownload(final String abstractPath) {
        super.completeDownload(abstractPath);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ImageView view5 = findView(R.id.imageview2);
                view5.setImageBitmap(ImageUtils.getLoacalBitmap(abstractPath));
                TextView view1 = findView(R.id.result5);
                view1.setText("存储位置:"+abstractPath);
            }
        });
    }

    @Override
    void httpError(String url, Map<String, Object> params, final Exception e) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                TextView view4 = findView(R.id.result5);
                view4.setText(e.getMessage());
            }
        });
    }

}
