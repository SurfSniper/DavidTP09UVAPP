package com.monash.paindiary.helper;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.navigation.NavDeepLinkBuilder;

import com.monash.paindiary.R;
import com.monash.paindiary.activities.AppActivity;

import java.util.Calendar;

public class ReminderBroadcast extends BroadcastReceiver {
    private final String NOTIFICATION_CHANNEL = "notifyPainEntry";
    @Override
    public void onReceive(Context context, Intent intent) {
        setNotificationTemp(context);

        MediaPlayer mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_NOTIFICATION_URI);
        mediaPlayer.start();
        setRepeatAlarm(context);
        Log.w("Broadcast Alarm", "New alarm set for next day.");
    }

    private void setRepeatAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH) + 1,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), // set same time
                0);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderBroadcast.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        // Cancel previous alarms.
        alarmMgr.cancel(alarmIntent);
        // Set new repeating alarm.
        alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
    }

    private void setNotificationTemp(Context context) {
        createNotificationChannel(context);

        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(context, AppActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        Bundle bundle = new Bundle();
        bundle.putBoolean("isFromNotification", true);
        // Get the PendingIntent containing the entire back stack
        PendingIntent pendingIntent = new NavDeepLinkBuilder(context)
                .setComponentName(AppActivity.class)
                .setGraph(R.navigation.mobile_navigation)
                .setDestination(R.id.nav_pain_data_entry_fragment)
                .setArguments(bundle)
                .createPendingIntent();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_baseline_doorbell_24)
                .setContentTitle("Pain entry Reminder")
                .setContentText("Add your pain entry for today.")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notificationManagerCompat.notify(0, builder.build());
    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, NOTIFICATION_CHANNEL, importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
