package com.monash.paindiary.helper;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.monash.paindiary.entity.PainRecordStr;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUploadWorker extends Worker {

    public DataUploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        PainRecordStr painRecordStr = convertInputData(getInputData());
        if (painRecordStr == null) {
            Log.i("ERROR", "DataUploadWorker: Can not get(parse) PainRecord.");
            return Result.failure();
        } else {
            writeDataToFirebase(painRecordStr);
            Log.i("SUCCESS", "DataUploadWorker: Data Upload work completed successfully.");
            Log.i("SUCCESS", "DATA UPLOADED: \n" + painRecordStr.toString());
            return Result.success();
        }
    }

    private PainRecordStr convertInputData(Data inputData) {
        return new PainRecordStr(
                inputData.getInt("uid", 0),
                inputData.getString("email"),
                inputData.getString("datetime"),
                inputData.getInt("painIntensityLevel", -1),
                inputData.getString("painArea"),
                inputData.getString("mood"),
                inputData.getInt("goal", -1),
                inputData.getInt("steps", -1),
                inputData.getString("temperature"),
                inputData.getString("humidity"),
                inputData.getString("pressure")
        );
    }

    private void writeDataToFirebase(PainRecordStr painRecord) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = firebaseDatabase.getReference("PainRecords");
        String userId = databaseRef.push().getKey();
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                Log.i("DataUploadWorker", "Firebase data modified at " + dateFormat.format(new Date()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("DataUploadWorker", "Firebase operation cancelled. Error: " + error);
            }
        });
        databaseRef.child(userId).setValue(painRecord);

    }
}
