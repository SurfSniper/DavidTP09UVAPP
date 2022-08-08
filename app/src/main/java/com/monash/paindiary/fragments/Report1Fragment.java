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
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.monash.paindiary.R;
import com.monash.paindiary.databinding.FragmentReport1Binding;
import com.monash.paindiary.entity.PainRecord;
import com.monash.paindiary.viewmodel.PainRecordViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Report1Fragment extends Fragment {
    private FragmentReport1Binding binding;
    private PieChart pieChart;

    public Report1Fragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReport1Binding.inflate(inflater, container, false);
        View view = binding.getRoot();
        pieChart = binding.areaPieChart;

        setupPieChart();
        loadPieChartData();

        return view;
    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(14);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setHoleRadius(5);
        pieChart.setDrawSlicesUnderHole(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setNoDataText("No data available to display.");
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
        new Thread(() -> {
            HashMap<String, Integer> painAreaCountMap = new HashMap<>();
            for (PainRecord painRecord : viewModel.getAllPainRecordsSync()) {
                if (painAreaCountMap.containsKey(painRecord.getPainArea()))
                    painAreaCountMap.put(painRecord.getPainArea(), painAreaCountMap.get(painRecord.getPainArea()) + 1);
                else
                    painAreaCountMap.put(painRecord.getPainArea(), 1);
            }
            if (painAreaCountMap.size() > 0) {
                Iterator iterator = painAreaCountMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) iterator.next();
                    entries.add(new PieEntry(entry.getValue(), entry.getKey()));
                }

                ArrayList<Integer> colors = new ArrayList<>();
                for (int color : ColorTemplate.MATERIAL_COLORS) {
                    colors.add(color);
                }

                for (int color : ColorTemplate.VORDIPLOM_COLORS) {
                    colors.add(color);
                }

                PieDataSet dataSet = new PieDataSet(entries, "Pain Locations");
                dataSet.setColors(colors);
                dataSet.setSliceSpace(3f);


                getActivity().runOnUiThread(() -> {
                    PieData data = new PieData(dataSet);
                    data.setDrawValues(true);
                    data.setValueFormatter(new PercentFormatter(pieChart));
                    data.setValueTextSize(15f);
                    data.setValueTextColor(Color.BLACK);
                    data.setHighlightEnabled(true);

                    pieChart.setTransparentCircleRadius(0);
                    pieChart.setData(data);
                    pieChart.invalidate();

                    pieChart.animateY(1400, Easing.EaseInOutQuad);
                });
            }
        }).start();
    }
}
