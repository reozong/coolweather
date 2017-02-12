package com.zhenzong.coolweather.bean;

import org.litepal.crud.DataSupport;

/**
 * @Description:
 *
 * @By: zheozong on 2017/2/12 10:21
 * @Email: reozong@gmail.com
 * @Reference:
 */
public class County extends DataSupport{
    private int id;
    private String countyName;
    private String weatherId;
    /*县所属的市*/
    private int cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
