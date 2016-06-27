package com.bitspilani.admin.adapters;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bitspilani.admin.R;
import com.bitspilani.admin.util.User;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by I311849 on 16/Jun/2016.
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    private ArrayList<User> users;

    public UserListAdapter(ArrayList<User> users) {
        this.users = users;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View user = LayoutInflater.from(parent.getContext()).inflate(R.layout.user, parent, false);

        return new ViewHolder(user);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        TextView userId = (TextView) holder.view.findViewById(R.id.userid);
        TextView userEmail = (TextView) holder.view.findViewById(R.id.user_email);
        userId.setText(users.get(position).getUserId());
        userEmail.setText(users.get(position).getEmail());

        ImageButton removeUser = (ImageButton) holder.view.findViewById(R.id.remove_user);
        removeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final User user = users.get(holder.getAdapterPosition());
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogBuilder.setMessage(String.format(Locale.ENGLISH,
                        "Are you sure you want to remove %s", user.getUserId()));
                alertDialogBuilder.setTitle("Confirm Remove User");
                alertDialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference authIds = database.getReference("auth_ids");
                        authIds.child(user.getUserId()).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError == null) {
                                    Toast.makeText(v.getContext(), "User Removed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public ViewHolder(View itemView) {

            super(itemView);
            this.view = itemView;
        }
    }
}
