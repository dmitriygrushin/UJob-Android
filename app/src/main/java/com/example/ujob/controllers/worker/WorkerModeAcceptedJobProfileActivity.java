package com.example.ujob.controllers.worker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ujob.R;
import com.example.ujob.models.JobListingModel;
import com.example.ujob.models.User;
import com.example.ujob.utilities.FirestoreCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class WorkerModeAcceptedJobProfileActivity extends AppCompatActivity {
    /*
        Todo: add confirm completing of job for the employer mode and allow the employerMode to see the email of the Worker
     */

    /* Firebase */
    private FirebaseFirestore db;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    /* View Resources */
    private ImageView profilePicture;
    private Button jobButton;
    private TextView name;
    private TextView jobName;
    private TextView description;
    private TextView zipcode;
    private TextView payment;
    private TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_mode_accepted_job_profile);

        /* Firebase */
        db = FirebaseFirestore.getInstance();

        /* View Resources */
        profilePicture = findViewById(R.id.worker_mode_accepted_job_profile_picture);
        jobButton = findViewById(R.id.worker_mode_accepted_job_profile_confirm_button);
        name = findViewById(R.id.worker_mode_accepted_job_profile_name);
        jobName = findViewById(R.id.worker_mode_accepted_job_profile_job_name);
        description = findViewById(R.id.worker_mode_accepted_job_profile_description);
        zipcode = findViewById(R.id.worker_mode_accepted_job_profile_zipcode);
        payment = findViewById(R.id.worker_mode_accepted_job_profile_payment);
        email = findViewById(R.id.worker_mode_accepted_job_profile_email);

        // get worker's userId from previous Activity
        Bundle extras = getIntent().getExtras();
        String employerId = null;
        if (extras != null)  employerId = extras.getString("userId");
        // Set Job data
        getJobData(db, employerId, jobListingModel -> {
            name.append(" " + jobListingModel.getName());
            jobName.append(" " + jobListingModel.getJobName());
            description.append(" " + jobListingModel.getDescription());
            zipcode.append(" " + jobListingModel.getLocation());
            payment.append(" " + jobListingModel.getAmount());
        });

        getUserData(db, employerId, user -> Picasso.get().load(user.getProfilePicture()).into(profilePicture));

        String finalEmployerId = employerId;
        // check if the this job is in the pendingJobs list. If so, then the acceptButton onClick function will change
        getPendingJobsList(currentUser.getUid(), pendingJobsList -> {
            // if the job is in the pendingJobs list then the user cannot accept the same job
            if (pendingJobsList != null && pendingJobsList.contains(finalEmployerId)) {
                String jobCompleteText = "---JOB COMPLETE!---";
                jobButton.setText(jobCompleteText);
                completeJobOnClick(finalEmployerId);
                displayEmployerEmail(finalEmployerId);
            } else {
                jobButton.setOnClickListener(view -> {
                    // add job to pendingJobs List
                    String jobCompleteText = "---JOB COMPLETE---";
                    addAcceptedJobToPendingJobsList(finalEmployerId);
                    Toast.makeText(WorkerModeAcceptedJobProfileActivity.this, "Job Accepted!", Toast.LENGTH_LONG).show();
                    jobButton.setText(jobCompleteText);
                    displayEmployerEmail(finalEmployerId);
                    completeJobOnClick(finalEmployerId);
                });
            }
        });
    }

    // sets new onClick Listener for Accept Button (complete Job onClick)
    private void completeJobOnClick(String employerId) {
        jobButton.setOnClickListener(view -> {
            deleteJob(employerId);
            startActivity(new Intent(WorkerModeAcceptedJobProfileActivity.this, WorkerModeActivity.class));
        });
    }

    private void displayEmployerEmail(String employerId) {
        getUserData(db, employerId, user -> {
            String emailText =  "EMAIL: " + user.getEmail();
            email.setText(emailText);
        });
    }

    public static void getJobData(FirebaseFirestore db, String userId, FirestoreCallback<JobListingModel> firestoreCallback) {
        DocumentReference docRef = db.collection("jobs").document(userId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("userInformation", "DocumentSnapshot data: " + document.getData());
                    String jobName = (String) Objects.requireNonNull(document.getData()).get("jobName");
                    String name = (String) document.getData().get("name");
                    String description = (String) document.getData().get("description");
                    String location = (String) document.getData().get("zipcode");
                    String payment = (String) document.getData().get("payment");
                    String author = "";
                    String profileURL = "";
                    firestoreCallback.onCallBack(new JobListingModel(jobName, name, description, location, payment, author, profileURL));
                } else {
                    Log.d("userInformation", "No such document");
                }
            } else {
                Log.d("userInformation", "get failed with ", task.getException());
            }
        });
    }

    public static void getUserData(FirebaseFirestore db, String userId, FirestoreCallback<User> firestoreCallback) {
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("userInformation", "DocumentSnapshot data: " + document.getData());
                    String firstName = (String) Objects.requireNonNull(document.getData()).get("firstName");
                    String lastName = (String) document.getData().get("lastName");
                    String email = (String) document.getData().get("email");
                    String profilePicture = (String) document.getData().get("profilePicture");
                    firestoreCallback.onCallBack(new User(firstName, lastName, email, profilePicture));
                } else {
                    Log.d("userInformation", "No such document");
                }
            } else {
                Log.d("userInformation", "get failed with ", task.getException());
            }
        });
    }

    // update the user in their jobsList and in the employers job list
    private void addAcceptedJobToPendingJobsList (String jobId) {
        // sends acceptedJob to pendingJob
        DocumentReference userJob = db.collection("users").document(currentUser.getUid());
        userJob.update("pendingJobs", FieldValue.arrayUnion(jobId));

        // update (this user) the worker from acceptedWorkers to pendingWorkers list
        DocumentReference employerJob = db.collection("jobs").document(jobId);
        employerJob.update("pendingWorkers", FieldValue.arrayUnion(currentUser.getUid()));
        // employerJob.update("acceptedWorkers", FieldValue.arrayRemove(currentUser.getUid()));

    }

    // Delete job from acceptedJobs List and pendingJobs List
    private void deleteJob (String jobId) {
        DocumentReference userJob = db.collection("users").document(currentUser.getUid());
        userJob.update("pendingJobs", FieldValue.arrayRemove(jobId));
        userJob.update("acceptedJobs", FieldValue.arrayRemove(jobId));
    }

    // get pendingJobs list from the Worker
    private void getPendingJobsList(String userId, FirestoreCallback<ArrayList<String>> firestoreCallback) {
        DocumentReference docRef = db.collection("users").document(userId); // select job from current user
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    firestoreCallback.onCallBack((ArrayList<String>) Objects.requireNonNull(document.getData()).get("pendingJobs"));
                } else {
                    Log.d("acceptedWorkersInfo", "No such document");
                }
            } else {
                Log.d("acceptedWorkersInfo", "get failed with ", task.getException());
            }
        });
    }
}