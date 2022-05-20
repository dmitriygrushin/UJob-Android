package com.example.ujob.controllers.generalUser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ujob.R;
import com.example.ujob.models.User;
import com.example.ujob.utilities.FirestoreCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class EditUserActivity extends AppCompatActivity {

    private Uri imageUri;

    /* Firebase */
    private FirebaseFirestore db;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        db = FirebaseFirestore.getInstance();

        Button uploadPicture = findViewById(R.id.upload_picture_button);
        Button submitButton = findViewById(R.id.edit_user_submit_button);

        // Get current users data from FirebaseFirestore
        getUserData(db, user.getUid(), (FirestoreCallback<User>) user -> {
            // Autocomplete pre-existing fields
            ((EditText)findViewById(R.id.user_skill)).setText(user.getSkill());
            ((EditText)findViewById(R.id.user_years_of_experience)).setText(user.getYearsOfExperience());
            ((EditText)findViewById(R.id.user_postal_code)).setText(user.getPostalCode());
            ((EditText)findViewById(R.id.user_about_me)).setText(user.getAboutMe());
        });

        // submit form
        submitButton.setOnClickListener(view -> {
            // get form info
            String skill = ((EditText)findViewById(R.id.user_skill)).getText().toString();
            String yearsOfExperience = ((EditText)findViewById(R.id.user_years_of_experience)).getText().toString();
            String postalCode = ((EditText)findViewById(R.id.user_postal_code)).getText().toString();
            String aboutMe = ((EditText)findViewById(R.id.user_about_me)).getText().toString();

            if (TextUtils.isEmpty(skill) && TextUtils.isEmpty(yearsOfExperience) && TextUtils.isEmpty(postalCode) && TextUtils.isEmpty(aboutMe)) {
                Toast.makeText(EditUserActivity.this, "Empty Form!", Toast.LENGTH_LONG).show();
            } else {
                updateUserInfo(skill, yearsOfExperience, postalCode, aboutMe);
            }
        });

        uploadPicture.setOnClickListener(view -> openImage());

    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        GalleryActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> GalleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imageUri = Objects.requireNonNull(result.getData()).getData();
                        uploadImage();
                    }
                }
            });

    private String getFileExtension (Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null){
            final StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("uploads/profilePicture/" + user.getUid() + "/").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            fileRef.putFile(imageUri).addOnCompleteListener(task -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String url = uri.toString(); // url to access image from firebase
                Log.d("DownloadUrl" , url);
                pd.dismiss();

                /* Associating uploaded image with the current user */
                Map<String, Object> userImageURL = new HashMap<>();
                userImageURL.put("profilePicture", url);
                db.collection("users").document(user.getUid()).update(userImageURL).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        // Toast.makeText(RegisterActivity.this, "Added User Fields!", Toast.LENGTH_SHORT).show();
                    }
                });

                Toast.makeText(EditUserActivity.this, "Image upload successful", Toast.LENGTH_SHORT).show();
            }));
        }
    }

    public void updateUserInfo(String skill, String yearsOfExperience, String postalCode, String aboutMe) {

        Map<String, Object> userFields = new HashMap<>();
        userFields.put("skill", skill);
        userFields.put("yearsOfExperience", yearsOfExperience);
        userFields.put("postalCode", postalCode);
        userFields.put("aboutMe", aboutMe);

        db.collection("users").document(user.getUid()).update(userFields).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditUserActivity.this, "Edited User Fields! - Success!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EditUserActivity.this, UserModesActivity.class));
            } else {
                Toast.makeText(EditUserActivity.this, "Edited User Fields NOT successful - Failure!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Get user Data from Firebase Firestore
    // Note just like other queries this is also an async method.
    public static void getUserData(FirebaseFirestore db, String userId, FirestoreCallback firestoreCallback) {
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
                    String skill = (String) document.getData().get("skill");
                    String yearsOfExperience = (String) document.getData().get("yearsOfExperience");
                    String postalCode = (String) document.getData().get("postalCode");
                    String aboutMe = (String) document.getData().get("aboutMe");
                    firestoreCallback.onCallBack(new User(firstName, lastName, email, profilePicture, skill, yearsOfExperience, postalCode, aboutMe));
                } else {
                    Log.d("userInformation", "No such document");
                }
            } else {
                Log.d("userInformation", "get failed with ", task.getException());
            }
        });
    }
}