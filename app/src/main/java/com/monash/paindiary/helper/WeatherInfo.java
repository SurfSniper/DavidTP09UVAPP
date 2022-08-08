package com.monash.paindiary.helper;

import com.monash.paindiary.apis.weather.WeatherResponse;

import java.util.Date;

public class WeatherInfo {
    private static WeatherInfo INSTANCE;
    private WeatherResponse weatherResponse;

    public static final String API_KEY = "947c6e809cdbe01fcf3f6d54451666b3";
    public static final String latitude = "-37.814";
    public static final String longitude = "144.9633";
    public static final String units = "metric";
    public static Date lastFetched;

    public static void setInstance(WeatherResponse weatherResponse, boolean forceOverride) {
        if (INSTANCE == null || forceOverride)
            INSTANCE = new WeatherInfo(weatherResponse);
    }

    public static void clearInstance() {
        INSTANCE = null;
    }

    public static WeatherInfo getInstance() {
        return INSTANCE;
    }

    private WeatherInfo(WeatherResponse weatherResponse) {
        this.weatherResponse = weatherResponse;
    }

    public static WeatherResponse getWeatherResponse() {
        if (INSTANCE == null)
            return null;
        else
            return INSTANCE.weatherResponse;
    }
}
