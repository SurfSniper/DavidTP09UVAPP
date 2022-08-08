package com.monash.paindiary.repository;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.monash.paindiary.dao.PainRecordDAO;
import com.monash.paindiary.database.PainRecordDatabase;
import com.monash.paindiary.entity.PainRecord;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PainRecordRepository {
    private PainRecordDAO painRecordDAO;
    private LiveData<List<PainRecord>> allPainRecords;

    public PainRecordRepository(Application application) {
        PainRecordDatabase database = PainRecordDatabase.getInstance(application);
        painRecordDAO = database.painRecordDAO();
        allPainRecords = painRecordDAO.getAll();
    }

    public LiveData<List<PainRecord>> getAllPainRecords() {
        return allPainRecords;
    }

    public List<PainRecord> getAllPainRecordsSync() {
        return painRecordDAO.getAllSync();
    }

    public void insert(final PainRecord painRecord) {
        PainRecordDatabase.databaseWriteExecutor.execute(() -> painRecordDAO.insert(painRecord));
    }

    public void delete(final PainRecord painRecord) {
        PainRecordDatabase.databaseWriteExecutor.execute(() -> painRecordDAO.delete(painRecord));
    }

    public void deleteAll() {
        PainRecordDatabase.databaseWriteExecutor.execute(() -> painRecordDAO.deleteAll());
    }

    public void updatePainRecord(final PainRecord painRecord) {
        PainRecordDatabase.databaseWriteExecutor.execute(() -> painRecordDAO.updatePainRecord(painRecord));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findByID(final int uid) {
        return CompletableFuture.supplyAsync(() -> painRecordDAO.findByID(uid), PainRecordDatabase.databaseWriteExecutor);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findByTimestamp(final double timestamp) {
        return CompletableFuture.supplyAsync(() -> painRecordDAO.findByTimestamp(timestamp), PainRecordDatabase.databaseWriteExecutor);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findByDate(final double startTimestamp, double endTimestamp) {
        return CompletableFuture.supplyAsync(() -> painRecordDAO.findByDate(startTimestamp, endTimestamp), PainRecordDatabase.databaseWriteExecutor);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<PainRecord>> findBetweenDate(final double startTimestamp, double endTimestamp) {
        return CompletableFuture.supplyAsync(() -> painRecordDAO.findBetweenDates(startTimestamp, endTimestamp), PainRecordDatabase.databaseWriteExecutor);
    }
}
