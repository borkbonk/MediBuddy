package com.sp.a0miniproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.sp.a0miniproj.calendar.RemindersForDateActivity;
import com.sp.a0miniproj.gpt.gpt;
import com.sp.a0miniproj.notes.LoginActivity;
import com.sp.a0miniproj.notes.Notes;
import com.sp.a0miniproj.reminder.Reminder;

public class drawerbase extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;  private static final int PERMISSION_CALL_PHONE = 1;
    private static final String KEY_EMERGENCY_NUMBER = "emergency_number";
    @Override
    public void setContentView(View view)
    {
        drawerLayout=(DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawerbase,null);
        FrameLayout container =drawerLayout.findViewById(R.id.activitycontainer);
        container.addView(view);
        super.setContentView(drawerLayout);
        Toolbar toolbar=drawerLayout.findViewById(R.id.toolbardrawerbase);
        setSupportActionBar(toolbar);
        NavigationView navigationView=drawerLayout.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.draweropen,R.string.drawerclose);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailsmenu, menu);
        return true;
    }
   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(drawerbase.this, LoginActivity.class));
            finish();
            return true;
        }else if(id==R.id.dial){
            initiateQuickDial();
            return true;
        }
        return false;

    }
    private void dialEmergencyNumber(String phoneNumber) {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(dialIntent);
    }

    protected void allocatedtitle(String titleString){
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(titleString);
        }
    }
    private void initiateQuickDial() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String storedEmergencyNumber = preferences.getString(KEY_EMERGENCY_NUMBER, "");

        if (storedEmergencyNumber.isEmpty()) {
            Toast.makeText(this, "Please set the emergency phone number first.", Toast.LENGTH_SHORT).show();
        } else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                dialEmergencyNumber(storedEmergencyNumber);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CALL_PHONE);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if(item.getItemId()==R.id.nav_alarm){
            startActivity(new Intent(this, Reminder.class));
            overridePendingTransition(0,0);
        }else if(item.getItemId()==R.id.nav_notes){
            startActivity(new Intent(this, Notes.class));
            overridePendingTransition(0,0);
        }else if(item.getItemId()==R.id.nav_calendar){
            startActivity(new Intent(this, RemindersForDateActivity.class));
            overridePendingTransition(0,0);

        }else if (item.getItemId()==R.id.nav_chat){
            startActivity(new Intent( this,gpt.class));
            overridePendingTransition(0,0);
        }else if(item.getItemId()==R.id.settings){
            startActivity(new Intent(this, settings  .class));
            overridePendingTransition(0,0);
        }



        return false;
    }
    protected void allocatedActivityTitle(String titlestring){
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(titlestring);
        }
    }
}