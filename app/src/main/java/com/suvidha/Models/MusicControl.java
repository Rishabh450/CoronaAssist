package com.suvidha.Models;

import android.content.Context;
import android.media.MediaPlayer;

import com.suvidha.R;

public class MusicControl {
    private static MusicControl sInstance;
    private Context mContext;
    private MediaPlayer mMediaPlayer;
    public MusicControl(Context context) {
        mContext = context;
    }

    public static MusicControl getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MusicControl(context);
        }
        return sInstance;
    }

    public void playMusic() {
        mMediaPlayer = MediaPlayer.create(mContext, R.raw.music);
        mMediaPlayer.start();
    }

    public void stopMusic() {
        if(mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.seekTo(0);
        }
    }
}