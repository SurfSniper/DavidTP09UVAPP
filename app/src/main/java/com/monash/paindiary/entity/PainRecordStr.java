package com.monash.paindiary.entity;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Date;

@IgnoreExtraProperties
public class PainRecordStr {
    public int uid;
    public String userEmail;
    public String dateTime;
    public int painIntensityLevel;
    public String painArea;
    public String mood;
    public int goal;
    public int stepCount;
    public String temperature;
    public String humidity;
    public String pressure;

    public PainRecordStr(PainRecord painRecord) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        this.uid = painRecord.uid;
        this.userEmail = painRecord.userEmail;
        this.dateTime = dateFormat.format(new Date(painRecord.timestamp));
        this.painIntensityLevel = painRecord.painIntensityLevel;
        this.painArea = painRecord.painArea;
        this.mood = painRecord.mood;
        this.goal = painRecord.goal;
        this.stepCount = painRecord.stepCount;
        this.temperature = String.valueOf(painRecord.temperature);
        this.humidity = String.valueOf(painRecord.humidity);
        this.pressure = String.valueOf(painRecord.pressure);
    }

    public PainRecordStr(int uid, String userEmail, String datetime, int painIntensityLevel,
                         String painArea, String mood, int goal, int stepCount,
                         String temperature, String humidity, String pressure) {
        this.uid = uid;
        this.userEmail = userEmail;
        this.dateTime = datetime;
        this.painIntensityLevel = painIntensityLevel;
        this.painArea = painArea;
        this.mood = mood;
        this.goal = goal;
        this.stepCount = stepCount;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
    }

    @NonNull
    @Override
    public String toString() {
        return "PainRecordStr{" +
                "uid=" + uid +
                ", userEmail='" + userEmail + '\'' +
                ", timestamp='" + dateTime + '\'' +
                ", painIntensityLevel=" + painIntensityLevel +
                ", painArea='" + painArea + '\'' +
                ", mood='" + mood + '\'' +
                ", goal=" + goal +
                ", stepCount=" + stepCount +
                ", temperature='" + temperature + '\'' +
                ", humidity='" + humidity + '\'' +
                ", pressure='" + pressure + '\'' +
                '}';
    }
}
