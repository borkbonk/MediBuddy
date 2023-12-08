package com.sp.a0miniproj.reminder;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.sp.a0miniproj.R;

public class RingtoneService extends Service {
    private MediaPlayer mediaPlayer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null && intent.getAction().equals("START_RINGTONE")) {
            playRingtone();
        }
        return START_STICKY;
    }

    private void playRingtone() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm); // Replace with your ringtone file
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRingtone();
    }

    private void stopRingtone() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
