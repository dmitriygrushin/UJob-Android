package com.example.ujob.controllers.generalUser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ujob.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    /* Firebase */
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("test", "onCreate");

        /* Firebase */
        auth = FirebaseAuth.getInstance();

        Button registerButton = findViewById(R.id.main_register_button);
        Button loginButton = findViewById(R.id.login_button);

        // Log the user in using Firebase auth
        loginButton.setOnClickListener(view -> {
            String email = ((EditText)findViewById(R.id.email)).getText().toString();
            String password = ((EditText)findViewById(R.id.password)).getText().toString();

            // check if login fields are empty
            if (!(email.isEmpty() || password.isEmpty())) {
                loginUser(email, password);
                // finish();
            } else {
                Toast.makeText(MainActivity.this, "Incomplete Sign-in Form", Toast.LENGTH_SHORT).show();
            }
        });

        registerButton.setOnClickListener(this::goToRegisterActivity);

    }

    protected void onStart() {
        super.onStart();
        Log.d("test", "onStart");
    }

    protected void onResume() {
        super.onResume();
        Log.d("test", "onResume");
    }

    protected void onPause() {
        super.onPause();
        Log.d("test", "onPause");
    }

    protected void onStop() {
        super.onStop();
        Log.d("test", "onStop");
    }

    // login user using Firebase auth
    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // if successful then send user to pick user modes
                Toast.makeText(MainActivity.this, "Logged In Successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, UserModesActivity.class));
            } else {
                Toast.makeText(MainActivity.this, "Wrong credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToRegisterActivity(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

}

// private void loginUser(String email, String password) {
//     auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//         @Override
//         public void onSuccess(AuthResult authResult) {
//             Toast.makeText(MainActivity.this, "Logged In Successfully!", Toast.LENGTH_SHORT).show();
//             startActivity(new Intent(MainActivity.this, UserMode.class));
//         }

//     });
// }
