package com.monash.paindiary.helper;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class IntValueFormatter extends ValueFormatter {

    @Override
    public String getFormattedValue(float value) {
        return Converters.formatFloat(value);
    }
}
