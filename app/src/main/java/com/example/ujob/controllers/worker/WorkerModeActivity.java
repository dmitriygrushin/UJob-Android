package com.example.ujob.controllers.worker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ujob.R;
import com.example.ujob.controllers.employer.EmployerProfileActivity;
import com.example.ujob.utilities.ZipcodeCallback;
import com.example.ujob.models.ZipcodeRadius;
import com.example.ujob.adapters.JobListingAdapter;
import com.example.ujob.models.JobListingModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class WorkerModeActivity extends AppCompatActivity implements JobListingAdapter.OnJobListener {

    private FirebaseFirestore db;
    SeekBar radius;
    TextView radiusView;
    ProgressBar progressBar;
    FusedLocationProviderClient location;
    private String zipcode="";
    int distance;
    ArrayList<String> zipcodes = new ArrayList<>();
    ArrayList<JobListingModel> jobListingModels;
    RecyclerView recyclerView;
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_mode);

        //Create instance of location provider class to be used in getZipcode()
        location = LocationServices.getFusedLocationProviderClient(this);
        db = FirebaseFirestore.getInstance();

        radius = findViewById(R.id.radius_seek_bar);
        radiusView = findViewById(R.id.radius_text_view);
        progressBar = findViewById(R.id.progressBar);
        radiusView.setText(String.format(getString(R.string.job_radius),15));
        jobListingModels = new ArrayList<>();
        getZipcodes();

        Button acceptedJobsButton = findViewById(R.id.worker_mode_accepted_jobs_list_button);

        acceptedJobsButton.setOnClickListener(view -> startActivity(new Intent(WorkerModeActivity.this, WorkerModeAcceptedJobsListActivity.class)));


        //Updates the radius when the user changes it from the default setting
        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                //increment job radius upon seek bar change
                radiusView.setText(String.format(getString(R.string.job_radius), progress > 0 ? progress * 5 : 1));
                getZipcodes();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });






    }
    private void getZipcode(ZipcodeCallback zipcodeCallback){

        //Check to make sure the user has the correct perms enabled
        if(ActivityCompat.checkSelfPermission(WorkerModeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            //Get the last location of the user (zipcode)
            location.getLastLocation().addOnCompleteListener(task -> {
                distance = radius.getProgress()<1? radius.getProgress()+1:radius.getProgress()*5;
                Location location = task.getResult();
                if(location!=null){
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    try{
                        List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                        Log.d("address",addressList.toString());
                        zipcode = addressList.get(0).getPostalCode();
                        Log.d("zipcode",String.valueOf(addressList.get(0).getPostalCode()));
                        zipcodeCallback.onZipcode(zipcode,distance);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else{
            ActivityCompat.requestPermissions(WorkerModeActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

        }

    }
    //If the user needed to enable the location permissions this async method will get the zipcode once they are accepted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getZipcodes();
            }
        }
    }
    private void getZipcodes(){
        if(recyclerView!=null)
            recyclerView.removeAllViewsInLayout();
        progressBar.setVisibility(View.VISIBLE);
        count=0;
        jobListingModels.clear();
        getZipcode((zipcode, distance) -> {
            ZipcodeRadius obj = new ZipcodeRadius(zipcode,distance);

            obj.getZipcodes(zips -> {
                if(!zips.isEmpty()) {
                    zipcodes = zips;
                    Log.d("Json Zipcodes", zipcodes.toString());
                    setUpJobListingModels();


                }
                else {
                    Log.d("Json Zipcodes", "Empty");
                    runOnUiThread(() -> Toast.makeText(WorkerModeActivity.this, "No jobs available within selected radius", Toast.LENGTH_LONG).show());

                }
            });
        });
    }
    private void setUpJobListingModels(){
        Log.d("Location", "in setUpJobListings");
        CollectionReference colRef = db.collection("jobs");
        //TODO: Fix zipcodes limit
        colRef.whereIn("zipcode",zipcodes.subList(0, Math.min(zipcodes.size(), 10))).get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult().size()>0){
                Log.d("Documents", String.valueOf(task.getResult().size()));

                for(DocumentSnapshot doc : task.getResult()){
                    Map<String,String> model = new HashMap<>();
                    model.put("jobName",doc.getString("jobName"));
                    model.put("name",doc.getString("name"));
                    model.put("description",doc.getString("description"));
                    model.put("zipcode",doc.getString("zipcode"));
                    model.put("payment",doc.getString("payment"));
                    model.put("author",doc.getString("author"));

                    db.collection("users").document(Objects.requireNonNull(doc.getString("author"))).get().addOnSuccessListener(documentSnapshot -> {


                        model.put("profilePicture",documentSnapshot.getString("profilePicture"));
                        jobListingModels.add(new JobListingModel(model.get("jobName"),model.get("name"),model.get("description"),model.get("zipcode"),
                                model.get("payment"),model.get("author"),model.get("profilePicture")));

                        Log.d("JobListingsModels",jobListingModels.get(count).toString());

                        JobListingAdapter adapter = new JobListingAdapter(WorkerModeActivity.this, jobListingModels, this);

                        recyclerView = findViewById(R.id.listingRecyclerView);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(WorkerModeActivity.this));
                        count++;
                    });

                }
            }
            else{
                Log.d("Task","Unsuccessful");
                recyclerView.removeAllViewsInLayout();
                runOnUiThread(() -> Toast.makeText(WorkerModeActivity.this, "No jobs available within selected radius", Toast.LENGTH_LONG).show());

            }
        });
        runOnUiThread(() -> progressBar.setVisibility(View.INVISIBLE));
    }




    @Override
    public void onJobClick(int position) {
        String employerId = jobListingModels.get(position).getAuthor(); // reference to the user that was selected.

        Intent intent = new Intent(this, EmployerProfileActivity.class);
        intent.putExtra("employerId", employerId);

        startActivity(intent);

    }
}