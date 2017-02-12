package com.zhenzong.coolweather.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @Description:
 *
 * @By: zheozong on 2017/2/12 18:08
 * @Email: reozong@gmail.com
 * @Reference:
 */
public class Forecast {
    public String date;

    @SerializedName ("tmp")
    public Temperature temperature;

    @SerializedName ("cond")
    public More more;

    public class Temperature {
        public String max;
        public String min;
    }

    public class More {
        @SerializedName ("txt_d")
        public String info;
    }
}
