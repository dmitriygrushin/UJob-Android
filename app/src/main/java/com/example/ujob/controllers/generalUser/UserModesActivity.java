package com.example.ujob.controllers.generalUser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ujob.R;
import com.example.ujob.controllers.employer.EmployerModeActivity;
import com.example.ujob.controllers.worker.WorkerModeActivity;
import com.example.ujob.models.User;
import com.example.ujob.utilities.FirestoreCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class UserModesActivity extends AppCompatActivity {

    /* Firebase */
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_mode);

        /* Firebase */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        Button logoutButton = findViewById(R.id.logout_button);
        Button employeeButton = findViewById(R.id.user_mode_employee_button);
        Button employerButton = findViewById(R.id.user_mode_employer_button);
        Button editProfileButton = findViewById(R.id.current_user_edit_profile_button);
        ImageView profilePicture = findViewById(R.id.current_user_profile_picture);

        // String userId = user.getUid();
        // Get current users data from FirebaseFirestore
        getUserData(db, Objects.requireNonNull(user).getUid(), user1 -> {
            String userFirstAndLastName = "Welcome " + user1.getFirstName() + " " + user1.getLastName();
            ((TextView) findViewById(R.id.current_user_welcome)).setText(userFirstAndLastName);
            // String userProfilePicture = user.getProfilePicture();
            Picasso.get().load(user1.getProfilePicture()).into(profilePicture);
        });

        // onClick sends user to EditUserActivity
        editProfileButton.setOnClickListener(view -> startActivity(new Intent(UserModesActivity.this, EditUserActivity.class)));

        employeeButton.setOnClickListener(view -> startActivity(new Intent(UserModesActivity.this, WorkerModeActivity.class)));

        employerButton.setOnClickListener(view -> startActivity(new Intent(UserModesActivity.this, EmployerModeActivity.class)));

        logoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(UserModesActivity.this, "Logged Out!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(UserModesActivity.this, MainActivity.class));
        });
    }

    // Updates users' information after returning from EditUserActivity without restarting the application
    @Override
    protected void onRestart() {
        super.onRestart();
        ImageView profilePicture = findViewById(R.id.current_user_profile_picture);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get current users data from FirebaseFirestore
        getUserData(db, Objects.requireNonNull(user).getUid(), user1 -> {
            String userFirstAndLastName = "Welcome " + user1.getFirstName() + " " + user1.getLastName();
            ((TextView) findViewById(R.id.current_user_welcome)).setText(userFirstAndLastName);
            // String userProfilePicture = user.getProfilePicture();
            Picasso.get().load(user1.getProfilePicture()).into(profilePicture);
        });
    }

    // Get user Data from Firebase Firestore
    // Note just like other queries this is also an async method.
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
}