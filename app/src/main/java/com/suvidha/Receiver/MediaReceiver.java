package com.suvidha.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import com.suvidha.Activities.LoginActivity;
import com.suvidha.Models.MusicControl;

public class MediaReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("band hua", "Loop running");
        MusicControl.getInstance(context).stopMusic();
        Intent intent1=new Intent(context, LoginActivity.class);
        context.startActivity(intent1);

    }
}
