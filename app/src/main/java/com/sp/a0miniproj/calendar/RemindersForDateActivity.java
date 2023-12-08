package com.sp.a0miniproj.calendar;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.sp.a0miniproj.R;
import com.sp.a0miniproj.databinding.ActivityRemindersForDateBinding;
import com.sp.a0miniproj.drawerbase;
import com.sp.a0miniproj.reminder.ReminderAdapter;
import com.sp.a0miniproj.reminder.ReminderDatabaseHelper;
import com.sp.a0miniproj.reminder.Reminderid;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RemindersForDateActivity extends drawerbase {
    private CalendarView calendarView;
    private RecyclerView recyclerViewRemindersForDate;
    private ReminderAdapter reminderAdapter;
    private List<Reminderid> remindersForDateList;
    private ReminderDatabaseHelper databaseHelper;
    ActivityRemindersForDateBinding activityRemindersForDateBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRemindersForDateBinding= ActivityRemindersForDateBinding.inflate(getLayoutInflater());

        setContentView(activityRemindersForDateBinding.getRoot());
        allocatedtitle("Calendar");

        calendarView = findViewById(R.id.calendarView);
        recyclerViewRemindersForDate = findViewById(R.id.recyclerViewRemindersForDate);
        recyclerViewRemindersForDate.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new ReminderDatabaseHelper(this);
        remindersForDateList = new ArrayList<>();
        reminderAdapter = new ReminderAdapter(remindersForDateList, this);
        recyclerViewRemindersForDate.setAdapter(reminderAdapter);
        Calendar currentCalendar = Calendar.getInstance();
        long currentTimestamp = currentCalendar.getTimeInMillis();
        showRemindersForSelectedDate(currentTimestamp);
        // Set up the calendar click listener
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar selectedCalendar = eventDay.getCalendar();
                long selectedDateTimestamp = selectedCalendar.getTimeInMillis();
                showRemindersForSelectedDate(selectedDateTimestamp);

            }
        });
    }

    private void showRemindersForSelectedDate(long selectedDateTimestamp) {
        remindersForDateList.clear();

        // Get the selected date in formatted form (e.g., 01 Aug 2023)
        String selectedDate = formatDate(selectedDateTimestamp);

        // Get all reminders from the database
        List<Reminderid> allReminders = databaseHelper.getAllReminders();

        // Filter reminders based on the selected date
        for (Reminderid reminder : allReminders) {
            if (isSameDate(reminder.getTimestamp(), selectedDateTimestamp)) {
                remindersForDateList.add(reminder);
            }
        }

        // Notify the adapter about the data change
        reminderAdapter.notifyDataSetChanged();

        // Display a message if there are no reminders for the selected date
        if (remindersForDateList.isEmpty()) {
            Toast.makeText(this, "No reminders for " + selectedDate, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isSameDate(long timestamp1, long timestamp2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(timestamp1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(timestamp2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}
