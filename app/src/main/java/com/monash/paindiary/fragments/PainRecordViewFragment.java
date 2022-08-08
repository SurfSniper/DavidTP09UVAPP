package com.monash.paindiary.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.monash.paindiary.activities.AppActivity;
import com.monash.paindiary.adapter.RecyclerViewAdapter;
import com.monash.paindiary.databinding.FragmentPainRecordviewBinding;
import com.monash.paindiary.enums.NavigationItem;
import com.monash.paindiary.viewmodel.PainRecordViewModel;

public class PainRecordViewFragment extends Fragment {
    private FragmentPainRecordviewBinding binding;
    private RecyclerViewAdapter recyclerViewAdapter;

    public PainRecordViewFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppActivity)getActivity()).ShowProgress(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPainRecordviewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        ((AppActivity) requireActivity()).ManualSelectNavigationItem(NavigationItem.RecordView);

        recyclerViewAdapter = new RecyclerViewAdapter();
        binding.recyclerViewPainRecords.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewPainRecords.setAdapter(recyclerViewAdapter);

        PainRecordViewModel viewModel = new ViewModelProvider(requireActivity()).get(PainRecordViewModel.class);
        viewModel.getAllPainRecords().observe(getViewLifecycleOwner(), painRecords -> {
            recyclerViewAdapter.setData(painRecords);
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
