package com.suvidha.Receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.util.Log;

import com.suvidha.Activities.QuarantineActivity;
import com.suvidha.R;

import java.util.logging.Handler;

/**
 * Created by sara on 2017/12/28.
 */

public class AlarmReceiver extends BroadcastReceiver {
    MediaPlayer mp = null;// Here
    private static final String TAG = "ak47";
    String set;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Loop running");
        if (true) {
            mp = MediaPlayer.create(context, R.raw.beep);//Onreceive gives you context
            final int[] count = {0}; // initialise outside listener to prevent looping

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                int maxCount = 30;
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if(count[0] < maxCount) {
                        count[0]++;
                        mediaPlayer.seekTo(0);
                        mediaPlayer.start();
                    }
                }});

            mp.start();// and this to play it
        } else {

        }

    }
}
