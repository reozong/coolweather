package com.zhenzong.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zhenzong.coolweather.bean.Forecast;
import com.zhenzong.coolweather.bean.Weather;
import com.zhenzong.coolweather.constant.Global;
import com.zhenzong.coolweather.util.HttpUtil;
import com.zhenzong.coolweather.util.JsonUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPic;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        initView();
        String weatherStr = sp.getString("weather", null);
        if (weatherStr != null) {
            //有缓存时直接解析天气数据
            Weather weather = JsonUtil.handleWeatherResponse(weatherStr);
            showWeatherInfo(weather);
        } else {
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            //本地没有数据就请求服务器数据
            requestWeather(weatherId);
        }
    }

    private void initView() {
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.car_wash_text);
        bingPic = (ImageView) findViewById(R.id.bing_pic);

        String bing_pic = sp.getString("bing_pic", null);
        if (bing_pic != null) {
            Glide.with(this).load(Global.BING_PIC).into(bingPic);
        } else {
            loadBingPic();
        }
    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic() {
        HttpUtil.sendHttpRequest(Global.BING_PIC, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String string = response.body().string();
                sp.edit().putString("bing_pic", string).apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.w("Weather", string);
                        Glide.with(WeatherActivity.this).load(string).into(bingPic);
                    }
                });
            }
        });
    }

    /**
     * 向服务器请求天气数据
     *
     * @param weatherId 天气id
     */
    private void requestWeather(String weatherId) {
        String address = Global.WEATHER + "?cityid=" + weatherId + "&key=" + Global.KEY_HEFENG;
        HttpUtil.sendHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String string = response.body().string();
                final Weather weather = JsonUtil.handleWeatherResponse(string);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.w("WEATHER", string);
                        if (weather != null && weather.status.equals("ok")) {
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("weather", string);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //顺便更新一下背景图片
//        loadBingPic();
    }

    /**
     * 显示天气情况
     *
     * @param weather
     */
    private void showWeatherInfo(Weather weather) {
        titleCity.setText(weather.basic.cityName);
        titleUpdateTime.setText(weather.basic.update.updateTime.split(" ")[1]);
        degreeText.setText(weather.now.temperature + "℃");
        weatherInfoText.setText(weather.now.more.info);

        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            ((TextView) view.findViewById(R.id.date_text)).setText(forecast.date);
            ((TextView) view.findViewById(R.id.info_text)).setText(forecast.more.info);
            ((TextView) view.findViewById(R.id.max_text)).setText(forecast.temperature.max);
            ((TextView) view.findViewById(R.id.min_text)).setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }

        if (weather != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }

        comfortText.setText("舒适度：" + weather.suggestion.comfort.info);
        carWashText.setText("洗车指数：" + weather.suggestion.carWash.info);
        sportText.setText("运动建议：" + weather.suggestion.sport.info);
        weatherLayout.setVisibility(View.VISIBLE);
    }

}
