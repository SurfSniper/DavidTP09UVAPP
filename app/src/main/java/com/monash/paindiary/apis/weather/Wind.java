package com.monash.paindiary.apis.weather;

import com.google.gson.annotations.SerializedName;

public class Wind {
    @SerializedName("speed")
    public float speed;
    @SerializedName("deg")
    public float deg;

    public float getSpeed() {
        return speed;
    }

    public float getDeg() {
        return deg;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setDeg(float deg) {
        this.deg = deg;
    }
}
