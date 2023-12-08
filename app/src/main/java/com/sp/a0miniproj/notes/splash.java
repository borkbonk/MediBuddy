package com.sp.a0miniproj.notes;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//import com.sp.a0miniproj.Dashboard;
import com.sp.a0miniproj.R;
import com.sp.a0miniproj.calendar.RemindersForDateActivity;
import com.sp.a0miniproj.reminder.Reminder;

public class splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if(currentUser==null){
                    startActivity(new Intent(splash.this, LoginActivity.class));
                }else{
                    startActivity(new Intent(splash.this, RemindersForDateActivity.class));
                }
                finish();
            }
        },1000);

    }
}