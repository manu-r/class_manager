package com.bitspilani.admin.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bitspilani.admin.R;
import com.bitspilani.admin.adapters.UserListAdapter;
import com.bitspilani.admin.util.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserListFragment extends Fragment implements ValueEventListener, View.OnClickListener {
    private OnFragmentInteractionListener mListener;
    private ArrayList<User> users;
    FirebaseDatabase firebaseDB;
    DatabaseReference dbRef;
    UserListAdapter userListAdapter;

    public UserListFragment() {

    }

    public static UserListFragment newInstance() {
        return new UserListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        users = new ArrayList<>();
        firebaseDB = FirebaseDatabase.getInstance();
        dbRef = firebaseDB.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_userlist);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        userListAdapter = new UserListAdapter(users);
        recyclerView.setAdapter(userListAdapter);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_add_user);
        fab.setOnClickListener(this);

        dbRef.child("auth_ids").addValueEventListener(this);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
        users.clear();
        for (DataSnapshot child : children) {
            String userId = child.getKey();
            String email = child.child("email").getValue().toString();

            if (child.child("userType").getValue().toString().equals("faculty")) {
                User user = new User(userId, email);
                users.add(user);
            }
        }
        userListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fab_add_user:
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.add_user);
                dialog.setCancelable(true);
                dialog.setTitle(R.string.add_user);

                final Button btnAddUser = (Button) dialog.findViewById(R.id.btn_add_user);
                btnAddUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText editTextUsername = (EditText) dialog.findViewById(R.id.new_username);
                        EditText editTextEmail = (EditText) dialog.findViewById(R.id.new_user_email);

                        if (editTextEmail.getText().toString().isEmpty() ||
                                editTextUsername.getText().toString().isEmpty()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Enter a username and valid email");
                        } else {
                            String email = editTextEmail.getText().toString();
                            String username = editTextUsername.getText().toString();
                            Map<String, Object> user = new HashMap<>();
                            if (dbRef.child("auth_ids").child(username) != null) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("Username already exists. Please enter a new one.");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                builder.setCancelable(false);
                                builder.show();
                            }
                            user.put("email", email);
                            user.put("userType", "faculty");
                            dbRef.child("auth_ids").child(username).updateChildren(user, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        Toast.makeText(getActivity(), "User added.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            dialog.cancel();
                        }
                    }
                });
                dialog.show();
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }
}
