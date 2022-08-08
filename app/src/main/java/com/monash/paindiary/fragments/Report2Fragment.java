package com.monash.paindiary.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.monash.paindiary.R;
import com.monash.paindiary.databinding.FragmentReport2Binding;
import com.monash.paindiary.helper.Converters;
import com.monash.paindiary.helper.IntValueFormatter;
import com.monash.paindiary.viewmodel.PainRecordViewModel;

import java.util.ArrayList;
import java.util.Date;

public class Report2Fragment extends Fragment {
    private FragmentReport2Binding binding;
    private PieChart pieChart;

    public Report2Fragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReport2Binding.inflate(inflater, container, false);
        View view = binding.getRoot();
        pieChart = binding.goalPieChart;

        setupPieChart();
        loadPieChartData();

        return view;
    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(false);
        pieChart.setClickable(false);
        pieChart.setRotationEnabled(false);
        pieChart.setEntryLabelTextSize(14);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setHoleRadius(60);
        pieChart.setDrawSlicesUnderHole(false);
        pieChart.setCenterText("Step Count");
        pieChart.setCenterTextSize(24);
        pieChart.getDescription().setEnabled(false);
        pieChart.setNoDataText("No step data available for today.");
        pieChart.setNoDataTextColor(getResources().getColor(R.color.teal_500, null));

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setTextSize(12);
        l.setDrawInside(false);
        l.setEnabled(false);
    }

    private void loadPieChartData() {
        PainRecordViewModel viewModel = new ViewModelProvider(requireActivity()).get(PainRecordViewModel.class);
        ArrayList<PieEntry> entries = new ArrayList<>();
        new Thread(() -> viewModel.findRecordByDate(new Date()).thenApply(painRecord -> {
            entries.add(new PieEntry(painRecord.getStepCount(), "Completed", 0));
            entries.add(new PieEntry(painRecord.getGoal() - painRecord.getStepCount(), "Remaining"));

            ArrayList<Integer> colors = new ArrayList<>();
            colors.add(getResources().getColor(R.color.yellow_500, null));
            colors.add(getResources().getColor(R.color.grey_200, null));

            PieDataSet dataSet = new PieDataSet(entries, "Pain Locations");
            dataSet.setColors(colors);
            dataSet.setSliceSpace(3f);

            getActivity().runOnUiThread(() -> {
                PieData data = new PieData(dataSet);
                data.setDrawValues(true);
                data.setValueFormatter(new IntValueFormatter());
                data.setValueTextSize(15f);
                data.setValueTextColor(Color.BLACK);
                data.setHighlightEnabled(true);

                pieChart.setCenterText(String.format("Steps\n%s / %s", Converters.formatLong(painRecord.getStepCount()), Converters.formatLong(painRecord.getGoal())));

                pieChart.setNoDataText("No data available.");
                pieChart.setTransparentCircleRadius(0);
                pieChart.setData(data);
                pieChart.invalidate();

//                    pieChart.animateY(1400, Easing.EaseInOutQuad);
                pieChart.animateY(1400, Easing.EaseInQuad);
            });

            return painRecord;
        })).start();
    }
}
