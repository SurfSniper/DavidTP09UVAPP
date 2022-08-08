package com.monash.paindiary.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayoutMediator;
import com.monash.paindiary.R;
import com.monash.paindiary.activities.AppActivity;
import com.monash.paindiary.adapter.ReportCollectionPagerAdapter;
import com.monash.paindiary.databinding.FragmentReportViewBinding;
import com.monash.paindiary.enums.NavigationItem;

public class ReportViewFragment extends Fragment {
    private FragmentReportViewBinding binding;

    public ReportViewFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppActivity)getActivity()).ShowProgress(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReportViewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        ((AppActivity) requireActivity()).ManualSelectNavigationItem(NavigationItem.ReportView);

        binding.viewPager2.setAdapter(new ReportCollectionPagerAdapter(this));
        new TabLayoutMediator(binding.tabLayout, binding.viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.tab1);
                    break;
                case 1:
                    tab.setText(R.string.tab2);
                    break;
                case 2:
                    tab.setText(R.string.tab3);
                    break;
            }
        }).attach();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
