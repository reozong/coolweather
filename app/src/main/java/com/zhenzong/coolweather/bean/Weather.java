package com.zhenzong.coolweather.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @Description:
 *
 * @By: zheozong on 2017/2/12 18:13
 * @Email: reozong@gmail.com
 * @Reference:
 */
public class Weather {
/*    "HeWeather":[
        {
            "status":"ok",
            "basic":{},
            "aqi":{},
            "now":{},
            "suggestion":{},
            "daily_forecast":[]
        }
    ]*/

    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
