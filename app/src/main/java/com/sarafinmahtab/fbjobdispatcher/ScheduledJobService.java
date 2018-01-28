package com.sarafinmahtab.fbjobdispatcher;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.Calendar;

/**
 * Created by Arafin on 28-Dec-17.
 */

public class ScheduledJobService extends JobService {

    private static final String TAG = ScheduledJobService.class.getSimpleName();

    private static final String sharedPreferenceName = "demoSharedPref";
    private SharedPreferences sharedPreferences;

    private int hour, min;

    @Override
    public boolean onStartJob(final JobParameters params) {
        //Offloading work to a new thread.
        Log.d(TAG, "completeJob: " + "jobStarted");

        setAlarm(params);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // If the job is failed it calls this method
        Log.d(TAG, "completeJob: " + "jobStartedFromStopJob");

        setAlarm(params);

        return true; // returns true to re-schedule the job again
    }

    private void setAlarm(JobParameters params) {

        sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);

        hour = sharedPreferences.getInt("hour", 0);
        min = sharedPreferences.getInt("min", 0);

        String value = String.valueOf(hour) + ":" + String.valueOf(min);
        Log.d("hour_min", value);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.MINUTE, hour);
        calendar.set(Calendar.MINUTE, min);

        Intent intent = new Intent(getBaseContext(), AlarmNotificationReceiver.class);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        boolean alarmRunning = (PendingIntent.getBroadcast(getBaseContext(), 1, intent, PendingIntent.FLAG_NO_CREATE) != null);
        Log.d("AlarmRunning " + String.valueOf(1), String.valueOf(alarmRunning));

        System.out.println("Next Prayer Time in seconds: " + calendar.getTimeInMillis()/1000);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        if(alarmRunning) {
            Log.d("Alarming", "Alarm On");

            if(manager != null) {
                manager.cancel(pendingIntent);
            }
        }

        if(manager != null) {
            manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        jobFinished(params, false); // finishes the job
        Log.d(TAG, "completeJob: " + "jobFinished");
    }
}
