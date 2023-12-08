package com.sp.a0miniproj.reminder;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sp.a0miniproj.calendar.RemindersForDateActivity;


import com.sp.a0miniproj.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import com.sp.a0miniproj.calendar.RemindersForDateActivity;
import com.sp.a0miniproj.databinding.ActivityReminderBinding;
import com.sp.a0miniproj.drawerbase;


public class Reminder extends drawerbase {

    ActivityReminderBinding activityReminderBinding;
    ReminderAdapter reminderAdapter;
    RecyclerView recyclerViewReminders;

    ReminderDatabaseHelper databaseHelper;
    EditText editTextTitle, editTextDescription;
    Button buttonSetDateTime;
    private Calendar calendar;
    private List<Reminderid> reminderList = new ArrayList<>();

    public static final int ADD_REMINDER_REQUEST_CODE = 1;
    public static final int EDIT_REMINDER_REQUEST_CODE = 2;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static final String ACTION_UPDATE_REMINDER_LIST = "com.sp.a0miniproj.reminder";

    private BroadcastReceiver updateReminderListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(ACTION_UPDATE_REMINDER_LIST)) {
                updateReminderList();
            }
        }
    };

    void setupRecyclerView(){

        recyclerViewReminders.setLayoutManager(new LinearLayoutManager(this));
        reminderAdapter = new ReminderAdapter(reminderList, this);
        recyclerViewReminders.setAdapter(reminderAdapter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityReminderBinding = ActivityReminderBinding.inflate(getLayoutInflater());
        setContentView(activityReminderBinding  .getRoot());
        calendar = Calendar.getInstance();
        allocatedtitle("Reminders");
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        recyclerViewReminders = findViewById(R.id.recyclerViewReminders);
        buttonSetDateTime = findViewById(R.id.buttonSetDateTime);

        databaseHelper = new ReminderDatabaseHelper(this);
        reminderList = databaseHelper.getAllReminders();

        recyclerViewReminders.setLayoutManager(new LinearLayoutManager(this));
        reminderAdapter = new ReminderAdapter(reminderList, this);
        recyclerViewReminders.setAdapter(reminderAdapter);
        reminderAdapter.setOnReminderClickListener(new ReminderAdapter.OnReminderClickListener() {
            @Override
            public void onReminderClick(long reminderId) {
                // Open the EditReminderActivity with the selected reminder's ID
                Intent intent = new Intent(Reminder.this, EditReminderActivity.class);
                intent.putExtra("reminderId", reminderId);
                startActivityForResult(intent, EditReminderActivity.EDIT_REMINDER_REQUEST_CODE);
            }
        });
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Trigger refresh action
                updateReminderList();
                swipeRefreshLayout.setRefreshing(false); // Stop refreshing animation
            }
        });


        buttonSetDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });

        Button buttonAddReminder = findViewById(R.id.buttonAddReminder);
        buttonAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReminder();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the broadcast receiver
        IntentFilter filter = new IntentFilter(ACTION_UPDATE_REMINDER_LIST);
        registerReceiver(updateReminderListReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the broadcast receiver to avoid memory leaks
        unregisterReceiver(updateReminderListReceiver);
    }

    private void showDateTimePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // When a date is selected, initialize the calendar object and show the time picker
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        showTimePicker();
                    }
                },
                year,
                month,
                dayOfMonth
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

    private void addReminder() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (title.isEmpty() && description.isEmpty()) {
            Toast.makeText(this, "Please fill in the title", Toast.LENGTH_SHORT).show();
            return;
        }

        if (description.isEmpty()) {
            Toast.makeText(this, "Please fill in the Description", Toast.LENGTH_SHORT).show();
            return;
        }
        if(title.isEmpty()&&calendar==null){
            Toast.makeText(this,"Please fill in the Title and the Date and time",Toast.LENGTH_SHORT).show();
        }
        if(description.isEmpty()&&calendar==null){
            Toast.makeText(this,"Please fill in the Description and the Date and Time",Toast.LENGTH_SHORT).show();
        }

        long timestamp = calendar.getTimeInMillis();

        Reminderid reminder = new Reminderid(title, description, timestamp);
        long id = databaseHelper.insertReminder(reminder);

        if (id != -1) {
            reminder.setId(id);
            reminderList.add(0, reminder);
            reminderAdapter.notifyItemInserted(0);

            editTextTitle.getText().clear();
            editTextDescription.getText().clear();

            Toast.makeText(this, "Reminder added successfully", Toast.LENGTH_SHORT).show();
        }

        scheduleAlarm(reminder);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == EditReminderActivity.ADD_REMINDER_REQUEST_CODE || requestCode == EditReminderActivity.EDIT_REMINDER_REQUEST_CODE) {
                // Check if the reminder was deleted in EditReminderActivity
                boolean isReminderDeleted = data.getBooleanExtra(EditReminderActivity.EXTRA_REMINDER_DELETED, false);
                if (isReminderDeleted) {
                    // Reminder was deleted, so update the reminder list
                    updateReminderList();
                }
            }
        }
    }

    private void updateReminderList() {
        reminderList.clear();
        reminderList.addAll(databaseHelper.getAllReminders());
        reminderAdapter.notifyDataSetChanged();
    }
}
