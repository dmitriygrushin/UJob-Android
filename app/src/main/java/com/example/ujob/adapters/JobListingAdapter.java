package com.example.ujob.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ujob.R;
import com.example.ujob.models.JobListingModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class JobListingAdapter extends RecyclerView.Adapter<JobListingAdapter.MyViewHolder>{
    Context context;
    ArrayList<JobListingModel> jobListingModels;
    private final OnJobListener mOnJobListener;
    public JobListingAdapter(Context context, ArrayList<JobListingModel> jobListingModels, OnJobListener onJobListener) {
        this.context = context;
        this.jobListingModels = jobListingModels;
        this.mOnJobListener = onJobListener;
    }

    @NonNull
    @Override
    public JobListingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflates layout and gives look to each row
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_listing,parent,false);
        return new JobListingAdapter.MyViewHolder(view, mOnJobListener);
    }

    @Override
    public void onBindViewHolder(@NonNull JobListingAdapter.MyViewHolder holder, int position) {
        //Assigns values to rows as they come back on screen
        holder.jobPlaceholder.setText(jobListingModels.get(position).getJobName());
        holder.authorPlaceholder.setText(jobListingModels.get(position).getName());
        holder.descriptionPlaceholder.setText(jobListingModels.get(position).getDescription());
        holder.amountPlaceholder.setText(String.format(context.getString(R.string.amount),jobListingModels.get(position).getAmount()));
        holder.zipPlaceholder.setText(jobListingModels.get(position).getLocation());
        Picasso.get().load(jobListingModels.get(position).getProfileURL()).into(holder.profileImage);

        holder.requestButton.setOnClickListener(view -> {
            holder.db.collection("jobs").document(jobListingModels.get(holder.getAdapterPosition())
                    .getAuthor())
                    .update("potentialWorkers", FieldValue.arrayUnion(holder.user.getUid()));
            Toast.makeText(context,"Request sent to author!",Toast.LENGTH_SHORT).show();
            holder.requestButton.setEnabled(false);
        });
    }

    @Override
    public int getItemCount() {
        //How many items are displayed
        return jobListingModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageButton profileImage;
        TextView jobPlaceholder, authorPlaceholder, descriptionPlaceholder, amountPlaceholder, zipPlaceholder;
        Button requestButton;
        FirebaseUser user;
        FirebaseFirestore db;

        OnJobListener onJobListener;

        public MyViewHolder(@NonNull View itemView, OnJobListener onJobListener) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profile_image_btn);
            jobPlaceholder = itemView.findViewById(R.id.job_placeholder);
            authorPlaceholder = itemView.findViewById(R.id.author_placeholder);
            descriptionPlaceholder = itemView.findViewById(R.id.description_placeholder);
            amountPlaceholder = itemView.findViewById(R.id.amount_placeholder);
            zipPlaceholder = itemView.findViewById(R.id.zip_placeholder);
            requestButton = itemView.findViewById(R.id.request_btn);
            user = FirebaseAuth.getInstance().getCurrentUser();
            db = FirebaseFirestore.getInstance();

            this.onJobListener = onJobListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onJobListener.onJobClick(getAbsoluteAdapterPosition());
        }
    }

    public interface OnJobListener {
        void onJobClick(int position);
    }

}
