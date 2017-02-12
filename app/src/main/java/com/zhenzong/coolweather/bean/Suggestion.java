package com.zhenzong.coolweather.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @Description:
 *
 * @By: zheozong on 2017/2/12 18:04
 * @Email: reozong@gmail.com
 * @Reference:
 */
public class Suggestion {
    @SerializedName ("comf")
    public Comfort comfort;

    @SerializedName ("cw")
    public CarWash carWash;

    public Sport sport;


    public class Sport {
        @SerializedName ("txt")
        public String info;
    }

    public class CarWash {
        @SerializedName ("txt")
        public String info;
    }

    public class Comfort {
        @SerializedName ("txt")
        public String info;
    }
}
