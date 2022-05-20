package com.example.ujob.controllers.employer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ujob.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class EditJobActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job);

        db = FirebaseFirestore.getInstance();

        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        getJobPost(currentUser);

        Button finish = findViewById(R.id.finish_edit_button);
        finish.setOnClickListener(view -> {
            String authorName = ((EditText)findViewById(R.id.edit_author_Name_Text)).getText().toString();
            String jobName = ((EditText)findViewById(R.id.edit_job_name_text)).getText().toString();
            String jobDesc = ((EditText)findViewById(R.id.edit_job_desc_text)).getText().toString();
            String payment = ((EditText)findViewById(R.id.edit_payment_text)).getText().toString();
            String zipcode = ((EditText)findViewById(R.id.edit_zipcode_text)).getText().toString();

            if(TextUtils.isEmpty(authorName) || TextUtils.isEmpty(jobName) || TextUtils.isEmpty(jobDesc)
                    || TextUtils.isEmpty(payment) || TextUtils.isEmpty(zipcode)) {
                Toast.makeText(EditJobActivity.this, "Form is incomplete!", Toast.LENGTH_LONG).show();
            }
            else {
                editJob(currentUser, authorName, jobName, jobDesc, payment, zipcode);
            }
        });

    }

    private void getJobPost(String userID){
        DocumentReference docRef = db.collection("jobs").document(userID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("jobInfo", "DocumentSnapshot data: " + document.getData());
                    String id = (String) Objects.requireNonNull(document.getData()).get("name");
                    String jobName = (String) document.getData().get("jobName");
                    String jobDesc = (String) document.getData().get("description");
                    String payment = (String) document.getData().get("payment");
                    String zipcode = (String) document.getData().get("zipcode");

                    ((EditText) findViewById(R.id.edit_author_Name_Text)).setText(id);
                    ((EditText) findViewById(R.id.edit_job_name_text)).setText(jobName);
                    ((EditText) findViewById(R.id.edit_job_desc_text)).setText(jobDesc);
                    ((EditText) findViewById(R.id.edit_payment_text)).setText(payment);
                    ((EditText) findViewById(R.id.edit_zipcode_text)).setText(zipcode);

                } else {
                    Log.d("jobInfo", "No such document");
                }
            } else {
                Log.d("jobInfo", "get failed with ", task.getException());
            }
        });
    }

    private void editJob(String userID,String name, String jobName, String jobDesc, String payment, String zipcode) {

        HashMap<String, Object> jobMap = new HashMap<>();
        jobMap.put("name", name);
        jobMap.put("description", jobDesc);
        jobMap.put("jobName", jobName);
        jobMap.put("payment", payment);
        jobMap.put("zipcode", zipcode);

        db.collection("jobs").document(userID).update(jobMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditJobActivity.this, "Edited Job Fields! - Success!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EditJobActivity.this, EmployerModeActivity.class));
            } else {
                Toast.makeText(EditJobActivity.this, "Edited Job Fields NOT successful - Failure!", Toast.LENGTH_SHORT).show();
            }
        });

    }

}