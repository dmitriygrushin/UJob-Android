package com.example.ujob;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.SystemClock;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.ujob.controllers.generalUser.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class WorkerModeEmployerProfileTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    //User Profiles (User Story 2) #7: Scenario 1
    public void employerProfileClicked() {
        onView(withId(R.id.email)).perform(typeText("test@gmail.com"));
        onView(withId(R.id.password)).perform(typeText("123456"));
        onView(withId(R.id.main_activity_view)).perform(closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        SystemClock.sleep(1500);
        onView(withId(R.id.user_mode_view)).check(matches(isDisplayed()));
        onView(withId(R.id.user_mode_employee_button)).perform(click());
        SystemClock.sleep(3000);
        onView(new RecyclerViewMatcher(R.id.listingRecyclerView)
                .atPositionOnView(0, R.id.CardElements)).perform(click());
        onView(withId(R.id.employer_profile_activity)).check(matches(isDisplayed()));
    }

    @Test
    //User Profiles (User Story 2) #7: Scenario 2
    public void employerProfileNotClicked() {
        onView(withId(R.id.email)).perform(typeText("test@gmail.com"));
        onView(withId(R.id.password)).perform(typeText("123456"));
        onView(withId(R.id.main_activity_view)).perform(closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        SystemClock.sleep(1500);
        onView(withId(R.id.user_mode_view)).check(matches(isDisplayed()));
        onView(withId(R.id.user_mode_employee_button)).perform(click());
        SystemClock.sleep(3000);
        onView(new RecyclerViewMatcher(R.id.listingRecyclerView)
                .atPositionOnView(0, R.id.profile_image_btn)).perform(click());
        onView(withId(R.id.worker_mode_view)).check(matches(isDisplayed()));
    }

    @Test
    //User Profiles (User Story 2) #7: Scenario 3
    public void employerProfileGoBack() {
        onView(withId(R.id.email)).perform(typeText("test@gmail.com"));
        onView(withId(R.id.password)).perform(typeText("123456"));
        onView(withId(R.id.main_activity_view)).perform(closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        SystemClock.sleep(1500);
        onView(withId(R.id.user_mode_view)).check(matches(isDisplayed()));
        onView(withId(R.id.user_mode_employee_button)).perform(click());
        SystemClock.sleep(3000);
        onView(new RecyclerViewMatcher(R.id.listingRecyclerView)
                .atPositionOnView(0, R.id.CardElements)).perform(click());
        onView(withId(R.id.employer_profile_activity)).check(matches(isDisplayed()));
        SystemClock.sleep(1500);
        onView(withId(R.id.go_back_to_job_listing_btn)).perform(click());
        onView(withId(R.id.worker_mode_view)).check(matches(isDisplayed()));
    }

}
