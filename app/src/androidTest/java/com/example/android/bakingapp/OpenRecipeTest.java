package com.example.android.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;

import com.example.android.bakingapp.activities.MainActivity;
import com.example.android.bakingapp.activities.RecipeActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.is;

/**
 * Created by adamzarn on 8/28/17.
 */

@RunWith(AndroidJUnit4.class)
public class OpenRecipeTest {

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule =
            new ActivityTestRule<MainActivity>(MainActivity.class);

    //This test will select Nutella Pie in the list of recipes and check
    //that the recipe activity is opened and that Nutella Pie is set as
    //the title of the toolbar.

    @Before
    public void registerIdlingResources() {
        Espresso.registerIdlingResources(mMainActivityTestRule.getActivity().getIdlingResource());
    }

    @Test
    public void recipeOpensProperActivity() {
        Intents.init();
        onView(withId(R.id.recipes_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        intended(hasComponent(RecipeActivity.class.getName()));
        matchToolbarTitle("Nutella Pie");
        Intents.release();
    }

    @After
    public void unregisterIdlingResources() {
        Espresso.unregisterIdlingResources(mMainActivityTestRule.getActivity().getIdlingResource());
    }

    //These methods were taken from Chiu-Ki Chan who gave a presentation on
    //Advanced Android Espresso. These offer a robust way of accessing any
    //toolbar title.

    private static ViewInteraction matchToolbarTitle(CharSequence title) {
        return onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarTitle(is(title))));
    }

    private static Matcher<Object> withToolbarTitle(final Matcher<CharSequence> textMatcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override
            public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
    }

}
