package com.example.ujob;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


import android.os.SystemClock;
import android.view.View;
import android.widget.SeekBar;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.ujob.controllers.generalUser.MainActivity;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class WorkerModeTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test //User-Modes (2) Scenario-1
    public void onSliderUpdateJobsDisplayed(){
        login();
        onView(withId(R.id.user_mode_employee_button)).perform(click());
        onView(withId(R.id.worker_mode_view)).check(matches(isDisplayed()));
        SystemClock.sleep(1000);
        onView(withId(R.id.radius_seek_bar)).perform(setProgress(1));
        SystemClock.sleep(3000);
        onView(new RecyclerViewMatcher(R.id.listingRecyclerView).atPositionOnView(0,R.id.job_placeholder)).check(matches(isDisplayed()));
    }

    @Test //User-Modes (2) Scenario-2
    public void onNoJobsAvailable(){
        login();
        onView(withId(R.id.user_mode_employee_button)).perform(click());
        onView(withId(R.id.worker_mode_view)).check(matches(isDisplayed()));
        SystemClock.sleep(1000);
        onView(withId(R.id.radius_seek_bar)).perform(setProgress(0));
        SystemClock.sleep(3000);
        onView(withId(R.id.request_btn)).check(doesNotExist());
    }
    @Test //User-Modes (2) Scenario-3
    public void onNoSliderUpdateJobsDisplayed(){
        login();
        onView(withId(R.id.user_mode_employee_button)).perform(click());
        onView(withId(R.id.worker_mode_view)).check(matches(isDisplayed()));
        SystemClock.sleep(3000);
        onView(new RecyclerViewMatcher(R.id.listingRecyclerView).atPositionOnView(0,R.id.job_placeholder)).check(matches(isDisplayed()));
    }



    public void login() {
        onView(withId(R.id.email)).perform(typeText("test@gmail.com"));
        onView(withId(R.id.password)).perform(typeText("123456"));
        onView(withId(R.id.main_activity_view)).perform(closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        SystemClock.sleep(1500);
        onView(withId(R.id.user_mode_view)).check(matches(isDisplayed()));
    }

    public static ViewAction setProgress(final int progress) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                SeekBar seekBar = (SeekBar) view;
                seekBar.setProgress(progress);
            }
            @Override
            public String getDescription() {
                return "Set a progress on a SeekBar";
            }
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(SeekBar.class);
            }
        };
    }
}

