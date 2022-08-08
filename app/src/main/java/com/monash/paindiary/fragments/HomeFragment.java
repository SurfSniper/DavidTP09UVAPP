package com.monash.paindiary.fragments;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.google.android.material.navigation.NavigationView;
import com.monash.paindiary.R;
import com.monash.paindiary.activities.AppActivity;
import com.monash.paindiary.apis.weather.RetrofitClient;
import com.monash.paindiary.apis.weather.WeatherResponse;
import com.monash.paindiary.databinding.FragmentHomeBinding;
import com.monash.paindiary.helper.Converters;
import com.monash.paindiary.helper.UserInfo;
import com.monash.paindiary.helper.WeatherInfo;
import com.monash.paindiary.viewmodel.PainRecordViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private final static String THIS_CLASS = "HomeFragment";
    private FragmentHomeBinding binding;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.btnCreateNewEntry.setEnabled(true);

        binding.textTime.setText(new SimpleDateFormat("EEE, dd MMM yyyy").format(new Date()));

        retrieveTodayEntry();

        binding.btnCreateNewEntry.setOnClickListener(this::btnCreateNewEntryOnClicked);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppActivity)getActivity()).ShowProgress(false);
        setWeatherData(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void btnCreateNewEntryOnClicked(View view) {
        binding.btnCreateNewEntry.setEnabled(false);
        Navigation.findNavController(view).navigate(
                R.id.nav_pain_data_entry_fragment,
                null,
                new NavOptions.Builder()
                        .setEnterAnim(R.anim.slide_up)
                        .setExitAnim(R.anim.fade_out)
                        .setPopExitAnim(R.anim.slide_down)
                        .build());
    }

    private void setWeatherData(boolean forceRefresh) {
        if (WeatherInfo.getInstance() == null
                || forceRefresh
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
                public void onResponse(Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        WeatherInfo.setInstance(response.body(), true);
                        WeatherInfo.lastFetched = new Date();
                        getActivity().runOnUiThread(() -> {
                            binding.textTemperature.setText(String.valueOf(Math.round(WeatherInfo.getWeatherResponse().getMain().getUv())));
                            binding.textHumidity.setText(String.valueOf(Math.round(WeatherInfo.getWeatherResponse().getMain().getHumidity())));
                            binding.textPressure.setText(String.valueOf(Math.round(WeatherInfo.getWeatherResponse().getMain().getPressure())));
                            String iconText = Converters.getFontAwesomeWeatherIcon(WeatherInfo.getWeatherResponse().getWeather().get(0).getMain());
                            binding.fontAwesomeWeatherIcon.setText(
                                    Html.fromHtml(iconText.isEmpty() ?
                                                    "" :
                                                    "&#x" + iconText + ";",
                                            Html.FROM_HTML_MODE_LEGACY));
                            binding.textWeatherText.setText(WeatherInfo.getWeatherResponse().getWeather().get(0).getMain());
                            binding.textWeatherCity.setText(WeatherInfo.getWeatherResponse().getName());
                            binding.textWind.setText(String.valueOf(WeatherInfo.getWeatherResponse().getWind().getSpeed()));
                        });
                    } else {
                        binding.textTemperature.setText("--");
                        binding.textHumidity.setText("-");
                        binding.textPressure.setText("-");
                        Log.i("ERROR", "Weather call failed");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
//                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(THIS_CLASS, "Exception: setWeatherData: " + t.getMessage());
                }
            });
        } else if (WeatherInfo.getWeatherResponse() != null) {
            binding.textTemperature.setText(String.valueOf(Math.round(WeatherInfo.getWeatherResponse().getMain().getTemp())));
            binding.textHumidity.setText(String.valueOf(Math.round(WeatherInfo.getWeatherResponse().getMain().getHumidity())));
            binding.textPressure.setText(String.valueOf(Math.round(WeatherInfo.getWeatherResponse().getMain().getPressure())));
            String iconText = Converters.getFontAwesomeWeatherIcon(WeatherInfo.getWeatherResponse().getWeather().get(0).getMain());
            binding.fontAwesomeWeatherIcon.setText(
                    Html.fromHtml(iconText.isEmpty() ?
                                    "" :
                                    "&#x" + iconText + ";",
                            Html.FROM_HTML_MODE_LEGACY));
            binding.textWeatherText.setText(WeatherInfo.getWeatherResponse().getWeather().get(0).getMain());
            binding.textWeatherCity.setText(WeatherInfo.getWeatherResponse().getName());
            binding.textWind.setText(String.valueOf(WeatherInfo.getWeatherResponse().getWind().getSpeed()));
        }
    }

    private void retrieveTodayEntry() {
        if (UserInfo.getInstance() != null && UserInfo.getInstance().getTodayEntryUID() < 0) {
            PainRecordViewModel viewModel = new ViewModelProvider(getActivity()).get(PainRecordViewModel.class);
            new Thread(() -> {
                viewModel.findRecordByDate(new Date()).thenApply(painRecord -> {
                    int uid = -1;
                    if (painRecord != null) {
                        uid = painRecord.getUid();
                        UserInfo.getInstance().setTodayEntryUID(uid);
                    }
                    int finalUid = uid;
                    getActivity().runOnUiThread(() -> {
                        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
                        if (finalUid >= 0) {
                            binding.btnCreateNewEntry.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_edit_note_24, null));
                            if (navigationView != null) {
                                navigationView.getMenu().findItem(R.id.nav_pain_data_entry_fragment).setTitle("Modify Entry");
                            }
                        } else {
                            binding.btnCreateNewEntry.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_note_add_24, null));
                            if (navigationView != null) {
                                navigationView.getMenu().findItem(R.id.nav_pain_data_entry_fragment).setTitle("Add Entry");
                            }
                        }
                    });
                    return painRecord;
                });
            }).start();
        } else {
            binding.btnCreateNewEntry.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_edit_note_24, null));
            NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
            if (navigationView != null) {
                navigationView.getMenu().findItem(R.id.nav_pain_data_entry_fragment).setTitle("Modify Entry");
            }
        }
    }


}
