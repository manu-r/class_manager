package com.bitspilani.admin.activity;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bitspilani.admin.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener, UserListFragment.OnFragmentInteractionListener, FacultyDetailsFragment.OnFragmentInteractionListener {
    private static final String TAG = "AdminActivity";
    FragmentManager fragmentManager;
    FacultyDetailsFragment  facultyDetailsFragment;

    DatabaseReference authRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        initVariables();
        initUI();
        facultyDetailsFragment = new FacultyDetailsFragment();
        fragmentManager.beginTransaction()
                .add(R.id.admin_screen, facultyDetailsFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    private void initUI() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.admin_menu);
    }

    private void initVariables() {
        fragmentManager = getSupportFragmentManager();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        authRef = db.getReference("auth_ids");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.list_users:
                Log.d(TAG, "onOptionsItemSelected: Clicked list_users");
                Fragment userListFragment = new UserListFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.admin_screen, userListFragment)
                        .addToBackStack("userListFragment")
                        .commit();
                return true;
            case R.id.user_search_bar:
                Log.d(TAG, "onOptionsItemSelected: Clicked search");
                return true;
            case R.id.action_settings:
                Log.d(TAG, "onOptionsItemSelected: Clicked settings");
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onClick(View v) {
    }

    private Context getCurrentActivity() {
        return this;
    }

    @Override
    public void onFragmentInteraction() {

    }
}
