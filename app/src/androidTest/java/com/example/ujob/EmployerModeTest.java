package com.example.ujob;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
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
public class EmployerModeTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);


    @Test
    //User-Modes (User Story 3) #11: Scenario 1
    public void createJob(){
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
        onView(withId(R.id.CreateJob_View)).perform(closeSoftKeyboard()); // - changed - some devices this test will fail if keyboard not closed
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

    }

    @Test
    //User-Modes (User Story 3) #11: Scenario 2
    public void createJobNoInfo(){
        onView(withId(R.id.email)).perform(typeText("test@gmail.com"));
        onView(withId(R.id.password)).perform(typeText("123456"));
        onView(withId(R.id.main_activity_view)).perform(closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        SystemClock.sleep(1500);
        onView(withId(R.id.user_mode_view)).check(matches(isDisplayed()));
        onView(withId(R.id.user_mode_employer_button)).perform(click());
        onView(withId(R.id.EmployerMode_view)).check(matches(isDisplayed()));
        onView(withId(R.id.create_Job_Button)).perform(click());
        onView(withId(R.id.post_job_button)).perform(click());
        onView(withId(R.id.CreateJob_View)).check(matches(isDisplayed()));

    }

    @Test
    //Addition test to test the edit job button, doesn't correspond with any scenario
    public void editJob(){
        onView(withId(R.id.email)).perform(typeText("test@gmail.com"));
        onView(withId(R.id.password)).perform(typeText("123456"));
        onView(withId(R.id.main_activity_view)).perform(closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        SystemClock.sleep(1500);
        onView(withId(R.id.user_mode_view)).check(matches(isDisplayed()));
        onView(withId(R.id.user_mode_employer_button)).perform(click());
        onView(withId(R.id.EmployerMode_view)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_job_button)).perform(click());
        onView(withId(R.id.EditJob_View)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_job_desc_text)).perform(clearText(), typeText("Create Unit Tests"));
        onView(withId(R.id.EditJob_View)).perform(closeSoftKeyboard());
        onView(withId(R.id.edit_payment_text)).perform(clearText(), typeText("30"));
        onView(withId(R.id.EditJob_View)).perform(closeSoftKeyboard());
        onView(withId(R.id.finish_edit_button)).perform(click());
        SystemClock.sleep(1500);
        onView(withId(R.id.EmployerMode_view)).check(matches(isDisplayed()));

    }

    @Test
    //User-Modes (User Story 3) #11: Scenario 3
    public void deleteJob() {
        onView(withId(R.id.email)).perform(typeText("test@gmail.com"));
        onView(withId(R.id.password)).perform(typeText("123456"));
        onView(withId(R.id.main_activity_view)).perform(closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        SystemClock.sleep(1500);
        onView(withId(R.id.user_mode_view)).check(matches(isDisplayed()));
        onView(withId(R.id.user_mode_employer_button)).perform(click());
        onView(withId(R.id.EmployerMode_view)).check(matches(isDisplayed()));
        onView(withId(R.id.delete_job_button)).perform(click());
        SystemClock.sleep(1500);
        onView(withId(R.id.EmployerMode_view)).check(matches(isDisplayed()));

    }
}
