package com.monash.paindiary.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.monash.paindiary.entity.PainRecord;

import java.util.List;

@Dao
public interface PainRecordDAO {

    @Query("SELECT * FROM PAINRECORD ORDER BY timestamp DESC")
    LiveData<List<PainRecord>> getAll();

    @Query("SELECT * FROM PAINRECORD ORDER BY timestamp DESC")
    List<PainRecord> getAllSync();

    @Query("SELECT * FROM PAINRECORD WHERE uid = :uid LIMIT 1")
    PainRecord findByID(int uid);

    @Query("SELECT * FROM PAINRECORD WHERE timestamp = :timestamp LIMIT 1")
    PainRecord findByTimestamp(double timestamp);

    @Query("SELECT * FROM PAINRECORD WHERE timestamp BETWEEN :startTime AND :endTime LIMIT 1")
    PainRecord findByDate(double startTime, double endTime);

    @Query("SELECT * FROM PAINRECORD WHERE timestamp BETWEEN :startDateTime AND :endDateTime")
    List<PainRecord> findBetweenDates(double startDateTime, double endDateTime);

    @Insert
    void insert(PainRecord painRecord);

    @Delete
    void delete(PainRecord painRecord);

    @Query("DELETE FROM PAINRECORD")
    void deleteAll();

    @Update
    void updatePainRecord(PainRecord painRecord);

}
