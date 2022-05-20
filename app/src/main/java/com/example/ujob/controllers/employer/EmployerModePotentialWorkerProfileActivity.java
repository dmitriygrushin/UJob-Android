package com.example.ujob.controllers.employer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ujob.R;
import com.example.ujob.controllers.generalUser.UserModesActivity;
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

public class EmployerModePotentialWorkerProfileActivity extends AppCompatActivity {

    final private String potentialWorkersListFlag = "potentialWorkersList";
    final private String acceptedWorkersListFlag = "acceptedWorkersList";
    final private String pendingWorkersListFlag = "pendingWorkersList";


    /* Firebase */
    private FirebaseFirestore db;
    private final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    /* View Resources */
    private ImageView profilePicture;
    private TextView firstName, lastName, skill, yearsOfExperience, zipcode, aboutMe, email;
    private Button confirmAcceptButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_mode_potential_worker_profile);
        /*
            Todo: if worker is in acceptedWorkers field:
                    if worker is in pendingWorkers field: // meaning that the worker has accepted the job 100% - on the workers end this query will need to be made which will add the worker into the pending list
                        Change onclick button to display --- Work Completed ---
                        Change onclick button to finish job - deletes worker from accepted and pending list
                        display email
                    else:
                        don't display email
                        have default before change
            do the if statemnts above

            if user is in x list then do y...

         */

        /* Firebase */
        db = FirebaseFirestore.getInstance();

        /* View Resources */
        profilePicture = findViewById(R.id.employer_mode_potential_worker_profile_picture);
        firstName = findViewById(R.id.employer_mode_potential_worker_profile_first_name);
        lastName = findViewById(R.id.employer_mode_potential_worker_profile_last_name);
        skill = findViewById(R.id.employer_mode_potential_worker_profile_skill);
        yearsOfExperience = findViewById(R.id.employer_mode_potential_worker_profile_years_of_experience);
        zipcode = findViewById(R.id.employer_mode_potential_worker_profile_zipcode);
        aboutMe = findViewById(R.id.employer_mode_potential_worker_profile_about_me);
        confirmAcceptButton = findViewById(R.id.employer_mode_potential_worker_profile_confirm_accept_button);
        email = findViewById(R.id.employer_mode_potential_worker_profile_email);

        // get worker's userId from previous Activity
        Bundle extras = getIntent().getExtras();
        String workerId = null;
        // userId
        if (extras != null)  workerId = extras.getString("userId");
        String finalWorkerId = workerId;

        // Load general user data
        getUserData(db, workerId, user -> {
            // Set fields of the view
            Picasso.get().load(user.getProfilePicture()).into(profilePicture);
            firstName.append(" " + user.getFirstName());
            lastName.append(" " + user.getLastName());
            skill.append(" " + user.getSkill());
            yearsOfExperience.append(" " + user.getYearsOfExperience());
            zipcode.append(" " + user.getPostalCode());
            aboutMe.append(" " + user.getAboutMe());
            String emailText = "Email : " + user.getEmail();
            email.setText(emailText);
        });

        getWorkerFlag(currentUser.getUid(), finalWorkerId, workerFlag -> {
            if (workerFlag.equals(potentialWorkersListFlag)) {
                Toast.makeText(EmployerModePotentialWorkerProfileActivity.this, workerFlag, Toast.LENGTH_LONG).show();
                potentialWorkerFlagLogic(finalWorkerId);
            }
            if (workerFlag.equals(acceptedWorkersListFlag)) {
                // if (you) the employer accepted the worker then now you need to wait for the worker to confirm and accept you
                Toast.makeText(EmployerModePotentialWorkerProfileActivity.this, workerFlag, Toast.LENGTH_LONG).show();
                acceptedWorkerFlagLogic();
            }
            if (workerFlag.equals(pendingWorkersListFlag)) {
                // when the user (worker) confirms the acceptance of the job then they will be placed in the pendingWorkers list
                Toast.makeText(EmployerModePotentialWorkerProfileActivity.this, workerFlag, Toast.LENGTH_LONG).show();
                pendingWorkerFlagLogic(finalWorkerId);
            }
        });


    }

    /* Modes */
    private void potentialWorkerFlagLogic(String finalWorkerId) {
        String hiddenEmail = "Email: wait for worker response.";
        email.setText(hiddenEmail);
        confirmAcceptButton.setOnClickListener(view -> {
            if (currentUser != null) {
                Toast.makeText(EmployerModePotentialWorkerProfileActivity.this, "Added user to accepted list!", Toast.LENGTH_SHORT).show();
                addPotentialToAcceptedWorker(currentUser.getUid(), finalWorkerId);
                startActivity((new Intent(EmployerModePotentialWorkerProfileActivity.this, UserModesActivity.class)));
            } else {
                Toast.makeText(EmployerModePotentialWorkerProfileActivity.this, "Current User ID Does NOT exist!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void acceptedWorkerFlagLogic() {
        String hiddenEmail = "Email: wait for worker response";
        email.setText(hiddenEmail);
        // (you) the employer accepted the worker now you're waiting for the worker to fully accept the job
        Log.d("acceptedWorkerFlagLogic", "acceptedWorkerFlagLogic: ");
        String waitingForWorkerText = "waiting for worker";
        confirmAcceptButton.setEnabled(false);
        confirmAcceptButton.setText(waitingForWorkerText);
    }

    private void pendingWorkerFlagLogic(String workerId) {
        Log.d("acceptedWorkerFlagLogic", "acceptedWorkerFlagLogic: ");
        String waitingForWorkerText = "Worker Accepted - Click if Job Complete";
        confirmAcceptButton.setText(waitingForWorkerText);

        confirmAcceptButton.setOnClickListener(view -> {
            removeWorkerFromPendingWorkerList(currentUser.getUid(), workerId);
            startActivity((new Intent(EmployerModePotentialWorkerProfileActivity.this, UserModesActivity.class)));
        });
    }

    /*
        potentialWorker Methods
    */
    // Adds potential worker to a list of acceptedWorkers for the job and remove them from potentialWorkers
    private void addPotentialToAcceptedWorker(String currentUserId, String workerId) {
        DocumentReference userJob = db.collection("jobs").document(currentUserId);
        userJob.update("acceptedWorkers", FieldValue.arrayUnion(workerId));
        userJob.update("potentialWorkers", FieldValue.arrayRemove(workerId));

        // Update the worker's list of acceptedJobs
        DocumentReference userWorker = db.collection("users").document(workerId);
        userWorker.update("acceptedJobs", FieldValue.arrayUnion(currentUserId));
    }

    /*
        acceptedWorker Methods
    */

    /*
        pendingWorker Methods
    */
    // remove Worker from accepted & pending Job listings
    private void removeWorkerFromPendingWorkerList(String currentUserId, String workerId) {
        DocumentReference userJob = db.collection("jobs").document(currentUserId);
        userJob.update("pendingWorkers", FieldValue.arrayRemove(workerId));
    }


    /*
        General Methods
    */
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


    private void getWorkerFlag(String userId, String workerId,FirestoreCallback<String> firestoreCallback) {
        DocumentReference docRef = db.collection("jobs").document(userId); // select job from current user
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("getWorkersFlag1", (String) Objects.requireNonNull(document.getData()).get("name"));
                    ArrayList<String> pendingWorkersList = (ArrayList<String>) document.getData().get("pendingWorkers");
                    ArrayList<String> acceptedWorkersList = (ArrayList<String>) document.getData().get("acceptedWorkers");
                    ArrayList<String> potentialWorkersList = (ArrayList<String>) document.getData().get("potentialWorkers");
                    String workerFlag;

                    if (pendingWorkersList != null && pendingWorkersList.contains(workerId)) {
                        workerFlag = pendingWorkersListFlag;
                    } else if (acceptedWorkersList != null && acceptedWorkersList.contains(workerId)) {
                        workerFlag = acceptedWorkersListFlag;
                    } else if (potentialWorkersList != null && potentialWorkersList.contains(workerId)) {
                        workerFlag = potentialWorkersListFlag;
                    } else {
                        workerFlag = "issue";
                        Log.d("acceptedWorkersInfo", "No such worker in either of the lists");
                    }

                    Log.d("firestore", "onComplete: " + workerFlag);
                    firestoreCallback.onCallBack(workerFlag);

                } else {
                    Log.d("acceptedWorkersInfo", "No such document");
                }
            } else {
                Log.d("acceptedWorkersInfo", "get failed with ", task.getException());
            }
        });
    }

}