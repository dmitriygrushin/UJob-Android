package com.example.ujob.models;

import androidx.annotation.NonNull;

public class JobListingModel {
    String jobName;
    String name;
    String description;
    String location;
    String amount;
    String profileURL;
    String author;

    @NonNull
    @Override
    public String toString() {
        return "JobListingModel{" +
                "jobName='" + jobName + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", amount='" + amount + '\'' +
                ", profileURL='" + profileURL + '\'' +
                '}';
    }

    public JobListingModel(String jobName, String name, String description,
                           String location, String amount,String author, String profileURL) {
        this.jobName = jobName;
        this.name = name;
        this.description = description;
        this.location = location;
        this.amount = amount;
        this.author = author;
        this.profileURL = profileURL;
    }

    public String getJobName() {
        return jobName;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getAmount() {
        return amount;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public String getAuthor() {
        return author;
    }
}
