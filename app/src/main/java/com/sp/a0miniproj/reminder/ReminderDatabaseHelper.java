package com.sp.a0miniproj.reminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ReminderDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "reminders.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "reminders";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    private static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TITLE + " TEXT NOT NULL, " +
            COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
            COLUMN_TIMESTAMP + " INTEGER NOT NULL);";

    public ReminderDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertReminder(Reminderid reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, reminder.getTitle());
        values.put(COLUMN_DESCRIPTION, reminder.getDescription());
        values.put(COLUMN_TIMESTAMP, reminder.getTimestamp());
        return db.insert(TABLE_NAME, null, values);
    }

    public List<Reminderid> getAllReminders() {
        List<Reminderid> reminders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COLUMN_TIMESTAMP + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Reminderid reminder = new Reminderid();
                reminder.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                reminder.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                reminder.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                reminder.setTimestamp(cursor.getLong(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
                reminders.add(reminder);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return reminders;
    }
    public Reminderid getReminderById(long reminderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_ID + "=?", new String[]{String.valueOf(reminderId)}, null, null, null);
        Reminderid reminder = null;
        if (cursor != null && cursor.moveToFirst()) {
            reminder = new Reminderid();
            reminder.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
            reminder.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            reminder.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
            reminder.setTimestamp(cursor.getLong(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
            cursor.close();
        }
        return reminder;
    }

    public int updateReminder(Reminderid reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, reminder.getTitle());
        values.put(COLUMN_DESCRIPTION, reminder.getDescription());
        values.put(COLUMN_TIMESTAMP, reminder.getTimestamp());
        return db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(reminder.getId())});
    }

    public void deleteReminder(long reminderId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(reminderId)});
    }
}
