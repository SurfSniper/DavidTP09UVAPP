package com.monash.paindiary.viewmodel;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.monash.paindiary.entity.PainRecord;
import com.monash.paindiary.helper.Converters;
import com.monash.paindiary.repository.PainRecordRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PainRecordViewModel extends AndroidViewModel {
    private PainRecordRepository repository;
    private LiveData<List<PainRecord>> allPainRecords;

    public PainRecordViewModel(@NonNull Application application) {
        super(application);
        repository = new PainRecordRepository(application);
        allPainRecords = repository.getAllPainRecords();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findRecordByID(final int uid) {
        return repository.findByID(uid);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findRecordByTimestamp(final double timestamp) {
        return repository.findByTimestamp(timestamp);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findRecordByDate(final Date date) {
        LocalDate localDate = Converters.convertToLocalDateViaInstant(date);
        return repository.findByDate(
                Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime(),
                Date.from(LocalDateTime.of(localDate, LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant()).getTime());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<PainRecord>> findRecordsBetweenDate(final Date startDate, final Date endDate) {
        LocalDate localStartDate = Converters.convertToLocalDateViaInstant(startDate);
        LocalDate localEndDate = Converters.convertToLocalDateViaInstant(endDate);
        return repository.findBetweenDate(
                Date.from(localStartDate.minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime(),
                Date.from(localEndDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime());
    }

    public LiveData<List<PainRecord>> getAllPainRecords() {
        return allPainRecords;
    }

    public List<PainRecord> getAllPainRecordsSync() {
        return repository.getAllPainRecordsSync();
    }

    public void insert(PainRecord painRecord) {
        painRecord.setTemperature(Converters.customRoundFloat(painRecord.getTemperature()));
        painRecord.setHumidity(Converters.customRoundFloat(painRecord.getHumidity()));
        painRecord.setPressure(Converters.customRoundFloat(painRecord.getPressure()));
        repository.insert(painRecord);
    }

    public void delete(PainRecord painRecord) {
        repository.delete(painRecord);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public void update(PainRecord painRecord) {
        repository.updatePainRecord(painRecord);
    }
}
