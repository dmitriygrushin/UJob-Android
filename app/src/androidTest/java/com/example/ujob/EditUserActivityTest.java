package com.example.ujob;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;


import com.example.ujob.controllers.generalUser.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import android.os.SystemClock;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class EditUserActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test // User Profiles (1) - Scenario 1
    public void customizeProfile() {
        login();

        // get in EditUserActivity
        onView(withId(R.id.current_user_edit_profile_button)).perform(click());
        SystemClock.sleep(1500);
        onView(withId(R.id.edit_user_activity_view)).check(matches(isDisplayed()));

        // clear pre-existing text in fields before adding new text in fields
        clearCustomizeFields();

        // fill fields with text and submit
        onView(withId(R.id.user_skill)).perform(typeText("Wizard"));
        onView(withId(R.id.user_years_of_experience)).perform(typeText("500"));
        onView(withId(R.id.user_postal_code)).perform(typeText("62655"));
        onView(withId(R.id.edit_user_activity_view)).perform(closeSoftKeyboard()); // need to close keyboard else about me can't be reached
        onView(withId(R.id.user_about_me)).perform(typeText("Hogwarts Grandmaster, Harri Potta ever heard of him?"));
        onView(withId(R.id.edit_user_activity_view)).perform(closeSoftKeyboard());
        onView(withId(R.id.edit_user_submit_button)).perform(click());
        SystemClock.sleep(1500);
        onView(withId(R.id.user_mode_view)).check(matches(isDisplayed()));
    }

    @Test // User Profiles (1) - Scenario 2
    public void customizeProfileEmpty() {
       login();

        // get in EditUserActivity
        onView(withId(R.id.current_user_edit_profile_button)).perform(click());
        SystemClock.sleep(1500);
        onView(withId(R.id.edit_user_activity_view)).check(matches(isDisplayed()));

        // clear pre-existing text in fields before adding new text in fields
        clearCustomizeFields();

        onView(withId(R.id.edit_user_submit_button)).perform(click());
        onView(withId(R.id.edit_user_activity_view)).check(matches(isDisplayed()));
    }

    @Test // User Profiles (1) - Scenario 3
    public void customizeProfileNothing() {
        login();
        // get in EditUserActivity
        onView(withId(R.id.current_user_edit_profile_button)).perform(click());
        SystemClock.sleep(1500);
        onView(withId(R.id.edit_user_activity_view)).check(matches(isDisplayed()));

        // perform click on the background
        onView(withId(R.id.edit_user_activity_view)).perform(click());
        SystemClock.sleep(2000);
        onView(withId(R.id.edit_user_activity_view)).check(matches(isDisplayed()));
    }

    public void login() {
        onView(withId(R.id.email)).perform(typeText("test@gmail.com"));
        onView(withId(R.id.password)).perform(typeText("123456"));
        onView(withId(R.id.main_activity_view)).perform(closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        SystemClock.sleep(1500);
        onView(withId(R.id.user_mode_view)).check(matches(isDisplayed()));
    }

    public void clearCustomizeFields() {
        onView(withId(R.id.user_skill)).perform(clearText());
        onView(withId(R.id.user_years_of_experience)).perform(clearText());
        onView(withId(R.id.user_postal_code)).perform(clearText());
        onView(withId(R.id.user_about_me)).perform(clearText());
    }
}