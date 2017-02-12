package com.zhenzong.coolweather.bean;

/**
 * @Description:
 *
 * @By: zheozong on 2017/2/12 17:57
 * @Email: reozong@gmail.com
 * @Reference:
 */
public class AQI {
    public AQICity city;

    public class AQICity{
        public String aqi;

        public String pm25;
    }
}
