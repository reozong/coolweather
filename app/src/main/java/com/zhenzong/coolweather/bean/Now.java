package com.zhenzong.coolweather.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @Description:
 *
 * @By: zheozong on 2017/2/12 17:59
 * @Email: reozong@gmail.com
 * @Reference:
 */
public class Now {
    /*    "now":{
            "tmp":"37",
            "cond":{
                "txt":"晴"
            }
        }*/
    @SerializedName ("tmp")
    public String temperature;

    @SerializedName ("cond")
    public More more;

    public class More {
        @SerializedName ("txt")
        public String info;
    }
}
