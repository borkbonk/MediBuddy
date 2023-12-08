package com.sp.a0miniproj.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StopRingtoneReceiver extends BroadcastReceiver {
    public static final String REMINDER_ID_EXTRA = "reminder_id_extra";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("StopRingtoneReceiver", "Stop Ringtone Action Clicked");

        // Send broadcast to the running AlarmReceiver to stop the ringtone
        Intent stopIntent = new Intent(context, AlarmReceiver.class);
        stopIntent.setAction("STOP_RINGTONE");
        stopIntent.putExtra(StopRingtoneReceiver.REMINDER_ID_EXTRA, intent.getLongExtra(REMINDER_ID_EXTRA, -1));
        context.sendBroadcast(stopIntent);
    }
}
