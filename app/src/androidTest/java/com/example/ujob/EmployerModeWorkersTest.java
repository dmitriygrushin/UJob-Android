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
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.util.HumanReadables;
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
public class EmployerModeWorkersTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test // User Profiles #10 - Scenario 1
    public void noPotentialWorkersExist() {
        // removes all workers from test user
        removeDummyPotentialWorkers();

        login();

        // get in employerMode
        onView(withId(R.id.user_mode_employer_button)).perform(click());
        onView(withId(R.id.EmployerMode_view)).check(matches(isDisplayed()));

        // check for potential workers
        onView(withId(R.id.employer_view_potential_workers_button)).perform(click());
        onView(withId(R.id.list_of_potential_workers_view)).check(matches(isDisplayed()));

        // check for flag if potential workers are being displayed
        onView(withId(R.id.potential_employee_list_nothing_text)).check(matches(isDisplayed()));
    }

    @Test // User Profiles #10 - Scenario 2
    public void potentialWorkersExist() {
        createJobDummyJob();
        addDummyPotentialWorker();

        // check for potential workers
        onView(withId(R.id.employer_view_potential_workers_button)).perform(click());
        onView(withId(R.id.list_of_potential_workers_view)).check(matches(isDisplayed()));

        // check for flag if potential workers are being displayed
        SystemClock.sleep(5500);
        onView(withId(R.id.potential_employee_list_nothing_text)).check(isNotDisplayed());
    }

    @Test // User Profiles #10 - Scenario 3
    public void acceptUser() {
        createJobDummyJob();
        addDummyPotentialWorker();

        // check for potential workers
        onView(withId(R.id.employer_view_potential_workers_button)).perform(click());
        SystemClock.sleep(3500);
        onView(withId(R.id.list_of_potential_workers_view)).check(matches(isDisplayed()));

        // check for flag if potential workers are being displayed
        onView(withId(R.id.potential_employee_list_nothing_text)).check(isNotDisplayed());
        onView(withId(R.id.potential_employee_list_recycler_view)).perform(click());
    }

    /* Helper methods */
    public void login() {
        onView(withId(R.id.email)).perform(typeText("test@gmail.com"));
        onView(withId(R.id.password)).perform(typeText("123456"));
        onView(withId(R.id.main_activity_view)).perform(closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        SystemClock.sleep(1500);
        onView(withId(R.id.user_mode_view)).check(matches(isDisplayed()));
    }

    private void addDummyPotentialWorker() {
        FirebaseFirestore db =  FirebaseFirestore.getInstance();
        DocumentReference userJob = db.collection("jobs").document("vi3CvgBnePN8gGGMm8LWaULakfK2");
        userJob.update("potentialWorkers", FieldValue.arrayUnion("PH6PBKKBJ0QKkT7VfPqSkaCaR0S2"));
    }

    private void createJobDummyJob() {
        onView(withId(R.id.email)).perform(typeText("test@gmail.com"));
        onView(withId(R.id.password)).perform(typeText("123456"));
        onView(withId(R.id.main_activity_view)).perform(closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        SystemClock.sleep(1500);
        onView(withId(R.id.user_mode_view)).check(matches(isDisplayed()));
        onView(withId(R.id.user_mode_employer_button)).perform(click());
        onView(withId(R.id.EmployerMode_view)).check(matches(isDisplayed()));
        onView(withId(R.id.create_Job_Button)).perform(click());
        onView(withId(R.id.author_Name_Text)).perform(typeText("Mister Tester"));
        onView(withId(R.id.CreateJob_View)).perform(closeSoftKeyboard()); // - changed
        onView(withId(R.id.job_name_text)).perform(typeText("Create Tests"));
        onView(withId(R.id.CreateJob_View)).perform(closeSoftKeyboard()); // - changed - some devices this test will fail if keyboard not closed
        onView(withId(R.id.job_desc_text)).perform(typeText("Make espresso tests for the app"));
        onView(withId(R.id.CreateJob_View)).perform(closeSoftKeyboard()); // - changed - some devices this test will fail if keyboard not closed
        onView(withId(R.id.payment_text)).perform(typeText("15"));
        onView(withId(R.id.CreateJob_View)).perform(closeSoftKeyboard());
        onView(withId(R.id.zipcode_text)).perform(typeText("11218"));
        onView(withId(R.id.CreateJob_View)).perform(closeSoftKeyboard());
        onView(withId(R.id.post_job_button)).perform(click());
        onView(withId(R.id.EmployerMode_view)).check(matches(isDisplayed()));
        SystemClock.sleep(2500);
    }

    private void removeDummyPotentialWorkers() {
        FirebaseFirestore db =  FirebaseFirestore.getInstance();
        DocumentReference userJob = db.collection("jobs").document("vi3CvgBnePN8gGGMm8LWaULakfK2");
        userJob.update("potentialWorkers", FieldValue.delete());
    }
    public static ViewAssertion isNotDisplayed() {
        return (view, noView) -> {
            if (view != null && isDisplayed().matches(view)) {
                throw new AssertionError("View is present in the hierarchy and Displayed: "
                        + HumanReadables.describe(view));
            }
        };
    }

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
