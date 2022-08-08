package com.monash.paindiary.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.monash.paindiary.activities.AppActivity;
import com.monash.paindiary.databinding.LayoutReminderDialogBinding;

public class ReminderDialogFragment extends DialogFragment {
    private LayoutReminderDialogBinding dialogBinding;
    private int WINDOW_HEIGHT;
    private int WINDOW_WIDTH;

    public interface OnDialogResult {
        void onAlarmTimeSet(int hour, int minute);
    }
    public OnDialogResult onDialogResult;

    @Override
    public void onStart() {
        super.onStart();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WINDOW_HEIGHT = displayMetrics.heightPixels;
        WINDOW_WIDTH = displayMetrics.widthPixels;

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(WINDOW_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.CENTER);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(WINDOW_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.CENTER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dialogBinding = LayoutReminderDialogBinding.inflate(inflater, container, false);
        View view = dialogBinding.getRoot();
        dialogBinding.btnSet.setEnabled(true);
        dialogBinding.btnCancel.setEnabled(true);

        dialogBinding.btnSet.setOnClickListener(v -> {
            dialogBinding.btnSet.setEnabled(false);
            dialogBinding.btnCancel.setEnabled(false);
            onDialogResult.onAlarmTimeSet(dialogBinding.timePicker.getHour(), dialogBinding.timePicker.getMinute());
            getDialog().dismiss();
        });

        dialogBinding.btnCancel.setOnClickListener(v -> {
            dialogBinding.btnSet.setEnabled(false);
            dialogBinding.btnCancel.setEnabled(false);
            getDialog().dismiss();
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dialogBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppActivity)getActivity()).ShowProgress(false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onDialogResult = (OnDialogResult) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e("ReminderDialogFragment", "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}
