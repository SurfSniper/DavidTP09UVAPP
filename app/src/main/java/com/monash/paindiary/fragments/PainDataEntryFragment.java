package com.monash.paindiary.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.slider.Slider;
import com.monash.paindiary.R;
import com.monash.paindiary.activities.AppActivity;
import com.monash.paindiary.apis.weather.RetrofitClient;
import com.monash.paindiary.apis.weather.WeatherResponse;
import com.monash.paindiary.databinding.FragmentPainDataEntryBinding;
import com.monash.paindiary.entity.PainRecord;
import com.monash.paindiary.enums.NavigationItem;
import com.monash.paindiary.helper.Converters;
import com.monash.paindiary.helper.DataUploadWorker;
import com.monash.paindiary.helper.ReminderBroadcast;
import com.monash.paindiary.helper.UserInfo;
import com.monash.paindiary.helper.WeatherInfo;
import com.monash.paindiary.viewmodel.PainRecordViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PainDataEntryFragment extends Fragment implements ReminderDialogFragment.OnDialogResult {
    private final static String THIS_CLASS = "PainDataEntryFragment";
    private FragmentPainDataEntryBinding binding;
    private PainRecordViewModel viewModel;
    private int uid = -1;
    private long last_inserted_timestamp;

    public PainDataEntryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Initialize vars
        binding = FragmentPainDataEntryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(getActivity()).get(PainRecordViewModel.class);
        binding.btnEdit.setEnabled(false);
        binding.textSliderIntensityValue.setText(String.valueOf((int) binding.sliderIntensityLevel.getValue()));
        binding.sliderIntensityLevel.requestFocus();
        binding.btnSave.setEnabled(true);
        binding.btnEdit.setEnabled(true);
        hideKeyboard();
        binding.editStepCount.clearFocus();
        binding.editGoal.clearFocus();
        binding.sliderIntensityLevel.requestFocus();

        ((AppActivity) requireActivity()).ManualSelectNavigationItem(NavigationItem.DataEntry);

        binding.btnSave.setOnClickListener(this::btnSaveOnClick);
        binding.btnEdit.setOnClickListener(this::btnEditOnClicked);
        binding.painAreaChipGroup.setOnCheckedChangeListener(this::painAreaChipGroupOnCheckedChange);
        binding.sliderIntensityLevel.addOnChangeListener(this::sliderIntensityLevelOnChanged);
        binding.editStepCount.addTextChangedListener(editStepCountTextWatcher());
        binding.editGoal.addTextChangedListener(editGoalTextWatcher());
        binding.btnMoodVeryLow.setOnClickListener(this::btnMoodOnClicked);
        binding.btnMoodLow.setOnClickListener(this::btnMoodOnClicked);
        binding.btnMoodAverage.setOnClickListener(this::btnMoodOnClicked);
        binding.btnMoodGood.setOnClickListener(this::btnMoodOnClicked);
        binding.btnMoodVeryGood.setOnClickListener(this::btnMoodOnClicked);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setControls();
        ((AppActivity)getActivity()).ShowProgress(false);
        boolean isFromNotification = false;
        if (getArguments() != null && !getArguments().isEmpty() && getArguments().containsKey("isFromNotification")) {
            isFromNotification = getArguments().getBoolean("isFromNotification");
        }
        if (!((AppActivity) getActivity()).isAlarmSet && !isFromNotification) {
            ReminderDialogFragment reminderDialogFragment = new ReminderDialogFragment();
            reminderDialogFragment.setTargetFragment(PainDataEntryFragment.this, 0);
            reminderDialogFragment.show(getFragmentManager(), "ReminderDialog");
        }
    }

    private void setControls() {
        PainRecordViewModel viewModel = new ViewModelProvider(getActivity()).get(PainRecordViewModel.class);
        new Thread(() -> viewModel.findRecordByDate(new Date()).thenApply(painRecord -> {
            getActivity().runOnUiThread(() -> {
                if (painRecord != null) {
                    uid = painRecord.getUid();
                    fillUIControls(painRecord);
                    ((AppActivity) getActivity()).getSupportActionBar().setTitle("Modify Entry");
                } else {
                    ((AppActivity) getActivity()).getSupportActionBar().setTitle("Add Entry");
                }
            });
            return painRecord;
        })).start();
    }

    @Override
    public void onDestroyView() {
        hideKeyboard();
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAlarmTimeSet(int hour, int minute) {
        setAlarm(hour, minute);
        ((AppActivity) getActivity()).isAlarmSet = true;
    }

    private void btnMoodOnClicked(View view) {
        hideKeyboard();
    }

    private TextWatcher editStepCountTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.inputLayoutStepCount.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private TextWatcher editGoalTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.inputLayoutStepCount.setSuffixText("/" + s);
                binding.inputLayoutGoal.setErrorEnabled(false);
                binding.inputLayoutStepCount.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private void fillUIControls(PainRecord painRecord) {
        if (painRecord != null) {
            binding.sliderIntensityLevel.setValue(painRecord.getPainIntensityLevel());
            int painAreaId = getChipByName(painRecord.getPainArea());
            if (painAreaId > 0)
                binding.painAreaChipGroup.check(painAreaId);
            int moodButtonId = getMoodButtonByName(painRecord.getMood());
            if (moodButtonId > 0) {
                binding.btnMoodGroup.check(moodButtonId);
                if (painRecord.getMood().toLowerCase().contains("good"))
                    binding.horizontalScrollButtonToggleGroup.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
            binding.editStepCount.setText(String.valueOf(painRecord.getStepCount()));
        }
    }

    private void sliderIntensityLevelOnChanged(Slider slider, float value, boolean fromUser) {
        hideKeyboard();
        binding.textSliderIntensityValue.setText(String.valueOf((int) value));
    }

    private void painAreaChipGroupOnCheckedChange(@NonNull ChipGroup chipGroup, int checkedId) {
        hideKeyboard();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            chip.setTextColor(getResources().getColor(
                    chip.isChecked() ? R.color.teal_900 : R.color.grey_900, null));
        }
    }

    private void setVisibilityOfUI(boolean isEnabled, @NonNull ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            child.setEnabled(isEnabled);
            if (child instanceof ViewGroup) {
                setVisibilityOfUI(isEnabled, (ViewGroup) child);
            }
        }
        binding.btnEdit.setEnabled(!isEnabled);
        if (!isEnabled) {
            binding.editStepCount.setTextColor(Color.GRAY);
            for (int i = 0; i < binding.painAreaChipGroup.getChildCount(); i++) {
                Chip chip = (Chip) binding.painAreaChipGroup.getChildAt(i);
                chip.setTextColor(getResources().getColor(
                        chip.isChecked() ? R.color.teal_900 : R.color.grey_500, null));
            }
        } else {
            binding.editStepCount.setTextColor(Color.BLACK);
            for (int i = 0; i < binding.painAreaChipGroup.getChildCount(); i++) {
                Chip chip = (Chip) binding.painAreaChipGroup.getChildAt(i);
                chip.setTextColor(getResources().getColor(
                        chip.isChecked() ? R.color.teal_900 : R.color.grey_900, null));
            }
        }
    }

    private int getChipByName(@NonNull String chipName) {
        for (int i = 0; i < binding.painAreaChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) binding.painAreaChipGroup.getChildAt(i);
            if (chip.getText().toString().toLowerCase().equals(chipName))
                return chip.getId();
        }
        return -1;
    }

    private int getMoodButtonByName(@NonNull String moodName) {
        switch (moodName.toLowerCase()) {
            case "very low":
                return binding.btnMoodVeryLow.getId();
            case "low":
                return binding.btnMoodLow.getId();
            case "average":
                return binding.btnMoodAverage.getId();
            case "good":
                return binding.btnMoodGood.getId();
            case "very good":
                return binding.btnMoodVeryGood.getId();
            default:
                return -1;
        }
    }

    // Save pain data entry with/without weather information.
    // If weather info is not up to date then it will make a call to weather api and update weather information first then save pain data entry.
    // If weather call fails then it saves pain data entry with default weather information.
    private void btnSaveOnClick(View view) {
        hideKeyboard();
        if (inputValidationCheck()) {
            setVisibilityOfUI(false, binding.mainDataEntryLayout);
            int intensityLevel = Math.round(binding.sliderIntensityLevel.getValue());
            String painArea = ((Chip) binding.painAreaChipGroup.findViewById(binding.painAreaChipGroup.getCheckedChipId())).getText().toString();
            String mood = ((Button) binding.btnMoodGroup.findViewById(binding.btnMoodGroup.getCheckedButtonId())).getText().toString();
            int goal = Integer.parseInt(binding.editGoal.getText().toString());
            int stepCount = Integer.parseInt(binding.editStepCount.getText().toString());
            if (WeatherInfo.getInstance() == null
                    || TimeUnit.HOURS.convert(new Date().getTime() - WeatherInfo.lastFetched.getTime(), TimeUnit.MILLISECONDS) >= 1) {
                WeatherInfo.clearInstance();
                Call<WeatherResponse> weatherResponseCallAsync = RetrofitClient
                        .getRetrofitService().weatherCall(
                                WeatherInfo.latitude,
                                WeatherInfo.longitude,
                                WeatherInfo.units,
                                WeatherInfo.API_KEY);
                weatherResponseCallAsync.enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            WeatherInfo.setInstance(response.body(), true);
                            WeatherInfo.lastFetched = new Date();
                            SaveEntry(intensityLevel, painArea, mood, goal, stepCount, true);
                        } else {
                            Log.i("ERROR", "Weather call failed");
                            SaveEntry(intensityLevel, painArea, mood, goal, stepCount, false);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                        Log.e(THIS_CLASS, "WEATHER CALL EXCEPTION: btnSaveOnClick: " + t.getMessage());
                    }
                });
            } else {
                SaveEntry(intensityLevel, painArea, mood, goal, stepCount, true);
            }
        }
    }

    private void SaveEntry(int intensityLevel, String painArea, String mood, int goal, int stepCount, boolean withWeather) {
        last_inserted_timestamp = Converters.dateToTimestamp(new Date());
        try {
            PainRecord newPainRecord = new PainRecord(
                    UserInfo.getUserEmail(),
                    last_inserted_timestamp,
                    intensityLevel,
                    painArea,
                    mood,
                    goal,
                    stepCount,
                    WeatherInfo.getWeatherResponse() != null ? WeatherInfo.getWeatherResponse().getMain().getTemp() : 0,
                    WeatherInfo.getWeatherResponse() != null ? WeatherInfo.getWeatherResponse().getMain().getHumidity() : 0,
                    WeatherInfo.getWeatherResponse() != null ? WeatherInfo.getWeatherResponse().getMain().getPressure() : 0
            );
            if (uid < 0) {
                // This task must be executed once in a day.
                viewModel.insert(newPainRecord);
                attachToWorker(newPainRecord);
                viewModel.findRecordByTimestamp(last_inserted_timestamp).thenApply(painRecord -> {
                    uid = painRecord.getUid();
                    UserInfo.getInstance().setTodayEntryUID(painRecord.getUid());
                    return painRecord;
                });
            } else {
                newPainRecord.setUid(uid);
                viewModel.update(newPainRecord);
            }
            getActivity().runOnUiThread(() -> {
                if (withWeather)
                    Toast.makeText(getContext(), "Pain entry saved successfully.", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), "Pain entry saved without weather information.", Toast.LENGTH_SHORT).show();
                NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
                if (navigationView != null) {
                    navigationView.getMenu().findItem(R.id.nav_pain_data_entry_fragment).setTitle("Modify Entry");
                }
            });
        } catch (Exception e) {
            Log.i("EXCEPTION", e.getMessage());
        }
    }

    private void btnEditOnClicked(View view) {
        hideKeyboard();
        ((AppActivity) getActivity()).getSupportActionBar().setTitle("Modify Entry");
        setVisibilityOfUI(true, binding.mainDataEntryLayout);
        if (uid < 0) {
            CompletableFuture<PainRecord> painRecordCompletableFuture = viewModel.findRecordByTimestamp(last_inserted_timestamp);
            painRecordCompletableFuture.thenApply(painRecord -> {
                uid = painRecord.getUid();
                return painRecord;
            });
        }
    }

    private void setAlarm(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                hour,
                minute - 2, // 2 minutes before
                0);

        AlarmManager alarmMgr = (AlarmManager) getContext().getSystemService(getContext().ALARM_SERVICE);
        Intent intent = new Intent(getContext(), ReminderBroadcast.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
        // Cancel previous alarms.
        alarmMgr.cancel(alarmIntent);
        // Set new exact alarm and repeat it for one day when its invoke again.
        alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
//        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, alarmIntent);

        Log.w(THIS_CLASS, "setAlarm: New Alarm is set.");
        Toast.makeText(getContext(), "Alarm is set", Toast.LENGTH_SHORT).show();
    }

    private void attachToWorker(PainRecord newPainRecord) {
        long currentTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY) >= 22 ? 0 : 22, //TODO to do instantly change to calendar.get(Calendar.HOUR_OF_DAY)
                0, //TODO to do instantly + 1 minute delay change to calendar.get(Calendar.MINUTE) + 1
                0
        );
        long specificTimeToTrigger = calendar.getTimeInMillis();
        long delay = specificTimeToTrigger - currentTime;
        delay = delay < 0 ? 0 : delay;
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest
                .Builder(DataUploadWorker.class)
                .setInputData(createInputData(newPainRecord))
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build();
        // To ensure one work run by this application delete all added work: not used in production build.
        WorkManager.getInstance(getContext()).cancelAllWork();
        WorkManager.getInstance(getContext()).enqueueUniqueWork("DataUploadWork", ExistingWorkPolicy.REPLACE, workRequest);
        Log.i(THIS_CLASS, "Future Data Entry Work has been set and will be triggered at " + new SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(calendar.getTime()));
    }

    private Data createInputData(PainRecord painRecord) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        return new Data.Builder()
                .putInt("uid", painRecord.getUid())
                .putString("email", painRecord.getUserEmail())
                .putString("datetime", dateFormat.format(new Date(painRecord.getDateTime())))
                .putInt("painIntensityLevel", painRecord.getPainIntensityLevel())
                .putString("painArea", painRecord.getPainArea())
                .putString("mood", painRecord.getMood())
                .putInt("goal", painRecord.getGoal())
                .putInt("steps", painRecord.getStepCount())
                .putString("temperature", String.valueOf(painRecord.getTemperature()))
                .putString("humidity", String.valueOf(painRecord.getHumidity()))
                .putString("pressure", String.valueOf(painRecord.getPressure()))
                .build();
    }

    private boolean inputValidationCheck() {
        boolean isAllValid = true;
        String steps = binding.editStepCount.getText().toString();
        String goal = binding.editGoal.getText().toString();

        if (steps.isEmpty()) {
            binding.inputLayoutStepCount.setError("Steps cannot be blank");
            isAllValid = false;
        } else if (!goal.isEmpty() && Integer.parseInt(steps) > Integer.parseInt(goal)) {
            binding.inputLayoutStepCount.setError("Please increase your goal to add more steps.");
            isAllValid = false;
        }
        if (goal.isEmpty()) {
            binding.inputLayoutGoal.setError("Goal cannot be blank");
            isAllValid = false;
        }
        return isAllValid;
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
