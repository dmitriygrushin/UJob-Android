package com.example.ujob.controllers.generalUser;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    /* Firebase */
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /* Firebase */
        auth = FirebaseAuth.getInstance();
        // Used to make NoSQL queries
        db = FirebaseFirestore.getInstance();

        Button registerButton = findViewById(R.id.register_submit);

        registerButton.setOnClickListener(view -> {
            // Get registration form fields
            String firstName = ((EditText)findViewById(R.id.register_first_name)).getText().toString();
            String lastName = ((EditText)findViewById(R.id.register_last_name)).getText().toString();
            String email = ((EditText)findViewById(R.id.register_email)).getText().toString();
            String password = ((EditText)findViewById(R.id.register_password)).getText().toString();

            // Validating that registration form is not empty
            if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(RegisterActivity.this, "Incomplete form!", Toast.LENGTH_LONG).show();
            } else if (password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Password needs to be at least 6 characters!", Toast.LENGTH_LONG).show();
            } else {
                // If form is in good standing then register user
                registerUser(firstName, lastName, email, password);
            }
        });
    }
/*
    public void goToMainActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        String firstName = ((EditText)findViewById(R.id.register_first_name)).getText().toString();
        String lastName = ((EditText)findViewById(R.id.register_last_name)).getText().toString();
        String email = ((EditText)findViewById(R.id.register_email)).getText().toString();
        String password = ((EditText)findViewById(R.id.register_password)).getText().toString();

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName)
                || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "Incomplete form!", Toast.LENGTH_LONG).show();
        } else if (password.length() < 4) {
            Toast.makeText(RegisterActivity.this, "Password needs to be at least 5 characters!", Toast.LENGTH_LONG).show();
        } else {
            registerUser(firstName, lastName, email, password);
        }
        User user = new User(firstName, lastName, email);
        intent.putExtra("User", user);
        startActivity(intent);
    }
    */

    // Register user using Firebase
    private void registerUser(String firstName, String lastName, String email, String password) {
        // register user using async method. This async method returns a task | Note: async methods return on the inside. return value (task) is inside.
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, task -> {
           if (task.isSuccessful()) {
               // The user is already registered by the time we hit this line
               // Save registration form fields in hashmap
               Map<String, String> userFields = new HashMap<>();
               userFields.put("firstName", firstName);
               userFields.put("lastName", lastName);
               userFields.put("email", email);
               // default profile picture
               userFields.put("profilePicture", "https://firebasestorage.googleapis.com/v0/b/ujob-3b797.appspot.com/o/uploads%2FprofilePicture%2Fdefault.jpg?alt=media&token=08c280fe-9d49-4553-9f54-ee26ad6a87b2");

               // get User's Unique ID This is given after the User is registered.
               String uid = Objects.requireNonNull(task.getResult().getUser()).getUid();

               /*   At this point the user is already registered now I'm just making a relationship between the user's uid and the userFields
                    NoSQL query to create a users "table" (they're called collections in NoSQL) with values userFields.
                    NOTE!!!: These values are stored in Firestore while Auth values i.e. email & password are stored in Firebase Authentication
                    I'm just making a relationship between these values. A relationship between the unique assigned id and userFields */
               db.collection("users").document(uid).set(userFields).addOnCompleteListener(task1 -> {
                   if (task1.isSuccessful()) {
                       Toast.makeText(RegisterActivity.this, "Added User Fields!", Toast.LENGTH_SHORT).show();
                   }
               });

               Log.d("registered", "registration to firebase complete");
               Toast.makeText(RegisterActivity.this, "Registration Complete!", Toast.LENGTH_LONG).show();
               startActivity(new Intent(RegisterActivity.this, UserModesActivity.class));
           } else {
               Toast.makeText(RegisterActivity.this, "Registration FAILED - Email in use", Toast.LENGTH_LONG).show();
           }
        });
    }


}