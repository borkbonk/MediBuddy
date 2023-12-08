package com.sp.a0miniproj.reminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.sp.a0miniproj.R;

public class AlarmReceiver extends BroadcastReceiver {
    public static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "ReminderChannel";
    private static final CharSequence NOTIFICATION_CHANNEL_NAME = "Reminder Channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("STOP_RINGTONE")) {
            // Stop the ringtone
            Intent stopIntent = new Intent(context, RingtoneService.class);
            context.stopService(stopIntent);
        } else {
            String title = intent.getStringExtra("title");
            String description = intent.getStringExtra("description");
            long reminderId = intent.getLongExtra("reminder_id", -1); // Get the reminder ID

            // Show a notification
            showNotification(context, title, description, reminderId);

            // Start the RingtoneService to play the ringtone
            Intent ringtoneIntent = new Intent(context, RingtoneService.class);
            ringtoneIntent.setAction("START_RINGTONE");
            context.startService(ringtoneIntent);
        }
    }

    private void showNotification(Context context, String title, String description, long reminderId) {
        // Create an intent for the StopRingtoneReceiver
        Intent stopRingtoneIntent = new Intent(context, StopRingtoneReceiver.class);
        stopRingtoneIntent.putExtra(StopRingtoneReceiver.REMINDER_ID_EXTRA, reminderId); // Pass the reminder ID
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(
                context,
                (int) reminderId,
                stopRingtoneIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Create an action for the Stop Ringtone button (use setOngoing(true) to make it an ongoing notification)
        NotificationCompat.Action stopAction = new NotificationCompat.Action.Builder(
                R.drawable.baseline_stop_circle_24,
                "Stop Ringtone",
                stopPendingIntent
        ).build();

        // Create an intent to open the app when the notification is clicked
        Intent appIntent = new Intent(context, Reminder.class);
        appIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(notificationChannel);
        }

        // Create the notification with setOngoing(true) to make it an ongoing notification
        Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(R.drawable.baseline_notifications_active_24)

                .setContentIntent(appPendingIntent)
                .setAutoCancel(true)
                .setOngoing(true) // Make the notification ongoing
                .addAction(stopAction) // Add the Stop Ringtone button to the notification
                .build();

        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
