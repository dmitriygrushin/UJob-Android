package com.example.ujob.controllers.employer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ujob.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class CreateJobActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_job);

        //currentUserID = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        db = FirebaseFirestore.getInstance();

        Button postJobButton = findViewById(R.id.post_job_button);
        postJobButton.setOnClickListener(view -> {
            String authorName = ((EditText)findViewById(R.id.author_Name_Text)).getText().toString();
            String jobName = ((EditText)findViewById(R.id.job_name_text)).getText().toString();
            String jobDesc = ((EditText)findViewById(R.id.job_desc_text)).getText().toString();
            String payment = ((EditText)findViewById(R.id.payment_text)).getText().toString();
            String zipcode = ((EditText)findViewById(R.id.zipcode_text)).getText().toString();

            if(TextUtils.isEmpty(authorName) || TextUtils.isEmpty(jobName) || TextUtils.isEmpty(jobDesc)
                    || TextUtils.isEmpty(payment) || TextUtils.isEmpty(zipcode)) {
                Toast.makeText(CreateJobActivity.this, "Form is incomplete!", Toast.LENGTH_LONG).show();
            }
            else {
                createPost(authorName, jobName, jobDesc, payment, zipcode);
            }
        });
    }

    private void createPost(String authorName, String jobName, String jobDesc, String payment, String zipcode) {

        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        HashMap<String, String> jobMap = new HashMap<>();
        jobMap.put("author", currentUser);
        jobMap.put("name", authorName);
        jobMap.put("description", jobDesc);
        jobMap.put("jobName", jobName);
        jobMap.put("payment", payment);
        jobMap.put("zipcode", zipcode);

        db.collection("jobs").document(currentUser).set(jobMap).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Toast.makeText(CreateJobActivity.this, "Job Posted!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(CreateJobActivity.this, EmployerModeActivity.class));
            }
            else {
                Toast.makeText(CreateJobActivity.this, "Oops an error has occurred! Please try again", Toast.LENGTH_LONG).show();
            }
        });

    }

}