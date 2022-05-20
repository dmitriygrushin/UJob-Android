package com.example.ujob.controllers.worker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ujob.R;
import com.example.ujob.adapters.WorkerModeAcceptedJobsRecyclerAdapter;
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

public class WorkerModeAcceptedJobsListActivity extends AppCompatActivity implements WorkerModeAcceptedJobsRecyclerAdapter.OnJobListener {

    private FirebaseFirestore db;
    private ArrayList<User> usersList ;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_mode_accepted_jobs_list);

        /* Firebase */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // Recycler View & its data
        recyclerView = findViewById(R.id.accepted_jobs_list_recycler_view);
        usersList = new ArrayList<>();


        getAcceptedJobs(db, Objects.requireNonNull(user).getUid(), acceptedJobs -> {
            if (acceptedJobs != null) {
                if (!acceptedJobs.isEmpty()) {
                    getUserDataFromList(db, acceptedJobs, queryDocumentSnapshots -> {
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
                    });
                }
            }
        });
    }


    private void setAdapter() {
        WorkerModeAcceptedJobsRecyclerAdapter adapter = new WorkerModeAcceptedJobsRecyclerAdapter (usersList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    // return data from list of acceptedJobs
    private void getAcceptedJobs(FirebaseFirestore db, String userId, FirestoreCallback<ArrayList<String>> firestoreCallback) {
        DocumentReference docRef = db.collection("users").document(userId); // select job from current user
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    firestoreCallback.onCallBack((ArrayList<String>) Objects.requireNonNull(document.getData()).get("acceptedJobs"));
                } else {
                    Log.d("acceptedJobs", "No such document");
                }
            } else {
                Log.d("acceptedJobs", "get failed with ", task.getException());
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

        Intent intent = new Intent(this, WorkerModeAcceptedJobProfileActivity.class);
        intent.putExtra("userId", userId);

        startActivity(intent);
    }

}