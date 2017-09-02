package com.example.android.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.android.bakingapp.activities.MainActivity;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by adamzarn on 9/2/17.
 */

@RunWith(AndroidJUnit4.class)
public class RecipeCountTest {

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule =
            new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void registerIdlingResources() {
        Espresso.registerIdlingResources(mMainActivityTestRule.getActivity().getIdlingResource());
    }

    @Test
    public void checkRecipeCount() {
        Espresso.onView(withId(R.id.recipes_recycler_view)).check(new RecyclerViewItemCountAssertion(4));
    }

    @After
    public void unregisterIdlingResources() {
        Espresso.unregisterIdlingResources(mMainActivityTestRule.getActivity().getIdlingResource());
    }

}

//Class taken from Stack Overflow user Stéphane - "https://stackoverflow.com/questions/36399787/how-to-count-recyclerview-items-with-espresso"
class RecyclerViewItemCountAssertion implements ViewAssertion {

    private final Matcher<Integer> matcher;

    //Constructor
    public RecyclerViewItemCountAssertion(Matcher<Integer> matcher) {
        this.matcher = matcher;
    }

    //Sets the Assertion's matcher to the expected count passed in.
    public RecyclerViewItemCountAssertion(int expectedCount) {
        this.matcher = is(expectedCount);
    }

    //Check method that gets the passed in Recycler View's adapter and
    //its item count and checks it against the expected count (i.e. the matcher).
    @Override
    public void check(View view, NoMatchingViewException noViewFoundException) {
        if (noViewFoundException != null) {
            throw noViewFoundException;
        }

        RecyclerView recyclerView = (RecyclerView) view;
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        assertThat(adapter.getItemCount(), matcher);
    }

}
