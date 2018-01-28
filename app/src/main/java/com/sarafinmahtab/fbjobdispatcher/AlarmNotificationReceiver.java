package com.sarafinmahtab.fbjobdispatcher;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Arafin on 12/11/2017.
 */

public class AlarmNotificationReceiver extends BroadcastReceiver {

    public static final String ANDROID_CHANNEL_ID = "com.sarafinmahtab.fbjobdispatcher.ANDROID";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("AlarmFired", "At Receiver");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ANDROID_CHANNEL_ID);

        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("New Job Started!")
                .setContentText("Welcome to FireBase JobDispatcher")
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentInfo("Info");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);;
        if(notificationManager != null) {
            notificationManager.notify(1,builder.build());
        }
    }
}
