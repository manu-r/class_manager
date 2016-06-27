package com.bitspilani.admin.adapters;

import android.app.Dialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bitspilani.admin.R;
import com.bitspilani.admin.util.Faculty;

import java.util.ArrayList;

import static android.view.View.*;

/**
 * Created by I311849 on 14/Jun/2016.
 */
public class FacultyAdapter extends RecyclerView.Adapter<FacultyAdapter.ViewHolder> {
    private ArrayList<Faculty> faculties;

    public FacultyAdapter(ArrayList<Faculty> faculties) {
        this.faculties = faculties;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View facultyDetails = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.faculty_list_item, parent, false);

        return new ViewHolder(facultyDetails);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(holder, this.faculties.get(position));
    }

    @Override
    public int getItemCount() {
        return this.faculties.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public View view;
        Faculty faculty;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            this.view = v;
        }

        public void setData(ViewHolder holder, Faculty faculty) {
            this.faculty = faculty;
            TextView facultyName = (TextView) holder.view.findViewById(R.id.faculty_name);
            facultyName.setText(faculty.getFirstName());
        }

        @Override
        public void onClick(View v) {
            Dialog dialog = new Dialog(v.getContext());
            dialog.setContentView(R.layout.faculty_details);

            TextView facultyName = (TextView) dialog.findViewById(R.id.faculty_name);
            TextView facultyPhNo = (TextView) dialog.findViewById(R.id.contact_number);
            TextView sessionStart = (TextView) dialog.findViewById(R.id.session_start);
            TextView sessionEnd = (TextView) dialog.findViewById(R.id.session_end);
            TextView pickupTime = (TextView) dialog.findViewById(R.id.cab_pickup_time);
            TextView returnTime = (TextView) dialog.findViewById(R.id.cab_return_time);
            TextView pickupLoc = (TextView) dialog.findViewById(R.id.cab_pickup_location);
            TextView dropLoc = (TextView) dialog.findViewById(R.id.cab_drop_location);

            facultyName.setText(faculty.getFirstName());
            facultyPhNo.setText(faculty.getPhNo());
            sessionStart.setText(faculty.getSessionStart().toString());
            sessionEnd.setText(faculty.getSessionEnd().toString());
            pickupTime.setText(faculty.getPickupTime().toString());
            returnTime.setText(faculty.getReturnTime().toString());
            pickupLoc.setText(faculty.getPickupLocation());
            dropLoc.setText(faculty.getDropLocation());

            dialog.setCancelable(true);
            dialog.show();

        }
    }

}
