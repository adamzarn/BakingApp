package com.example.android.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.bakingapp.activities.DetailActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by adamzarn on 8/28/17.
 */

@RunWith(AndroidJUnit4.class)
public class StepNavigationTest {

    @Rule
    public ActivityTestRule<DetailActivity> mDetailActivityTestRule =
            new ActivityTestRule<DetailActivity>(DetailActivity.class);

    @Test
    public void nextButtonIncrementsPosition() {
        Espresso.onView(withId(R.id.next_step_button))
                .perform(click());

    }

    @Test
    public void previousButtonDecrementsPosition() {
        Espresso.onView(withId(R.id.next_step_button))
                .perform(click());
        Espresso.onView(withId(R.id.step_detail_title)).check(matches((isDisplayed())));
        Espresso.onView(withId(R.id.description_text_view)).check(matches((isDisplayed())));
    }

}
