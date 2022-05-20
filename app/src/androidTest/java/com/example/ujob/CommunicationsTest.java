package com.example.ujob;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.SystemClock;
import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.ujob.controllers.generalUser.MainActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CommunicationsTest {

    private final String employerId = "09wwxXLGOKY4JhPyiH7iuDxqw5D2"; // Email: testJob@gmail.com
    private final String workerId = "vi3CvgBnePN8gGGMm8LWaULakfK2"; // Email: test@gmail.com
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);


    @Test // User Story #3 - Scenario 1
    public void seeAcceptedJobsList() {
        login();
        onView(withId(R.id.user_mode_employee_button)).perform(click());
        onView(withId(R.id.worker_mode_view)).check(matches(isDisplayed()));
        onView(withId(R.id.worker_mode_accepted_jobs_list_button)).perform(click());
        onView(withId(R.id.accepted_list_of_jobs_view)).check(matches(isDisplayed()));
    }

    @Test // User Story #3 - Scenario 2
    public void checkState() {
        workerAcceptJob();
        employerAcceptWorker();

        login();
        onView(withId(R.id.user_mode_employee_button)).perform(click());
        onView(withId(R.id.worker_mode_view)).check(matches(isDisplayed()));
        onView(withId(R.id.worker_mode_accepted_jobs_list_button)).perform(click());
        onView(withId(R.id.accepted_list_of_jobs_view)).check(matches(isDisplayed()));
        onView(new RecyclerViewMatcher(R.id.accepted_jobs_list_recycler_view).atPositionOnView(0,R.id.accept_potential_worker_button)).perform(click());
        onView(withId(R.id.worker_mode_job_profile_view)).check(matches(isDisplayed()));
        testCleanUp();
    }

    @Test // User Story #3 - Scenario 3
    public void changeState() {
        workerAcceptJob();
        employerAcceptWorker();

        login();
        onView(withId(R.id.user_mode_employee_button)).perform(click());
        onView(withId(R.id.worker_mode_view)).check(matches(isDisplayed()));
        onView(withId(R.id.worker_mode_accepted_jobs_list_button)).perform(click());
        onView(withId(R.id.accepted_list_of_jobs_view)).check(matches(isDisplayed()));
        SystemClock.sleep(1500);
        onView(new RecyclerViewMatcher(R.id.accepted_jobs_list_recycler_view).atPositionOnView(0,R.id.accept_potential_worker_button)).perform(click());
        SystemClock.sleep(1500);
        onView(withId(R.id.worker_mode_job_profile_view)).check(matches(isDisplayed()));
        SystemClock.sleep(1500);
        onView((withId(R.id.worker_mode_accepted_job_profile_confirm_button))).perform(click());
        //SystemClock.sleep(1500);
        onView(withId(R.id.worker_mode_view)).check(matches(isDisplayed()));
        testCleanUp();
    }

    // cleans up user and job to be ready for testing
    public void testCleanUp() {
        DocumentReference userJob = db.collection("jobs").document(employerId);
        userJob.update("acceptedWorkers", FieldValue.arrayRemove(workerId));
        userJob.update("potentialWorkers", FieldValue.arrayRemove(workerId));

        // Update the worker's list of acceptedJobs
        DocumentReference userWorker = db.collection("users").document(workerId);
        userWorker.update("acceptedJobs", FieldValue.arrayRemove(employerId));
    }

    public void workerAcceptJob() {
        db.collection("jobs").document(employerId).update("potentialWorkers", FieldValue.arrayUnion(workerId));
    }

    public void employerAcceptWorker() {
        DocumentReference userJob = db.collection("jobs").document(employerId);
        userJob.update("acceptedWorkers", FieldValue.arrayUnion(workerId));
        userJob.update("potentialWorkers", FieldValue.arrayRemove(workerId));

        // Update the worker's list of acceptedJobs
        DocumentReference userWorker = db.collection("users").document(workerId);
        userWorker.update("acceptedJobs", FieldValue.arrayUnion(employerId));
    }

    /*
        holder.requestButton.setOnClickListener(view -> {
            holder.db.collection("jobs").document(jobListingModels.get(holder.getAdapterPosition())
                    .getAuthor())
                    .update("potentialWorkers", FieldValue.arrayUnion(holder.user.getUid()));
            Toast.makeText(context,"Request sent to author!",Toast.LENGTH_SHORT).show();
            holder.requestButton.setEnabled(false);
        });
     */


    /* Helper methods */
    public void login() {
        onView(withId(R.id.email)).perform(typeText("test@gmail.com"));
        onView(withId(R.id.password)).perform(typeText("123456"));
        onView(withId(R.id.main_activity_view)).perform(closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        SystemClock.sleep(1500);
        onView(withId(R.id.user_mode_view)).check(matches(isDisplayed()));
    }

    /* Utilities */
    public static class MyViewAction {
        public static ViewAction clickChildViewWithId(final int id) {
            return new ViewAction() {
                @Override
                public Matcher<View> getConstraints() {
                    return null;
                }

                @Override
                public String getDescription() {
                    return "Click on a child view with specified id.";
                }

                @Override
                public void perform(UiController uiController, View view) {
                    View v = view.findViewById(id);
                    v.performClick();
                }
            };
        }

    }

}
