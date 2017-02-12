package com.zhenzong.coolweather.util;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @Description:
 *
 * @By: zheozong on 2017/2/12 10:41
 * @Email: reozong@gmail.com
 * @Reference:
 */
public class HttpUtil {
    /**
     * 发送http请求
     * @param address 请求地址
     * @param callback 回调
     */
    public static void sendHttpRequest(String address, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        okHttpClient.newCall(request).enqueue(callback);
    }
}
