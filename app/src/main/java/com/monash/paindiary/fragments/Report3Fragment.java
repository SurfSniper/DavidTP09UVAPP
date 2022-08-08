package com.monash.paindiary.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.monash.paindiary.R;
import com.monash.paindiary.databinding.FragmentReport3Binding;
import com.monash.paindiary.entity.PainRecord;
import com.monash.paindiary.viewmodel.PainRecordViewModel;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Report3Fragment extends Fragment {
    private FragmentReport3Binding binding;
    private LineChart lineChart;
    private MaterialDatePicker.Builder dateRangeBuilder;
    private Date startDate;
    private Date endDate;
    private Date currentDateTime;
    private String selectedWeatherAtt = "";
    private List<Long> xAxisDateRecords;

    public Report3Fragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReport3Binding.inflate(inflater, container, false);
        View view = binding.getRoot();
        currentDateTime = new Date();
        lineChart = binding.relationalLineChart;
        dateRangeBuilder = MaterialDatePicker.Builder.dateRangePicker();
        lineChart.setDoubleTapToZoomEnabled(false);
        fillSpinner();
        binding.btnShow.setEnabled(true);
        binding.btnClear.setEnabled(true);
        binding.btnSelectDateRange.setEnabled(true);

        binding.btnSelectDateRange.setOnClickListener(this::btnSelectDateRangeOnClicked);
        binding.btnShow.setOnClickListener(this::btnShowOnClicked);
        binding.btnClear.setOnClickListener(this::btnClearOnClicked);
        binding.btnPerformTest.setOnClickListener(this::btnPerformTestOnClicked);
        binding.editTextWeatherAttribute.setOnItemClickListener((parent, view1, position, id) -> {
            if (!selectedWeatherAtt.equals(String.valueOf(binding.editTextWeatherAttribute.getAdapter().getItem(position)))) {
                selectedWeatherAtt = String.valueOf(binding.editTextWeatherAttribute.getAdapter().getItem(position));
                resetChart();
            }
        });

        setUpRelationalLineChart();
        return view;
    }

    private void fillSpinner() {
        ArrayList<String> arrayList = new ArrayList<String>() {{
            add("Temperature");
            add("Humidity");
            add("Pressure");
        }};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_list_item, arrayList);
        binding.editTextWeatherAttribute.setAdapter(arrayAdapter);
        selectedWeatherAtt = "Temperature";
        binding.editTextWeatherAttribute.setText(selectedWeatherAtt, false);
    }

    private void btnPerformTestOnClicked(View view) {
        double[][] data = new double[xAxisDateRecords.size()][];
        for (int i = 0; i < xAxisDateRecords.size(); i++) {
            data[i] = new double[2];
            data[i][0] = lineChart.getLineData().getDataSetByIndex(0).getEntryForIndex(i).getY();
            data[i][1] = lineChart.getLineData().getDataSetByIndex(1).getEntryForIndex(i).getY();
        }
        if (data.length > 2) {
            Pair<Double, Double> pair = getCorrelationValues(data);
            binding.textRValue.setText(String.valueOf(pair.second));
            binding.textPValue.setText(String.valueOf(pair.first));
            binding.layoutTestResult.setVisibility(View.VISIBLE);
        } else
            Toast.makeText(getContext(), "Not enough data to perform test.", Toast.LENGTH_SHORT).show();
    }

    private void btnSelectDateRangeOnClicked(View view) {
        binding.btnShow.setEnabled(false);
        binding.btnClear.setEnabled(false);
        binding.btnSelectDateRange.setEnabled(false);
        binding.layoutSpinnerWeatherAttribute.clearFocus();
        MaterialDatePicker datePicker = dateRangeBuilder
                .setTitleText("Select dates")
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy");
            Pair<Long, Long> pair = (Pair<Long, Long>) selection;
            startDate = new Date(pair.first);
            endDate = new Date(pair.second);
            binding.btnSelectDateRange.setText(String.format("%s - %s", dateFormat.format(startDate), dateFormat.format(endDate)));
        });
        datePicker.show(getActivity().getSupportFragmentManager(), "DATE_RANGE_PICKER");
        resetChart();
        binding.btnShow.setEnabled(true);
        binding.btnClear.setEnabled(true);
        binding.btnSelectDateRange.setEnabled(true);
    }

    private void btnShowOnClicked(View view) {
        binding.btnShow.setEnabled(false);
        binding.btnClear.setEnabled(false);
        binding.btnSelectDateRange.setEnabled(false);
        binding.layoutSpinnerWeatherAttribute.clearFocus();
        binding.btnPerformTest.setEnabled(false);
        binding.layoutTestResult.setVisibility(View.INVISIBLE);
        if (startDate == null || endDate == null) {
            Toast.makeText(getContext(), "Please select date range first.", Toast.LENGTH_SHORT).show();
            binding.btnShow.setEnabled(true);
            binding.btnClear.setEnabled(true);
            binding.btnSelectDateRange.setEnabled(true);
        } else {
            try {
                if (lineChart.getData() != null) {
                    resetChart();
                }
                lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
                    private final SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);

                    @Override
                    public String getFormattedValue(float value) {
//                        if (xAxisValueIndexCounter >= lineChart.getLineData().getDataSetByIndex(0).getEntryCount())
//                            xAxisValueIndexCounter = 0;
//                        return mFormat.format(new Date(xAxisDateRecords.get(xAxisValueIndexCounter++)));
                        return mFormat.format(new Date(currentDateTime.getTime() - (long) value));
                    }
                });
                loadRelationalLineChartData();
                binding.btnPerformTest.setEnabled(true);
            } catch (Exception e) {
                Log.e("EXCEPTION", e.getMessage());
            } finally {
                binding.btnShow.setEnabled(true);
                binding.btnClear.setEnabled(true);
                binding.btnSelectDateRange.setEnabled(true);
            }
        }
    }

    private void resetChart() {
        getActivity().runOnUiThread(() -> {
            binding.btnPerformTest.setEnabled(false);
            binding.layoutTestResult.setVisibility(View.INVISIBLE);
            if (lineChart != null && lineChart.getData() != null) {
                lineChart.getData().clearValues();
                lineChart.getXAxis().setValueFormatter(null);
                lineChart.notifyDataSetChanged();
                lineChart.clear();
                lineChart.invalidate();
            }
        });
    }

    private void btnClearOnClicked(View view) {
        binding.btnShow.setEnabled(false);
        binding.btnClear.setEnabled(false);
        binding.btnSelectDateRange.setEnabled(false);
        binding.layoutSpinnerWeatherAttribute.clearFocus();
        binding.btnPerformTest.setEnabled(false);
        binding.layoutTestResult.setVisibility(View.INVISIBLE);
        resetChart();
        startDate = null;
        endDate = null;
        binding.btnSelectDateRange.setText(R.string.text_select_date_range);
        binding.btnShow.setEnabled(true);
        binding.btnClear.setEnabled(true);
        binding.btnSelectDateRange.setEnabled(true);
    }

    private void setUpRelationalLineChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setNoDataText("Please select Date Range and Weather Attribute then hit SHOW.");
        lineChart.setNoDataTextColor(getResources().getColor(R.color.teal_500, null));

        Legend l = lineChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setDrawGridLines(true);
        xAxis.setAxisMinimum(0f);
        xAxis.setCenterAxisLabels(true);
    }

    private void loadRelationalLineChartData() {
        new Thread(() -> {
            PainRecordViewModel viewModel = new ViewModelProvider(requireActivity()).get(PainRecordViewModel.class);
            viewModel.findRecordsBetweenDate(startDate, endDate).thenApply(painRecords -> {
                ArrayList<Entry> entries_pain_intensities = new ArrayList<>();
                ArrayList<Entry> entries_weather_att = new ArrayList<>();
                xAxisDateRecords = new ArrayList<>();
                painRecords.sort((a, b) -> Float.compare(b.getDateTime(), a.getDateTime()));
                for (PainRecord painRecord : painRecords) {
                    xAxisDateRecords.add(painRecord.getDateTime());
                    entries_pain_intensities.add(
                            new Entry(
                                    currentDateTime.getTime() - painRecord.getDateTime(),
                                    painRecord.getPainIntensityLevel()));
                    entries_weather_att.add(
                            new Entry(
                                    currentDateTime.getTime() - painRecord.getDateTime(),
                                    selectedWeatherAtt.toLowerCase().equals("pressure") ?
                                            painRecord.getPressure() :
                                            selectedWeatherAtt.toLowerCase().equals("humidity") ?
                                                    painRecord.getHumidity() :
                                                    painRecord.getTemperature()));
                }
                getActivity().runOnUiThread(() -> {
                    lineChart.setData(new LineData(
                            getDataSet(
                                    entries_pain_intensities,
                                    YAxis.AxisDependency.LEFT,
                                    "Pain Intensity",
                                    getResources().getColor(R.color.teal_500, null)),
                            getDataSet(
                                    entries_weather_att,
                                    YAxis.AxisDependency.RIGHT,
                                    selectedWeatherAtt,
                                    getResources().getColor(R.color.yellow_theme_dark, null))
                    ));
                    lineChart.invalidate();
                    lineChart.animateX(1000, Easing.EaseOutSine);
                });
                return painRecords;
            });
        }).start();
    }

    @NonNull
    private LineDataSet getDataSet(@NonNull List<Entry> entries, YAxis.AxisDependency axisDependency, String label, int color) {
        entries.sort((a, b) -> Float.compare(a.getX(), b.getX()));
        LineDataSet set = new LineDataSet(entries, label);
        set.setColor(color);
        set.setLineWidth(2.5f);
        set.setCircleColor(color);
        set.setCircleRadius(5f);
        set.setFillColor(color);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(color);

        set.setAxisDependency(axisDependency);
        return set;
    }

    /// First pair is P value and second is correlation value.
    private Pair<Double, Double> getCorrelationValues(double[][] data) {
        // two column array: 1st column=first array, 1st column=second array
        // create a real matrix
        RealMatrix m = MatrixUtils.createRealMatrix(data);
        // measure all correlation test: x-x, x-y, y-x, y-x
        for (int i = 0; i < m.getColumnDimension(); i++)
            for (int j = 0; j < m.getColumnDimension(); j++) {
                PearsonsCorrelation pc = new PearsonsCorrelation();
                double cor = pc.correlation(m.getColumn(i), m.getColumn(j));
//                System.out.println(i + "," + j + "=[" + String.format(".%2f", cor) + "," + "]");
            }
        // correlation test (another method): x-y
        PearsonsCorrelation pc = new PearsonsCorrelation(m);
        RealMatrix corM = pc.getCorrelationMatrix();
        // significant test of the correlation coefficient (p-value)
        RealMatrix pM = pc.getCorrelationPValues();
        return new Pair<>(pM.getEntry(0, 1), corM.getEntry(0, 1));
    }
}
