package com.example.ujob.controllers.employer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ujob.R;
import com.example.ujob.controllers.worker.WorkerModeActivity;
import com.example.ujob.models.User;
import com.example.ujob.utilities.FirestoreCallback;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class EmployerProfileActivity extends AppCompatActivity {

    /* View Resources */
    private ImageView profilePicture;
    private TextView firstName, lastName, skill, yearsOfExperience, zipcode, aboutMe;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_profile);

        /* Firebase */
        //String employerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        /* Firebase */
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        /* View Resources */
        profilePicture = findViewById(R.id.employer_profile_pic);
        firstName = findViewById(R.id.employer_profile_first_name);
        lastName = findViewById(R.id.employer_profile_last_name);
        skill = findViewById(R.id.employer_profile_skill);
        yearsOfExperience = findViewById(R.id.employer_profile_years_of_experience);
        zipcode = findViewById(R.id.employer_profile_zipcode);
        aboutMe = findViewById(R.id.employer_profile_about_me);

        Button goBackToJobListing = findViewById(R.id.go_back_to_job_listing_btn);
        goBackToJobListing.setOnClickListener(view -> startActivity(new Intent(EmployerProfileActivity.this, WorkerModeActivity.class)));

        // get worker's userId from previous Activity
        Bundle extras = getIntent().getExtras();
        String employerID = null;
        if (extras != null)  employerID = extras.getString("employerId");

        getUserData(db, employerID, user -> {
            // Set fields of the view
            Picasso.get().load(user.getProfilePicture()).into(profilePicture);
            firstName.append(" " + user.getFirstName());
            lastName.append(" " + user.getLastName());
            skill.append(" " + user.getSkill());
            yearsOfExperience.append(" " + user.getYearsOfExperience());
            zipcode.append(" " + user.getPostalCode());
            aboutMe.append(" " + user.getAboutMe());
        });
    }

    public static void getUserData(FirebaseFirestore db, String userId, FirestoreCallback<User> firestoreCallback) {
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("userInformation", "DocumentSnapshot data: " + document.getData());
                    String profilePicture = (String) Objects.requireNonNull(document.getData()).get("profilePicture");
                    String firstName = (String) document.getData().get("firstName");
                    String lastName = (String) document.getData().get("lastName");
                    String email = (String) document.getData().get("email");

                    // Potentially Null values are set to N/A if User decided not to customize their profile.
                    String skill = document.getData().get("skill") == null ? "N/A" : (String) document.getData().get("skill");
                    String yearsOfExperience = document.getData().get("yearsOfExperience") == null ? "N/A" : (String) document.getData().get("yearsOfExperience");
                    String zipcode = document.getData().get("zipcode") == null ? "N/A" : (String) document.getData().get("zipcode");
                    String aboutMe = document.getData().get("aboutMe") == null ? "N/A" : (String) document.getData().get("aboutMe");

                    firestoreCallback.onCallBack(new User(firstName, lastName, email, profilePicture, skill, yearsOfExperience, zipcode, aboutMe));
                } else {
                    Log.d("userInformation", "No such document");
                }
            } else {
                Log.d("userInformation", "get failed with ", task.getException());
            }
        });
    }
}