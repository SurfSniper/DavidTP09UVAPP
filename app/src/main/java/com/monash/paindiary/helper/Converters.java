package com.monash.paindiary.helper;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(long value) {
        return new Date(value);
    }

    @TypeConverter
    public static long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static float customRoundFloat(float sourceNumber) {
        return Float.parseFloat(String.format("%.2f", sourceNumber));
    }

    @TypeConverter
    public static LocalDate convertToLocalDateViaInstant(@NonNull Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    @NonNull
    @TypeConverter
    public static String formatInteger(int value) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return decimalFormat.format(value);
    }

    @NonNull
    @TypeConverter
    public static String formatFloat(float value) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return decimalFormat.format(value);
    }

    @NonNull
    @TypeConverter
    public static String formatLong(long value) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return decimalFormat.format(value);
    }

    public static String getFontAwesomeWeatherIcon(String openWeatherCode) {
        if (openWeatherCode != null && !openWeatherCode.isEmpty()) {
            switch (openWeatherCode.toLowerCase()) {
                case "clear":
                    return "f00d";
                case "clouds":
                    return "f013";
                case "rain":
                    return "f01a";
                case "thunderstorm":
                    return "f01d";
                case "drizzle":
                    return "f01b";
                case "snow":
                    return "f076";
                case "mist":
                    return "f021";
                default:
                    return "f063";
            }
        }
        return "";
    }
}
