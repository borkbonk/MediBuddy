package com.sp.a0miniproj;

import android.os.Bundle;

import com.sp.a0miniproj.databinding.ActivityAlarmBinding;

public class Alarm extends drawerbase {
    ActivityAlarmBinding alarmBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alarmBinding=ActivityAlarmBinding.inflate(getLayoutInflater());
        setContentView(alarmBinding.getRoot());
    }
}