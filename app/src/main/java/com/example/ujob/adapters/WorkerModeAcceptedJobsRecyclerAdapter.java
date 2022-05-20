package com.example.ujob.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ujob.R;
import com.example.ujob.models.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class WorkerModeAcceptedJobsRecyclerAdapter extends RecyclerView.Adapter<WorkerModeAcceptedJobsRecyclerAdapter.MyViewHolder> {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final ArrayList<User> usersList;
    private final OnJobListener mOnJobListener;

    // when you want to create an instance of the recycler adapter you need to pass it a list of users
    public WorkerModeAcceptedJobsRecyclerAdapter(ArrayList<User> usersList, OnJobListener onJobListener) {
        this.usersList = usersList;
        this.mOnJobListener = onJobListener;
    }

    // find Ids from "recycler_view_potential_workers_list"
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView nameTxt;
        private final ImageView workerProfilePicture;
        private final TextView workerYearsOfExperince;
        private final TextView yearsOfExperienceText;
        OnJobListener onJobListener;

        public MyViewHolder(final View view, OnJobListener onJobListener) {
            super(view);
            nameTxt = view.findViewById(R.id.potential_worker_placeholder);
            workerProfilePicture = view.findViewById(R.id.worker_profile_picture);
            Button acceptWorkerButton = view.findViewById(R.id.accept_potential_worker_button);
            this.onJobListener = onJobListener;
            acceptWorkerButton.setOnClickListener(this);
            workerYearsOfExperince = view.findViewById(R.id.potential_worker_years_of_experience_number);
            yearsOfExperienceText = view.findViewById(R.id.potential_worker_years_of_experience_text);
        }

        @Override
        public void onClick(View view) {
            // onJobListener.onJobClick(getAdapterPosition()); // getAdapterPosition() is depreciated
            onJobListener.onJobClick(getAbsoluteAdapterPosition());
        }
    }

    @NonNull
    @Override
    public WorkerModeAcceptedJobsRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_potential_workers_list, parent, false);
        return new MyViewHolder(itemView, mOnJobListener);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkerModeAcceptedJobsRecyclerAdapter.MyViewHolder holder, int position) {
        String firstName = usersList.get(position).getFirstName();
        String lastName = usersList.get(position).getLastName().substring(0,1);
        String fullName = firstName + " " + lastName.toUpperCase();
        String profilePictureURL = usersList.get(position).getProfilePicture();
        String userId = usersList.get(position).getUserId();
        
        setJobName(userId ,holder);
        
        // String yearsOfExperience = usersList.get(position).get == null ? "N/A" : usersList.get(position).getYearsOfExperience();
        String jobText = "Job :";
        Picasso.get().load(profilePictureURL).into(holder.workerProfilePicture);
        holder.nameTxt.setText(fullName);
        holder.yearsOfExperienceText.setText(jobText);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public interface OnJobListener {
        void onJobClick(int position);
    }

    private void setJobName (String userID, MyViewHolder holder){
        DocumentReference docRef = db.collection("jobs").document(userID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("jobInfo", "DocumentSnapshot data: " + document.getData());
                    String jobName = (String) Objects.requireNonNull(document.getData()).get("jobName");
                    holder.workerYearsOfExperince.setText(jobName);
                } else {
                    Log.d("jobInfo", "No such document");
                }
            } else {
                Log.d("jobInfo", "get failed with ", task.getException());
            }
        });
    }
}
