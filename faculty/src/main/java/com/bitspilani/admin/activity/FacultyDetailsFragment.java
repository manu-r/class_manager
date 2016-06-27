package com.bitspilani.admin.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitspilani.admin.R;
import com.bitspilani.admin.adapters.FacultyAdapter;
import com.bitspilani.admin.util.ExcelHelper;
import com.bitspilani.admin.util.Faculty;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jxl.write.WriteException;

public class FacultyDetailsFragment extends Fragment implements ValueEventListener, View.OnClickListener {
    private static final String TAG = "FacultyDetailsFragment";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL = 10;
    private OnFragmentInteractionListener mListener;
    FirebaseDatabase firebaseDB;
    DatabaseReference dbRef;
    private ArrayList<Faculty> faculties;
    RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    ProgressDialog progressDialog;


    public FacultyDetailsFragment() {
        // Required empty public constructor
    }

    public static FacultyDetailsFragment newInstance() {
        return new FacultyDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        firebaseDB = FirebaseDatabase.getInstance();
        dbRef = firebaseDB.getReference();
        dbRef.child("faculty").addValueEventListener(this);
        faculties = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_faculty_details, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.list_faculties);
        recyclerView.setClickable(true);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_export_excel);
        fab.setOnClickListener(this);

        adapter = new FacultyAdapter(faculties);

        if (recyclerView != null) {
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if(!progressDialog.isShowing()) {
            progressDialog.show();
        }
        this.faculties.clear();
        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
        for(DataSnapshot child: children) {
            Faculty faculty = child.getValue(Faculty.class);
            this.faculties.add(faculty);
        }
        if (progressDialog.isShowing()) {
            progressDialog.cancel();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_export_excel:
                Log.d(TAG, "onClick: I came here");
                File file = Environment.getExternalStorageDirectory();
                if(!file.canWrite()) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL);
                }
                file = new File(file.getPath() +File.separator +"session_details.xlsx");
                if(!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                ExcelHelper excelHelper = new ExcelHelper();
                excelHelper.setOutputFile(file);
                try {
                    excelHelper.write(faculties);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }
}