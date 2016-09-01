package com.mydemo.common;

import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class BaseUtil {
    private Context context;

    protected InputMethodManager imm;

    private TelephonyManager tManager;

    public BaseUtil(Context context) {
        this.context = context;
        tManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
    }

    public void DisPlay(String content) {
        Toast.makeText(context, content, 1).show();
    }

    public void DisplayToast(String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public void hideOrShowSoftInput(boolean isShowSoft, EditText editText) {
        if (isShowSoft) {
            imm.showSoftInput(editText, 0);
        } else {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    // 获得当前程序版本信息
    public String getVersionName() throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        return packInfo.versionName;
    }

    // 獲得設備信息
    public String getDeviceId() throws Exception {
        String deviceId = tManager.getDeviceId();

        return deviceId;
    }

    /**
     * 获取SIM卡序列号
     * 
     * @return
     */
    public String getToken() {
        return tManager.getSimSerialNumber();
    }

    /* 獲得系統版本 */

    public String getClientOs() {
        return android.os.Build.ID;
    }

    /* 獲得系統版本號 */
    public String getClientOsVer() {
        return android.os.Build.VERSION.RELEASE;
    }

    // 獲得系統語言包
    public String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public String getCountry() {

        return Locale.getDefault().getCountry();
    }

}
