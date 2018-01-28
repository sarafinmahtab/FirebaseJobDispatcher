package com.sarafinmahtab.fbjobdispatcher;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private String jobTag = "myScheduledJobId";

    private static final String sharedPreferenceName = "demoSharedPref";
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);

        startBtn = findViewById(R.id.start_job);
        stopBtn = findViewById(R.id.stop_job);
        set = findViewById(R.id.set);

        sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                cancelJob(getApplicationContext(), jobTag);
            }
        });

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        editor = sharedPreferences.edit();
                        editor.putInt("hour", selectedHour);
                        editor.putInt("min", selectedMinute);
                        editor.apply();

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
    }

    @NonNull
    public static Job createJob(FirebaseJobDispatcher dispatcher, String jobTag){

        Log.d("JobCreate", "Job Created!!");

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
                //.setExtras(bundle)
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

    private EditText editText;

    private Button startBtn;
    private Button stopBtn;
    private Button set;
}
