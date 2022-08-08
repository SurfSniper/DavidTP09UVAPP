package com.monash.paindiary.apis.weather;

import com.google.gson.annotations.SerializedName;

public class uv {
    @SerializedName("UV")
    public float Uv;


    public float getUv() {
        return Uv;
    }

    public void setUv(float Uv) {
        this.Uv = Uv;
    }

}
