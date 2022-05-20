package com.example.ujob.controllers.employer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ujob.R;
import com.example.ujob.controllers.generalUser.UserModesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class EmployerModeActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private Button viewAcceptedWorkers;

    public EmployerModeActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_mode);

        Button viewPotentialWorkers = findViewById(R.id.employer_view_potential_workers_button);
        viewAcceptedWorkers =  findViewById(R.id.employer_view_accepted_workers_button);

        viewAcceptedWorkers.setOnClickListener(view -> startActivity(new Intent(EmployerModeActivity.this, EmployerModeAcceptedListActivity.class)));

        viewPotentialWorkers.setOnClickListener(view -> startActivity(new Intent(EmployerModeActivity.this, EmployerModePotentialEmployeeListActivity.class)));

        Button createJob = findViewById(R.id.create_Job_Button);
        createJob.setOnClickListener(view -> startActivity(new Intent(EmployerModeActivity.this, CreateJobActivity.class)));

        db = FirebaseFirestore.getInstance();

        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        getJobPost(currentUser);

        //listJobData(currentUser);

        Button deleteJobBTN = findViewById(R.id.delete_job_button);
        deleteJobBTN.setOnClickListener(view -> {
            deleteJob(currentUser);
            finish();
            startActivity(getIntent());
        });

        Button editJobBTN = findViewById(R.id.edit_job_button);
        editJobBTN.setOnClickListener(view -> startActivity(new Intent(EmployerModeActivity.this, EditJobActivity.class)));

        Button goBack = findViewById(R.id.go_back);
        goBack.setOnClickListener(view -> startActivity(new Intent(EmployerModeActivity.this, UserModesActivity.class)));

    }


    private void getJobPost(String userID){
        DocumentReference docRef = db.collection("jobs").document(userID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("jobInfo", "DocumentSnapshot data: " + document.getData());
                    String jobName = (String) Objects.requireNonNull(document.getData()).get("jobName");
                    String jobDesc = (String) document.getData().get("description");
                    String payment = (String) document.getData().get("payment");
                    String zipcode = (String) document.getData().get("zipcode");
                    String displayPayment = "Payment: " + payment;
                    String displayZipcode = "Area Code: " + zipcode;
                    ((TextView) findViewById(R.id.display_job_name)).setText(jobName);
                    ((TextView) findViewById(R.id.display_job_desc)).setText(jobDesc);
                    ((TextView) findViewById(R.id.display_payment)).setText(displayPayment);
                    ((TextView) findViewById(R.id.display_zipcode)).setText(displayZipcode);
                    ArrayList<String> acceptedWorkers = (ArrayList<String>) document.getData().get("acceptedWorkers");

                    if (acceptedWorkers != null && acceptedWorkers.isEmpty()) viewAcceptedWorkers.setVisibility(View.GONE);
                    if (acceptedWorkers == null) viewAcceptedWorkers.setVisibility(View.GONE);
                } else {
                    Log.d("jobInfo", "No such document");
                }
            } else {
                Log.d("jobInfo", "get failed with ", task.getException());
            }
        });
    }

    private void deleteJob(String userID) {
        db.collection("jobs").document(userID)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("onSuccess", "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e -> Log.w("onFail", "Error deleting document", e));
    }


}