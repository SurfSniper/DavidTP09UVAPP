package com.monash.paindiary.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.monash.paindiary.dao.PainRecordDAO;
import com.monash.paindiary.entity.PainRecord;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {PainRecord.class}, version = 4, exportSchema = false)
public abstract class PainRecordDatabase extends RoomDatabase {
    public abstract PainRecordDAO painRecordDAO();

    private static PainRecordDatabase INSTANCE;

    // we create an ExecutorService with a fixed thread pool so we can later run
    // database operations asynchronously on a background thread.
    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    //A synchronized method in a multi threaded environment means that two threads
    //are not allowed to access data at the same time
    public static synchronized PainRecordDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), PainRecordDatabase.class, "PainRecordDatabase")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

}
