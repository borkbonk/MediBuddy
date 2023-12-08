package com.sp.a0miniproj;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sp.a0miniproj.databinding.ActivitySettingsBinding;
import com.sp.a0miniproj.notes.LoginActivity;
import com.sp.a0miniproj.reminder.ReminderDatabaseHelper;

public class settings extends drawerbase {

    private static final int PERMISSION_CALL_PHONE = 1;
    private static final String KEY_EMERGENCY_NUMBER = "emergency_number";

    private EditText editTextEmergencyNumber;
    private String emergencyNumber;
    ActivitySettingsBinding activitySettingsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySettingsBinding = ActivitySettingsBinding.inflate(getLayoutInflater());

        setContentView(activitySettingsBinding.getRoot());
        allocatedtitle("Settings");

        FirebaseApp.initializeApp(this); // Initialize Firebase if not already done
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        editTextEmergencyNumber = findViewById(R.id.editTextEmergencyNumber);

        Button btnSetEmergencyNumber = findViewById(R.id.btnSetEmergencyNumber);
        btnSetEmergencyNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEmergencyNumber();
            }
        });
      Button cleardatabase=findViewById(R.id.btnClearDatabase);
        cleardatabase.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showClearDatabaseConfirmationDialog();
            }
        });

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out the user
                showLogoutConfirmationDialog();


                // Finish the current activity to remove it from the back stack
            }
        });}
    private void showClearDatabaseConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Database Clear");
        builder.setMessage("Are you sure you want to clear the database?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearDatabase();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Logout");
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Sign out the user
                FirebaseAuth.getInstance().signOut();

                // Clear the activity stack and start LoginActivity
                Intent intent = new Intent(settings.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                // Finish the current activity to remove it from the back stack
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

        private void setEmergencyNumber() {
        emergencyNumber = editTextEmergencyNumber.getText().toString().trim();
        if (emergencyNumber.isEmpty()) {
            Toast.makeText(this, "Please enter a valid emergency phone number.", Toast.LENGTH_SHORT).show();
        } else {
            // Save the emergency number to SharedPreferences
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_EMERGENCY_NUMBER, emergencyNumber);
            editor.apply();
            Toast.makeText(this, "Emergency phone number set.", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

          if (id == R.id.dial) {
            initiateQuickDial();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initiateQuickDial() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String storedEmergencyNumber = preferences.getString(KEY_EMERGENCY_NUMBER, "");

        if (storedEmergencyNumber.isEmpty()) {
            Toast.makeText(this, "Please set the emergency phone number first.", Toast.LENGTH_SHORT).show();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                dialEmergencyNumber(storedEmergencyNumber);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CALL_PHONE);
            }
        }
    }


    private void dialEmergencyNumber(String phoneNumber) {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(dialIntent);
    }

    public void clearDatabase() {
        ReminderDatabaseHelper dbHelper = new ReminderDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM reminders"); // Replace "my_table" with your table name
        db.close();
        Toast.makeText(this, "Database cleared", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, initiate the call
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                String storedEmergencyNumber = preferences.getString(KEY_EMERGENCY_NUMBER, "");
                if (!storedEmergencyNumber.isEmpty()) {
                    dialEmergencyNumber(storedEmergencyNumber);
                }
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(this, "Permission to call denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
