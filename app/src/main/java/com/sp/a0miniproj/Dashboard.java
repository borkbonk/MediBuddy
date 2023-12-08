package com.sp.a0miniproj;


import android.os.Bundle;

import com.sp.a0miniproj.databinding.ActivityDashboardBinding;
import com.sp.a0miniproj.drawerbase;

public class Dashboard extends drawerbase {
    ActivityDashboardBinding dashboardBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashboardBinding=dashboardBinding.inflate(getLayoutInflater());
        allocatedActivityTitle("Dashboard");
        setContentView(dashboardBinding.getRoot());
    }
}