package com.bitspilani.classmanager.activity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bitspilani.classmanager.R;
import com.bitspilani.classmanager.util.AppConstants;
import com.bitspilani.classmanager.util.Time;

import java.util.Calendar;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "HomeActivity";
    private SharedPreferences userData;
    private TextView textViewPickupTime, textViewReturnTime;
    private TextView btnChangePickupTime, btnChangeReturnTime;
    private Time pickupTime;
    private Time returnTime;
    Button submit;
    TimePickerDialog timeChanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar adminToolbar = (Toolbar) findViewById(R.id.admin_toolbar);
        setSupportActionBar(adminToolbar);

        initializeScreen();
        userData = getSharedPreferences(AppConstants.USER_DATA, MODE_PRIVATE);

        Log.d(TAG, "Email (Shared Preference): " +userData.getString(AppConstants.EMAIL, ""));

        if( userData.getString(AppConstants.EMAIL, "").isEmpty() ) {
            gotoLoginActivity();
        }
    }

    private void initializeScreen() {
        textViewPickupTime = (TextView) findViewById(R.id.pickup_time);
        textViewReturnTime = (TextView) findViewById(R.id.return_time);

        btnChangePickupTime = (TextView) findViewById(R.id.change_pickup_time);
        btnChangeReturnTime = (TextView) findViewById(R.id.change_return_time);

        submit = (Button) findViewById(R.id.submit_request);

        if (submit != null) {
            submit.setOnClickListener(this);
        }

        if (btnChangePickupTime != null) {
            btnChangePickupTime.setOnClickListener(this);
        }
        if (btnChangeReturnTime != null) {
            btnChangeReturnTime.setOnClickListener(this);
        }
    }

    private void gotoLoginActivity() {
        Intent loginActivity = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(loginActivity);
        finish();
    }

    @Override
    public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        switch (v.getId()) {
            case R.id.change_pickup_time:
                timeChanger = new TimePickerDialog(getCurrentActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        setPickupTime(hourOfDay, minute);
                    }
                }, hour, minute, false);
                timeChanger.setCancelable(true);
                timeChanger.show();
                break;

            case R.id.change_return_time:
                timeChanger = new TimePickerDialog(getCurrentActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        setReturnTime(hourOfDay, minute);
                    }
                }, hour, minute, false);
                timeChanger.setCancelable(true);
                timeChanger.show();
                break;

            case R.id.submit_request:
                Intent admin = new Intent(HomeActivity.this, AdminActivity.class);
                startActivity(admin);
                finish();
        }
    }

    private Activity getCurrentActivity() {
        return this;
    }

    private void setPickupTime(int hourOfDay, int minute) {
        this.pickupTime = new Time(hourOfDay, minute);
        textViewPickupTime.setText(hourOfDay +":" + minute);
    }

    private void setReturnTime(int hourOfDay, int minute) {
        this.returnTime = new Time(hourOfDay, minute);
        textViewReturnTime.setText(hourOfDay +":" + minute);
    }
}
