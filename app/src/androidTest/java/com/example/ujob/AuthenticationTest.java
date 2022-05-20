package com.example.ujob;

import static androidx.test.espresso.Espresso.onView;
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

import java.util.UUID;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AuthenticationTest {

    // Generates random string so that no chance for duplicate information that could lead to an error.
    public String stringGen(){
        return UUID.randomUUID().toString().replaceAll("-","").substring(0,6);
    }

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test //Authentication 1 - Scenario 1
    public void registration(){
        onView(withId(R.id.main_register_button)).perform(click());
        onView(withId(R.id.register_view)).check(matches(isDisplayed()));
        onView(withId(R.id.register_first_name)).perform(typeText(stringGen()));
        onView(withId(R.id.register_last_name)).perform(typeText(stringGen()));
        onView(withId(R.id.register_email)).perform(typeText(stringGen()+"@gmail.com"));
        onView(withId(R.id.register_view)).perform(closeSoftKeyboard());
        onView(withId(R.id.register_password)).perform(typeText("123456"));
        onView(withId(R.id.register_view)).perform(closeSoftKeyboard());
        onView(withId(R.id.register_submit)).perform(click());
        SystemClock.sleep(1500);
        onView(withId(R.id.user_mode_view)).check(matches(isDisplayed()));
    }

    @Test //Authentication 1 - Scenario 2
    public void registerUsedEmail(){
        onView(withId(R.id.main_register_button)).perform(click());
        onView(withId(R.id.register_view)).check(matches(isDisplayed()));
        onView(withId(R.id.register_first_name)).perform(typeText(stringGen()));
        onView(withId(R.id.register_last_name)).perform(typeText(stringGen()));
        onView(withId(R.id.register_email)).perform(typeText("test@gmail.com")); // Already stored email
        onView(withId(R.id.register_view)).perform(closeSoftKeyboard());
        onView(withId(R.id.register_password)).perform(typeText("123456"));
        onView(withId(R.id.register_view)).perform(closeSoftKeyboard());
        onView(withId(R.id.register_submit)).perform(click());
        onView(withId(R.id.register_view)).check(matches(isDisplayed())); // Check that view did not change
    }

    @Test //Authentication 1 - Scenario 3
    public void registerWithoutInformation(){
        onView(withId(R.id.main_register_button)).perform(click());
        onView(withId(R.id.register_view)).check(matches(isDisplayed()));
        onView(withId(R.id.register_submit)).perform(click());
        onView(withId(R.id.register_view)).check(matches(isDisplayed())); // Check that view did not change
    }

    @Test //Authentication 2 - Scenario 1
    public void login(){
        onView(withId(R.id.email)).perform(typeText("test@gmail.com"));
        onView(withId(R.id.password)).perform(typeText("123456"));
        onView(withId(R.id.main_activity_view)).perform(closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        SystemClock.sleep(1500);
        onView(withId(R.id.user_mode_view)).check(matches(isDisplayed()));
    }

    @Test //Authentication 2 - Scenario 2
    public void badCredentialLogin(){
        onView(withId(R.id.email)).perform(typeText("bademail@gmail.com"));
        onView(withId(R.id.password)).perform(typeText("123456"));
        onView(withId(R.id.main_activity_view)).perform(closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.main_activity_view)).check(matches(isDisplayed()));
    }

    @Test //Authentication 2 - Scenario 3
    public void loginWithoutInformation(){
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.main_activity_view)).check(matches(isDisplayed()));
    }

}

