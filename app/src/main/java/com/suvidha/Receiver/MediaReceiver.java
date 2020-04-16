package com.suvidha.Receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import com.suvidha.Activities.LoginActivity;
import com.suvidha.Models.MusicControl;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MediaReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("band hua", "Loop running");
        MusicControl.getInstance(context).stopMusic();
        Intent intent1=new Intent(context, LoginActivity.class);
        context.startActivity(intent1);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager. cancel(2);


    }
}
