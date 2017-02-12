package com.zhenzong.coolweather.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @Description:
 *
 * @By: zheozong on 2017/2/12 16:23
 * @Email: reozong@gmail.com
 * @Reference:
 */
public class Basic {

//    {
//        "city":"深圳",
//        "id":"CN34343434",
//        "update":{
//            "loc":"2017-01-12 21:00"
//        }
//    }

    @SerializedName ("city")
    public String cityName;

    @SerializedName ("id")
    public String weatherId;

    public Update update;

    public class Update {
        @SerializedName ("loc")
        public String updateTime;
    }
}
