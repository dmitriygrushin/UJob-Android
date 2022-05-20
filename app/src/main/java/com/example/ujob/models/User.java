package com.example.ujob.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

// Note to team: Serializable interface will allow custom objects to be passed between Activities if that is needed later
public class User implements Serializable {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String profilePicture;
    private String userId = "";
    private String skill = "";
    private String yearsOfExperience = "";
    private String postalCode = "";
    private String aboutMe = "";

    public User(String firstName, String lastName, String email, String profilePictureURL) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profilePicture = profilePictureURL;
    }
    public User(String firstName, String lastName, String email, String profilePictureURL, String userId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profilePicture = profilePictureURL;
        this.userId = userId;
    }

    public User(String firstName, String lastName, String email, String profilePictureURL, String userId, String yearsOfExperience) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profilePicture = profilePictureURL;
        this.userId = userId;
        this.yearsOfExperience = yearsOfExperience;
    }

    public User(String firstName, String lastName, String email, String profilePictureURL,
                String skill, String yearsOfExperience, String postalCode, String aboutMe) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profilePicture = profilePictureURL;
        this.skill = skill;
        this.yearsOfExperience = yearsOfExperience;
        this.postalCode = postalCode;
        this.aboutMe = aboutMe;
    }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public String getEmail() { return email; }

    public String getProfilePicture() { return profilePicture; }

    public String getSkill() { return skill; }

    public String getYearsOfExperience() { return yearsOfExperience; }

    public String getPostalCode() { return postalCode; }

    public String getAboutMe() { return aboutMe; }

    public String getUserId() { return userId; }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}


/*
        String skill = ((EditText)findViewById(R.id.user_skill)).getText().toString();
        String yearsOfExperience = ((EditText)findViewById(R.id.user_years_of_experience)).getText().toString();
        String postalCode = ((EditText)findViewById(R.id.user_postal_code)).getText().toString();
        String aboutMe = ((EditText)findViewById(R.id.user_about_me)).getText().toString();
 */