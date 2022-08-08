package com.monash.paindiary.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
@Entity(tableName = "PAINRECORD")
public class PainRecord {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "user_email")
    @NonNull
    public String userEmail;

    @ColumnInfo(name = "timestamp")
    public long timestamp;

    @ColumnInfo(name = "pain_intensity")
    public int painIntensityLevel;

    @ColumnInfo(name = "pain_area")
    @NonNull
    public String painArea;

    @ColumnInfo(name = "mood")
    @NonNull
    public String mood;

    @ColumnInfo(name = "goal")
    public int goal;

    @ColumnInfo(name = "step_count")
    public int stepCount;

    @ColumnInfo(name = "day_temperature")
    public float temperature;

    @ColumnInfo(name = "day_humidity")
    public float humidity;

    @ColumnInfo(name = "day_pressure")
    public float pressure;

    public PainRecord(@NonNull String userEmail, long timestamp, int painIntensityLevel,
                      @NonNull String painArea, @NonNull String mood, int goal, int stepCount,
                      float temperature, float humidity, float pressure) {
        this.userEmail = userEmail;
        this.timestamp = timestamp;
        this.painIntensityLevel = painIntensityLevel;
        this.painArea = painArea;
        this.mood = mood;
        this.goal = goal;
        this.stepCount = stepCount;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
    }

    public int getUid() {
        return uid;
    }

    @NonNull
    public String getUserEmail() {
        return userEmail;
    }

    public long getDateTime() {
        return timestamp;
    }

    public int getPainIntensityLevel() {
        return painIntensityLevel;
    }

    @NonNull
    public String getPainArea() {
        return painArea;
    }

    @NonNull
    public String getMood() {
        return mood;
    }

    public int getGoal() {
        return goal;
    }

    public int getStepCount() {
        return stepCount;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getPressure() {
        return pressure;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setUserEmail(@NonNull String userEmail) {
        this.userEmail = userEmail;
    }

    public void setDatetime(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public void setPainIntensityLevel(int intensityLevel) {
        this.painIntensityLevel = intensityLevel;
    }

    public void setPainArea(@NonNull String painArea) {
        this.painArea = painArea;
    }

    public void setMood(@NonNull String mood) {
        this.mood = mood;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }
}
