package com.sarafinmahtab.fbjobdispatcher;

import android.app.Application;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

/**
 * Created by Arafin on 05-Jan-18.
 */

public class MainApp extends Application {

    private String jobTag = "myScheduledJobId";

    @Override
    public void onCreate() {
        super.onCreate();

        //scheduling new job
        //creating new firebase job dispatcher
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext()));
        //creating new job and adding it with dispatcher
        Job job = createJob(dispatcher, jobTag);
        dispatcher.mustSchedule(job);
    }

    @NonNull
    public static Job createJob(FirebaseJobDispatcher dispatcher, String jobTag){

        return dispatcher.newJobBuilder()
                //persist the task across boots
                .setLifetime(Lifetime.FOREVER)
                //.setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                //call this service when the criteria are met.
                .setService(ScheduledJobService.class)
                //unique id of the task
                .setTag(jobTag)
                //don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)
                // We are mentioning that the job is periodic.
                .setRecurring(true)
                // Run between 30 - 60 seconds from now.
                .setTrigger(Trigger.executionWindow(290, 300))
                // retry with exponential backoff
                //.setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                //Run this job only when the network is available.
                //.setConstraints(Constraint.ON_ANY_NETWORK)
                .build();
    }
}
