package com.sp.a0miniproj.reminder;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sp.a0miniproj.R;
import com.sp.a0miniproj.drawerbase;

import java.util.Calendar;

public class EditReminderActivity extends drawerbase {

    private long reminderId;
    private ReminderDatabaseHelper databaseHelper;
    private EditText editTextTitle, editTextDescription;
    private Button buttonSetEditDateTime;
    private Calendar calendar;
    public static final int ADD_REMINDER_REQUEST_CODE = 1;
    public static final int EDIT_REMINDER_REQUEST_CODE = 2;
    public static final String EXTRA_REMINDER_DELETED = "extra_reminder_deleted";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reminder);
            editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextEditDescription);
        buttonSetEditDateTime = findViewById(R.id.buttonSetEditDateTime);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Reminder");

        databaseHelper = new ReminderDatabaseHelper(this);

        if (getIntent().hasExtra("reminderId")) {
            reminderId = getIntent().getLongExtra("reminderId", -1);

            if (reminderId == -1) {
                // Handle invalid reminderId, maybe show an error message and finish the
                // activity
                finish();
                return;
            }
        } else {
            // Handle missing reminderId, maybe show an error message and finish the activity
            finish();
            return;
        }

        Reminderid reminder = databaseHelper.getReminderById(reminderId);

        if (reminder == null) {
            // Handle non-existent reminder with the given ID, maybe show an error message and
            // finish the activity
            finish();
            return;
        }

        editTextTitle.setText(reminder.getTitle());
        editTextDescription.setText(reminder.getDescription());

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(reminder.getTimestamp());

        buttonSetEditDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });

        Button buttonSaveEditReminder = findViewById(R.id.buttonSaveEditReminder);
        buttonSaveEditReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReminderChanges();

            }
        });

        Button buttonDeleteReminder = findViewById(R.id.buttonDeleteReminder);
        buttonDeleteReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteReminder();
            }
        });
    }

    private void showDateTimePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                showTimePicker();
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show();
    }

    private void saveReminderChanges() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (!title.isEmpty() && !description.isEmpty()) {
            long newTimestamp = calendar.getTimeInMillis(); // Get the new timestamp

            // Update the existing reminder with the new data
            Reminderid updatedReminder = new Reminderid(title, description, newTimestamp);
            updatedReminder.setId(reminderId);
            databaseHelper.updateReminder(updatedReminder);

            // Notify MainActivity to update the list
            Intent updateListIntent = new Intent(Reminder.ACTION_UPDATE_REMINDER_LIST);
            sendBroadcast(updateListIntent);

            // Cancel the old alarm and schedule a new alarm with the updated timestamp
            cancelAlarm();
            scheduleAlarm(updatedReminder);

            // Handle other actions after saving the changes, e.g., show a toast message
            Toast.makeText(this, "Reminder updated", Toast.LENGTH_SHORT).show();

            finish(); // Close the activity after saving
        }
    }




    private void deleteReminder() {
        cancelAlarm();
        databaseHelper.deleteReminder(reminderId);

        // Send back the result to the MainActivity to update the RecyclerView
        Intent intent = new Intent();
        intent.putExtra(EXTRA_REMINDER_DELETED, true);
        setResult(RESULT_OK, intent);
        // Handle other actions after deleting the reminder, e.g., show a toast message

        finish(); // Close the activity after deletion
    }
    private void scheduleAlarm(Reminderid reminder) {
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("title", reminder.getTitle());
        alarmIntent.putExtra("description", reminder.getDescription());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) reminder.getId(),
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminder.getTimestamp(), pendingIntent);
        }
    }
    private void cancelAlarm() {
        Intent alarmIntent = new Intent(this, AlarmReceiver.class); // Change AlarmReceiver.class to the actual class that handles your alarms
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) reminderId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
