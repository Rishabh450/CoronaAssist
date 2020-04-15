package com.suvidha.Utilities;

import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;

import com.suvidha.R;

public class ApplicationClass extends Application {
   public MediaPlayer mp=MediaPlayer.create(this, R.raw.beep);;


    @Override
    public void onCreate() {
        super.onCreate();

    }

    public ApplicationClass() {


    }

    public MediaPlayer getMp() {
        return mp;
    }

    public void setMp(MediaPlayer mp) {
        this.mp = mp;
    }
}
