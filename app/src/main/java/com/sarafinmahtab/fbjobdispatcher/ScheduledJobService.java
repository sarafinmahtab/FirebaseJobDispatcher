package com.sarafinmahtab.fbjobdispatcher;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.Calendar;

/**
 * Created by Arafin on 28-Dec-17.
 */

public class ScheduledJobService extends JobService {

    private static final String TAG = ScheduledJobService.class.getSimpleName();

    @Override
    public boolean onStartJob(final JobParameters params) {
        //Offloading work to a new thread.
        Log.d(TAG, "completeJob: " + "jobStarted");

        new Thread(new MyThread(params)).start();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // If the job is failed it calls this method
        Log.d(TAG, "completeJob: " + "jobStartedFromStopJob");

        new Thread(new MyThread(params)).start();

        return true; // returns true to re-schedule the job again
    }

    public class MyThread implements Runnable {

        JobParameters params;

        MyThread(JobParameters params) {
            this.params = params;
        }

        @Override
        public void run() {

            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(System.currentTimeMillis());
            calendar1.set(Calendar.HOUR_OF_DAY, 21);
            calendar1.set(Calendar.MINUTE, 0);
            calendar1.set(Calendar.SECOND, 0);

            codeNeedToRun(1, calendar1);

            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTimeInMillis(System.currentTimeMillis());
            calendar2.set(Calendar.HOUR_OF_DAY, 21);
            calendar2.set(Calendar.MINUTE, 2);
            calendar2.set(Calendar.SECOND, 0);

            codeNeedToRun(2, calendar2);

            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTimeInMillis(System.currentTimeMillis());
            calendar3.set(Calendar.HOUR_OF_DAY, 21);
            calendar3.set(Calendar.MINUTE, 4);
            calendar3.set(Calendar.SECOND, 0);

            codeNeedToRun(3, calendar3);

            Calendar calendar4 = Calendar.getInstance();
            calendar4.setTimeInMillis(System.currentTimeMillis());
            calendar4.set(Calendar.HOUR_OF_DAY, 21);
            calendar4.set(Calendar.MINUTE, 6);
            calendar4.set(Calendar.SECOND, 0);

            codeNeedToRun(4, calendar4);

            Calendar calendar5 = Calendar.getInstance();
            calendar5.setTimeInMillis(System.currentTimeMillis());
            calendar5.set(Calendar.HOUR_OF_DAY, 21);
            calendar5.set(Calendar.MINUTE, 9);
            calendar5.set(Calendar.SECOND, 0);

            codeNeedToRun(5, calendar5);

            jobFinished(params, false); // finishes the job
            Log.d(TAG, "completeJob: " + "jobFinished");
        }
    }

    private void codeNeedToRun(int requestCode, Calendar calendar) {
        Intent intent = new Intent(getApplicationContext(), AlarmNotificationReceiver.class);
        AlarmManager manager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        boolean alarmRunning = (PendingIntent.getBroadcast(getApplicationContext(), requestCode, intent, PendingIntent.FLAG_NO_CREATE) != null);
        Log.d("AlarmRunning " + String.valueOf(requestCode), String.valueOf(alarmRunning));

        if(!alarmRunning) {
            Log.d("Alarming", "Alarm On");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),requestCode, intent,PendingIntent.FLAG_UPDATE_CURRENT);

            if(manager != null) {
                manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }
}
