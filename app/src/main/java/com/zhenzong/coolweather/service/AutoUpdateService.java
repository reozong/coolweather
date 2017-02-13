package com.zhenzong.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.zhenzong.coolweather.bean.Weather;
import com.zhenzong.coolweather.constant.Global;
import com.zhenzong.coolweather.util.HttpUtil;
import com.zhenzong.coolweather.util.JsonUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 后台自动更新天气，首页图片的服务
 */
public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        setAlarm();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 创建定时任务
     */
    private void setAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int interval = 6 * 60 * 60 * 1000; // 6小时
        // 触发时间
        long triggerAtTime = SystemClock.elapsedRealtime() + interval; // 服务启动后每6小时执行一次服务
        Intent intent = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 10, intent, 0);
        alarmManager.cancel(pi);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
    }

    /**
     * 获取天气数据，如果没有缓存，说明是第一次启动，直接交由Activity
     */
    private void updateWeather() {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherStr = sp.getString("weather", null);
        if (weatherStr != null) {
            //有缓存时直接解析天气数据
            Weather weather = JsonUtil.handleWeatherResponse(weatherStr);
            String weatherId = weather.basic.weatherId;
            String weatherUrl = Global.WEATHER + "?cityid=" + weatherId + "&key=" + Global.KEY_HEFENG;
            HttpUtil.sendHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String weatherJson = response.body().string();
                    Weather weather = JsonUtil.handleWeatherResponse(weatherJson);
                    if (weather != null && weather.status.equals("ok")) {
                        sp.edit().putString("weather", weatherJson).apply();
                    }
                }
            });
        }
    }

    /**
     * 更新背景图
     */
    private void updateBingPic() {
        HttpUtil.sendHttpRequest(Global.BING_PIC, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit().putString("bingPic",
                        bingPic).apply();
            }
        });
    }
}
