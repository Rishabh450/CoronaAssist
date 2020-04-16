package com.suvidha.Receiver;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;

import com.suvidha.Activities.MainActivity;
import com.suvidha.Activities.QuarantineActivity;
import com.suvidha.Models.MusicControl;
import com.suvidha.R;
import com.suvidha.Utilities.ApplicationClass;
import com.suvidha.Utilities.LiveLocationService;

import java.util.logging.Handler;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by sara on 2017/12/28.
 */

public class AlarmReceiver extends BroadcastReceiver { ;// Here


    private static final String TAG = "ak47";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Loop running");
        String ac = "run";

        if (intent.getAction() != null)
            ac = intent.getAction();

        MusicControl.getInstance(context).playMusic();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(2, getNotificationAlarm(context));


    }

    public Notification getNotificationAlarm(Context context)
    {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("app_channel", "Demo Notification", NotificationManager.IMPORTANCE_LOW);
            channel.setSound(null, null);
            NotificationManager mManager = (NotificationManager) context. getSystemService(NOTIFICATION_SERVICE);
            mManager.createNotificationChannel(channel);
        }
        Intent snoozeIntent = new Intent(context, MediaReceiver.class);


        snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 1);
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(context, 1, snoozeIntent, 0);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentTitle("Quarantine Alarm")
                .setAutoCancel(true)

                .addAction(R.drawable.ic_launcher_suvidha_foreground, "STOP",
                        snoozePendingIntent)
                .setContentText("Your need to send your selfie to Police")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(contentIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            notificationBuilder.setChannelId("app_channel");
        notificationBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
        return notificationBuilder.build();
    }
}
