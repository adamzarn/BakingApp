package com.example.android.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.example.android.bakingapp.activities.MainActivity;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.stream.IntStream;

import static android.media.session.PlaybackState.STATE_BUFFERING;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.google.android.exoplayer2.ExoPlayer.STATE_IDLE;
import static com.google.android.exoplayer2.ExoPlayer.STATE_READY;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;

/**
 * Created by adamzarn on 9/2/17.
 */

@RunWith(AndroidJUnit4.class)
public class VideoPlaybackTest {

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule =
            new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void registerIdlingResources() {
        Espresso.registerIdlingResources(mMainActivityTestRule.getActivity().getIdlingResource());
    }

    @Test
    public void videoIsPlaying() {
        Intents.init();
        onView(withId(R.id.recipes_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.steps_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.exo_play))
                .perform(click());
        onView(allOf(
                withId(R.id.simple_video_view),
                withClassName(is(SimpleExoPlayerView.class.getName()))))
                .check(new VideoPlaybackAssertion(true));
        Intents.release();
    }

    @After
    public void unregisterIdlingResources() {
        Espresso.unregisterIdlingResources(mMainActivityTestRule.getActivity().getIdlingResource());
    }

}

class VideoPlaybackAssertion implements ViewAssertion {

    private final Matcher<Boolean> matcher;

    //Constructor
    public VideoPlaybackAssertion(Matcher<Boolean> matcher) {
        this.matcher = matcher;
    }

    //Sets the Assertion's matcher to the expected playbck state.
    public VideoPlaybackAssertion(Boolean expectedState) {
        this.matcher = is(expectedState);
    }

    //Method to check if the video is playing.
    @Override
    public void check(View view, NoMatchingViewException noViewFoundException) {
        if (noViewFoundException != null) {
            throw noViewFoundException;
        }

        SimpleExoPlayerView exoPlayerView = (SimpleExoPlayerView) view;
        SimpleExoPlayer exoPlayer = exoPlayerView.getPlayer();
        int state = exoPlayer.getPlaybackState();
        int acceptableStates[] = {1,2,3}; //Buffering, Preparing, or Ready
        Boolean isPlaying = false;
        for (int i : acceptableStates) {
            if (i == state) {
                isPlaying = true;
            }
        }
        assertThat(isPlaying, matcher);
    }

}
