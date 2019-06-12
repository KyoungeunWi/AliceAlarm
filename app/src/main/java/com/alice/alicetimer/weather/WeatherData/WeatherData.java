package com.alice.alicetimer.weather.WeatherData;

import java.util.List;

public class WeatherData {

    private String cityName ;
    private String weatherDesciption ;
    private String maxTemprature ;
    private String minTemprature ;

    public WeatherData(String cityName, String weatherDesciption, String maxTemprature, String minTemprature) {
        this.cityName = cityName;
        this.weatherDesciption = weatherDesciption;
        this.maxTemprature = maxTemprature;
        this.minTemprature = minTemprature;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getWeatherDesciption() {
        return weatherDesciption;
    }

    public void setWeatherDesciption(String weatherDesciption) {
        this.weatherDesciption = weatherDesciption;
    }

    public String getMaxTemprature() {
        return maxTemprature;
    }

    public void setMaxTemprature(String maxTemprature) {
        this.maxTemprature = maxTemprature;
    }

    public String getMinTemprature() {
        return minTemprature;
    }

    public void setMinTemprature(String minTemprature) {
        this.minTemprature = minTemprature;
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "cityName='" + cityName + '\'' +
                ", weatherDesciption='" + weatherDesciption + '\'' +
                ", maxTemprature='" + maxTemprature + '\'' +
                ", minTemprature='" + minTemprature + '\'' +
                '}';
    }
}
