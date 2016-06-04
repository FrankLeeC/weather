package com.trace.weather.domain;

import android.media.Image;

import java.io.Serializable;

/**
 * Created by dell-pc on 2015/10/29.
 */
public class Weather implements Serializable{
    private String province;
    private String city;
    private String cityCode;
    private String temp0; //当天
    private String temp1;
    private String temp2;
    private String temp3;
    private String temp4;
    private String weather1;
    private String weather2;
    private String weather3;
    private String weather4;
    private String weather5;
    private String windDirection;
    private String windLevel;
    private String day; //当天是周几
    private String tomorrow;
    private String dayAfterTomorrow;
    private String date; //当天是几号
    private String dateTomorrow;
    private String dateAfterTomorrow;
    private int image;
    private int colorFlag;

    public Weather() {
    }

    public Weather(String city, String cityCode, String temp0,String temp1, String temp2, String temp3,
                   String temp4, String weather1, String weather2, String weather3,
                   String weather4,String weather5,String day, String tomorrow, String dayAfterTomorrow,
                   String date, String dateTomorrow, String dateAfterTomorrow,int colorFlag) {
        this.city = city;
        this.cityCode = cityCode;
        this.temp0 = temp0;
        this.temp1 = temp1;
        this.temp2 = temp2;
        this.temp3 = temp3;
        this.temp4 = temp4;
        this.weather1 = weather1;
        this.weather2 = weather2;
        this.weather3 = weather3;
        this.weather4 = weather4;
        this.weather5 = weather5;
        this.day = day;
        this.tomorrow = tomorrow;
        this.dayAfterTomorrow = dayAfterTomorrow;
        this.date = date;
        this.dateTomorrow = dateTomorrow;
        this.dateAfterTomorrow = dateAfterTomorrow;
        this.colorFlag = colorFlag;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getTemp0() {
        return temp0;
    }

    public void setTemp0(String temp0) {
        this.temp0 = temp0;
    }

    public String getTemp1() {
        return temp1;
    }

    public void setTemp1(String temp1) {
        this.temp1 = temp1;
    }

    public String getTemp2() {
        return temp2;
    }

    public void setTemp2(String temp2) {
        this.temp2 = temp2;
    }

    public String getTemp3() {
        return temp3;
    }

    public void setTemp3(String temp3) {
        this.temp3 = temp3;
    }

    public String getTemp4() {
        return temp4;
    }

    public void setTemp4(String temp4) {
        this.temp4 = temp4;
    }

    public String getWeather1() {
        return weather1;
    }

    public void setWeather1(String weather1) {
        this.weather1 = weather1;
    }

    public String getWeather2() {
        return weather2;
    }

    public void setWeather2(String weather2) {
        this.weather2 = weather2;
    }

    public String getWeather3() {
        return weather3;
    }

    public void setWeather3(String weather3) {
        this.weather3 = weather3;
    }

    public String getWeather4() {
        return weather4;
    }

    public void setWeather4(String weather4) {
        this.weather4 = weather4;
    }

    public String getWeather5() {
        return weather5;
    }

    public void setWeather5(String weather5) {
        this.weather5 = weather5;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getWindLevel() {
        return windLevel;
    }

    public void setWindLevel(String windLevel) {
        this.windLevel = windLevel;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTomorrow() {
        return tomorrow;
    }

    public void setTomorrow(String tomorrow) {
        this.tomorrow = tomorrow;
    }

    public String getDayAfterTomorrow() {
        return dayAfterTomorrow;
    }

    public void setDayAfterTomorrow(String dayAfterTomorrow) {
        this.dayAfterTomorrow = dayAfterTomorrow;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateTomorrow() {
        return dateTomorrow;
    }

    public void setDateTomorrow(String dateTomorrow) {
        this.dateTomorrow = dateTomorrow;
    }

    public String getDateAfterTomorrow() {
        return dateAfterTomorrow;
    }

    public void setDateAfterTomorrow(String dateAfterTomorrow) {
        this.dateAfterTomorrow = dateAfterTomorrow;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getColorFlag() {
        return colorFlag;
    }

    public void setColorFlag(int colorFlag) {
        this.colorFlag = colorFlag;
    }
}
