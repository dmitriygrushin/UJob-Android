package com.example.ujob.controllers.employer;

/*
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class EmployerModeAcceptedListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_mode_accepted_list);
    }
}

 */

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ujob.R;
import com.example.ujob.adapters.EmployerModeWorkersRecyclerAdapter;
import com.example.ujob.models.User;
import com.example.ujob.utilities.FirestoreCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class  EmployerModeAcceptedListActivity extends AppCompatActivity implements EmployerModeWorkersRecyclerAdapter.OnJobListener {

    private FirebaseFirestore db;
    private ArrayList<User> usersList ;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_mode_accepted_list);

        /* Firebase */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.accepted_employee_list_recycler_view);

        usersList = new ArrayList<>();

        getPotentialWorkers(db, Objects.requireNonNull(user).getUid(), acceptedWorkers -> getUserDataFromList(db, acceptedWorkers, queryDocumentSnapshots -> {
            // Add list of users to usersList
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                /* Todo: Works but better to Refactor: The user is being queried but then the Id will be sent to the next activity to be queried again.
                        Essentially the data that's already been queried is being queried again. Issue being that not all users have customized their profile leaving fields missing */
                Log.d("listOfUserIds", document.getId() + " => " + document.getData());
                String firstName = (String) document.getData().get("firstName");
                String lastName = ((String) document.getData().get("lastName"));
                String email = ((String) document.getData().get("email"));
                String profilePicture = ((String) document.getData().get("profilePicture"));
                String yearsOfExperience = ((String) document.getData().get("yearsOfExperience"));
                String userId = document.getId();
                usersList.add(new User(firstName, lastName, email, profilePicture, userId, yearsOfExperience));
            }
            // adapter for recycler view
            setAdapter();
        }));
    }

    private void setAdapter() {
        EmployerModeWorkersRecyclerAdapter adapter = new EmployerModeWorkersRecyclerAdapter(usersList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void getPotentialWorkers(FirebaseFirestore db, String userId, FirestoreCallback<ArrayList<String>> firestoreCallback) {
        DocumentReference docRef = db.collection("jobs").document(userId); // select job from current user
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    ArrayList<String> acceptedWorkers = (ArrayList<String>) Objects.requireNonNull(document.getData()).get("acceptedWorkers");
                    ArrayList<String> pendingWorkers = (ArrayList<String>) document.getData().get("pendingWorkers");
                    if (pendingWorkers != null) {
                        if (acceptedWorkers != null) {
                            acceptedWorkers.addAll(pendingWorkers);
                        }
                    }
                    firestoreCallback.onCallBack(acceptedWorkers);
                } else {
                    Log.d("acceptedWorkersInfo", "No such document");
                }
            } else {
                Log.d("acceptedWorkersInfo", "get failed with ", task.getException());
            }
        });
    }

    private void getUserDataFromList(FirebaseFirestore db, ArrayList<String> userIds, FirestoreCallback<QuerySnapshot> firestoreCallback) {
        // Find users based on array of  userIds (documentId)
        db.collection("users").whereIn(FieldPath.documentId(), userIds).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot result = task.getResult();
                firestoreCallback.onCallBack(result);
            } else {
                Log.d("listOfUserIds", "Error getting documents: ", task.getException());
            }
        });

    }

    @Override
    public void onJobClick(int position) {
        String userId = usersList.get(position).getUserId(); // reference to the user that was selected.

        Intent intent = new Intent(this, EmployerModePotentialWorkerProfileActivity.class);
        intent.putExtra("userId", userId);

        startActivity(intent);
    }
}
