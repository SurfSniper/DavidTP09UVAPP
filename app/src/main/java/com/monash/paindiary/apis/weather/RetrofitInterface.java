package com.monash.paindiary.apis.weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitInterface {

    @GET("data/3.0/weather?")
    Call<WeatherResponse> weatherCall(@Query("lat") String lat, @Query("lon") String lon, @Query("units") String units, @Query("appid") String appId);
}
