package com.monash.paindiary.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.monash.paindiary.databinding.LayoutPainRecordBinding;
import com.monash.paindiary.entity.PainRecord;
import com.monash.paindiary.helper.Converters;
import com.monash.paindiary.viewmodel.PainRecordViewModel;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String THIS_CLASS = "RecyclerViewAdapter";
    private List<PainRecord> painRecordList = new ArrayList<>();

    public void setData(List<PainRecord> painRecordList) {
        this.painRecordList = painRecordList;
        notifyDataSetChanged();
    }
//    public RecyclerViewAdapter(List<PainRecord> painRecordList) {
//        this.painRecordList = painRecordList;
//    }

//    public RecyclerViewAdapter(FragmentActivity fragmentActivity, LifecycleOwner lifecycleOwner) {
//        PainRecordViewModel viewModel = new ViewModelProvider(fragmentActivity).get(PainRecordViewModel.class);
//        viewModel.getAllPainRecords().observe(lifecycleOwner, painRecords -> {
//            painRecordList = painRecords;
//            notifyDataSetChanged();
//        });
//    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutPainRecordBinding binding = LayoutPainRecordBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder viewHolder, int position) {
        final PainRecord painRecord = painRecordList.get(position);
        viewHolder.binding.textHiddenRecordId.setText(String.valueOf(painRecord.getUid()));
        viewHolder.binding.textEntryDate.setText((new SimpleDateFormat("EEE, dd MMM yyyy")).format(new Date(painRecord.getDateTime())));
        viewHolder.binding.painIntensityValue.setText(String.valueOf(painRecord.getPainIntensityLevel()));
        viewHolder.binding.painAreaValue.setText(painRecord.getPainArea());
        viewHolder.binding.dailyStepValue.setText(String.format("%s/%s", Converters.formatInteger(painRecord.getStepCount()), Converters.formatInteger(painRecord.getGoal())));
        viewHolder.binding.moodValue.setText(painRecord.getMood());
        viewHolder.binding.temperatureValue.setText(Html.fromHtml(String.format("%s%s", Math.round(painRecord.getTemperature()), "&#176; C"), HtmlCompat.FROM_HTML_MODE_LEGACY));
        viewHolder.binding.humidityValue.setText(Html.fromHtml(String.format("%s %s", Math.round(painRecord.getHumidity()), "%"), HtmlCompat.FROM_HTML_MODE_LEGACY));
        viewHolder.binding.pressureValue.setText(Html.fromHtml(String.format("%s %s", Math.round(painRecord.getPressure()), "hPa"), HtmlCompat.FROM_HTML_MODE_LEGACY));

        // TODO Not required by assignment specification
//        viewHolder.itemView.setOnClickListener(v -> {
//            int uid = Integer.parseInt(((TextView) v.findViewById(R.id.text_hidden_record_id)).getText().toString());
//            try {
//                long timestamp = (new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")).parse(
//                        ((TextView) v.findViewById(R.id.text_entry_date)).getText().toString()
//                ).getTime();
//                if (isTodayRecord(timestamp)) {
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("uid", uid);
//                    Navigation.findNavController(v).navigate(
//                            R.id.nav_pain_data_entry_fragment,
//                            bundle,
//                            new NavOptions.Builder()
//                                    .setEnterAnim(R.anim.slide_up)
//                                    .setExitAnim(R.anim.fade_out)
//                                    .setPopExitAnim(R.anim.slide_down)
//                                    .build()
//                    );
//                }
//            } catch (Exception e) {
//                Log.e(THIS_CLASS, "Exception: itemViewClick: " + e.getMessage());
//            }
//        });
    }

    private boolean isTodayRecord(long timestamp) {
        LocalDate currentDate = Converters.convertToLocalDateViaInstant(new Date());
        return timestamp >= Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime() &&
                timestamp <= Date.from(LocalDateTime.of(currentDate, LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant()).getTime();
    }

    @Override
    public int getItemCount() {
        return painRecordList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LayoutPainRecordBinding binding;

        public ViewHolder(LayoutPainRecordBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
