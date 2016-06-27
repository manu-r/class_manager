package com.bitspilani.admin.activity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bitspilani.admin.R;
import com.bitspilani.admin.util.AppConstants;
import com.bitspilani.admin.util.AppUtil;
import com.bitspilani.admin.util.Faculty;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, OnCompleteListener<Void>, ValueEventListener {
    private static final String TAG = "HomeActivity";
    private SharedPreferences userData;
    private EditText editTextPickupTime,
            editTextReturnTime,
            editTextSessionStart,
            editTextSessionEnd,
            editTextPickupLocation,
            editTextDropLocation;
    Faculty faculty;
    Button submit;
    FirebaseDatabase database;
    DatabaseReference dbReference,
            me;
    TimePickerDialog timeChanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar adminToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(adminToolbar);

        initVariables();

        initializeScreen();
    }

    private void initVariables() {
        userData = getSharedPreferences(AppConstants.USER_DATA, MODE_PRIVATE);
        this.faculty = new Faculty();
        String id = userData.getString(AppConstants.ID, "");
        if( id.isEmpty() ) {
            gotoLoginActivity();
        }
        Log.d(TAG, "onCreate: ID: " +id);
        database = FirebaseDatabase.getInstance();
        dbReference = database.getReference();
        me = dbReference.child("faculty").child(id);
        me.addListenerForSingleValueEvent(this);
    }

    private void initializeScreen() {
        //Pickup Details
        editTextPickupTime = (EditText) findViewById(R.id.cab_pickup_time);
        editTextPickupLocation = (EditText) findViewById(R.id.cab_pickup_location);

        //Drop Details
        editTextReturnTime = (EditText) findViewById(R.id.cab_return_time);
        editTextDropLocation = (EditText) findViewById(R.id.cab_drop_location);

        //Session Details
        editTextSessionStart = (EditText) findViewById(R.id.session_start);
        editTextSessionEnd = (EditText) findViewById(R.id.session_end);

        //Submit Button
        submit = (Button) findViewById(R.id.submit_request);

        if (submit != null) {
            submit.setOnClickListener(this);
        }

        editTextPickupTime.setInputType(InputType.TYPE_NULL);

        editTextReturnTime.setInputType(InputType.TYPE_NULL);

        editTextSessionStart.setInputType(InputType.TYPE_NULL);

        editTextSessionEnd.setInputType(InputType.TYPE_NULL);

        editTextPickupTime.setOnClickListener(this);
        editTextReturnTime.setOnClickListener(this);
        editTextSessionEnd.setOnClickListener(this);
        editTextSessionStart.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.faculty_menu, menu);
        return true;
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
            case R.id.session_start:
                timeChanger = new TimePickerDialog(getCurrentActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        setSessionStartTime(hourOfDay, minute);
                    }
                }, hour, minute, false);
                timeChanger.setCancelable(true);
                timeChanger.show();
                break;

            case R.id.session_end:
                timeChanger = new TimePickerDialog(getCurrentActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        setSessionEndTime(hourOfDay, minute);
                    }
                }, hour, minute, false);
                timeChanger.setCancelable(true);
                timeChanger.show();
                break;

            case R.id.cab_pickup_time:
                timeChanger = new TimePickerDialog(getCurrentActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        setPickupTime(hourOfDay, minute);
                    }
                }, hour, minute, false);
                timeChanger.setCancelable(true);
                timeChanger.show();
                break;

            case R.id.cab_return_time:
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
                faculty.setPickupLocation(editTextPickupLocation.getText().toString());
                faculty.setDropLocation(editTextDropLocation.getText().toString());
                me.updateChildren(this.faculty.getMap()).addOnCompleteListener(this);
                break;
        }
    }

    private Activity getCurrentActivity() {
        return this;
    }

    private void setSessionStartTime(int hourOfDay, int minute) {
        String time = AppUtil.formatTime(hourOfDay, minute);
        this.faculty.setSessionStart(time);
        editTextSessionStart.setText(time);
    }

    private void setSessionEndTime(int hourOfDay, int minute) {
        String time = AppUtil.formatTime(hourOfDay, minute);
        faculty.setSessionEnd(time);
        editTextSessionEnd.setText(time);
    }

    private void setPickupTime(int hourOfDay, int minute) {
        String time = AppUtil.formatTime(hourOfDay, minute);
        faculty.setPickupTime(time);
        editTextPickupTime.setText(time);
    }

    private void setReturnTime(int hourOfDay, int minute) {
        String time = AppUtil.formatTime(hourOfDay, minute);
        faculty.setReturnTime(time);
        editTextReturnTime.setText(time);
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Faculty temp = dataSnapshot.getValue(Faculty.class);
        if(temp != null ) {
            this.faculty = temp;
            updateUI();
        }
    }

    private void updateUI() {
        editTextSessionStart.setText(faculty.getSessionStart().toString());
        editTextSessionEnd.setText(faculty.getSessionEnd().toString());
        editTextPickupLocation.setText(faculty.getPickupLocation());
        editTextReturnTime.setText(faculty.getReturnTime().toString());
        editTextPickupTime.setText(faculty.getPickupTime().toString());
        editTextDropLocation.setText(faculty.getDropLocation());
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Toast.makeText(getCurrentActivity(),
                "Cannot retrieve your data from the server (Showing cached data).",
                Toast.LENGTH_LONG).show();
    }
}
