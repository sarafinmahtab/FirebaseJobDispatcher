package com.sarafinmahtab.fbjobdispatcher;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

public class MainActivity extends AppCompatActivity {

    private String jobTag = "myScheduledJobId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);

        startBtn = findViewById(R.id.start_job);
        stopBtn = findViewById(R.id.stop_job);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                textView.setText("Job Scheduled");

                //scheduling new job
                //creating new firebase job dispatcher
                FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext()));
                //creating new job and adding it with dispatcher
                Job job = createJob(dispatcher, jobTag);
                dispatcher.mustSchedule(job);
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                textView.setText("Job Cancelled");

                cancelJob(getApplicationContext(), jobTag);
            }
        });
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

    @NonNull
    public static Job updateJob(FirebaseJobDispatcher dispatcher, String jobTag) {
        return dispatcher.newJobBuilder()
                //update if any task with the given tag exists.
                .setReplaceCurrent(true)
                //Integrate the job you want to start.
                .setService(ScheduledJobService.class)
                .setTag(jobTag)
                // Run between 30 - 60 seconds from now.
                .setTrigger(Trigger.executionWindow(10, 15))
                .build();
    }

    public void cancelJob(Context context, String jobTag){

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        //Cancel all the jobs for this package
        //dispatcher.cancelAll();
        // Cancel the job for this tag
        dispatcher.cancel(jobTag);
    }

    private TextView textView;

    private Button startBtn;
    private Button stopBtn;
}
