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
import com.bitspilani.classmanager.util.Faculty;
import com.bitspilani.classmanager.util.Time;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "HomeActivity";
    private SharedPreferences userData;
    private TextView textViewPickupTime, textViewReturnTime;
    private TextView textViewSessionStart, textViewSessionEnd;
    Faculty faculty;
    Button submit;
    FirebaseDatabase database;
    DatabaseReference facultyRef;
    DatabaseReference me;
    TimePickerDialog timeChanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar adminToolbar = (Toolbar) findViewById(R.id.admin_toolbar);
        setSupportActionBar(adminToolbar);

        initVariables();

        initializeScreen();
    }

    private void initVariables() {
        userData = getSharedPreferences(AppConstants.USER_DATA, MODE_PRIVATE);
        String id = userData.getString(AppConstants.ID, "");
        if( id.isEmpty() ) {
            gotoLoginActivity();
        }
        Log.d(TAG, "onCreate: ID: " +id);
        database = FirebaseDatabase.getInstance();
        facultyRef = database.getReference("faculty");
        me = facultyRef.child(id);
        facultyRef.child(id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String firstName = dataSnapshot.child("firstName").toString();
                String lastName = dataSnapshot.child("lastName").getValue().toString();
                String phNo = dataSnapshot.child("phNo").getValue().toString();
                String pickupTime = dataSnapshot.child("pickupTime").getValue().toString();
                String returnTime = dataSnapshot.child("returnTime").getValue().toString();
                String sessionStart = dataSnapshot.child("sessionEnd").getValue().toString();
                String sessionEnd = dataSnapshot.child("sessionStart").getValue().toString();

                faculty.setFirstName(firstName);
                faculty.setLastName(lastName);
                faculty.setPhNo(phNo);
                faculty.setPickupTime(getTime(pickupTime));
                faculty.setReturnTime(getTime(returnTime));
                faculty.setSessionStart(getTime(sessionStart));
                faculty.setSessionEnd(getTime(sessionEnd));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        faculty = new Faculty(userData.getString(AppConstants.EMAIL, ""));
    }

    private void initializeScreen() {
        textViewPickupTime = (TextView) findViewById(R.id.pickup_time);
        textViewReturnTime = (TextView) findViewById(R.id.return_time);

        textViewSessionStart = (TextView) findViewById(R.id.session_start);
        textViewSessionEnd = (TextView) findViewById(R.id.session_end);

        submit = (Button) findViewById(R.id.submit_request);

        database = FirebaseDatabase.getInstance();
        if(database != null) {
            String id = getSharedPreferences(AppConstants.USER_DATA, MODE_PRIVATE).getString(AppConstants.ID, "");
            facultyRef = database.getReference("faculty");

            facultyRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onDataChange: " +dataSnapshot.getValue());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        setTime(textViewPickupTime, 23, 59);
        Time time = getTime(textViewPickupTime.getText().toString());
        Log.d(TAG, "Time: " +time.getHour() +":" +time.getMinute());




        if (submit != null) {
            submit.setOnClickListener(this);
        }

        textViewReturnTime.setOnClickListener(this);
        textViewPickupTime.setOnClickListener(this);

        if (textViewSessionStart != null) {
            textViewSessionStart.setOnClickListener(this);
        }
        if (textViewSessionEnd != null) {
            textViewSessionEnd.setOnClickListener(this);
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

            case R.id.cab_pickup:
                timeChanger = new TimePickerDialog(getCurrentActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        setPickupTime(hourOfDay, minute);
                    }
                }, hour, minute, false);
                timeChanger.setCancelable(true);
                timeChanger.show();
                break;

            case R.id.cab_drop:
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
                facultyRef.updateChildren(faculty.getMap());
                break;
                /*
                Intent admin = new Intent(HomeActivity.this, AdminActivity.class);
                startActivity(admin);
                finish();
                */
        }
    }

    private Activity getCurrentActivity() {
        return this;
    }

    private void setSessionStartTime(int hourOfDay, int minute) {
        faculty.setSessionStart(new Time(hourOfDay, minute));
        textViewPickupTime.setText(hourOfDay +":" + minute);
    }

    private void setSessionEndTime(int hourOfDay, int minute) {
        faculty.setSessionEnd(new Time(hourOfDay, minute));
        textViewPickupTime.setText(hourOfDay +":" + minute);
    }

    private void setPickupTime(int hourOfDay, int minute) {
        faculty.setPickupTime(new Time(hourOfDay, minute));
        textViewPickupTime.setText(hourOfDay +":" + minute);
    }

    private void setReturnTime(int hourOfDay, int minute) {
        faculty.setReturnTime(new Time(hourOfDay, minute));
        textViewReturnTime.setText(hourOfDay +":" + minute);
    }

    private void setTime(TextView view, int hourOfDay, int minute) {
        int iHour = hourOfDay % 12;
        if(iHour == 0) {
            iHour = 12;
        }
        String a = hourOfDay / 12 == 0 ? "AM" : "PM";
        String time = String.format(Locale.ENGLISH, "%02d:%02d %s", iHour, minute, a);

        view.setText(time);
    }

    private Time getTime(String time) {
        String pattern = "([0][1-9]|[1][0-2]):([0-5][0-9]) (AM|PM)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(time);
        m.matches();
        String hour = m.group(1);
        String minute = m.group(2);
        String a = m.group(3);

        Log.d(TAG, "getTime: " +hour);
        int iHour = Integer.parseInt(hour);
        Log.d(TAG, "getTime: " +minute);
        int iMinute = Integer.parseInt(minute);
        iHour += a.equals("PM") ? 12 : 0;

        return new Time(iHour, iMinute);
    }
}
