package com.example.android.bakingapp.activities;

import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.fragments.StepDetailFragment;
import com.example.android.bakingapp.objects.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by adamzarn on 8/24/17.
 */

public class DetailActivity extends AppCompatActivity implements StepDetailFragment.OnButtonClickListener {

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final String TAG = "PlayerActivity";

    private SimpleExoPlayer player;
    private SimpleExoPlayerView playerView;

    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;
    private String currentVideoURL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String currentRecipe = getIntent().getExtras().getString("Recipe");
        int currentStep = getIntent().getExtras().getInt("Step");
        ArrayList<Step> steps = getIntent().getExtras().getParcelableArrayList("Steps");

        if (findViewById(R.id.step_detail_container) == null) {

            playerView = (SimpleExoPlayerView) findViewById(R.id.video_view);
            currentVideoURL = steps.get(currentStep).getVideoURL();

        } else {

            final Toolbar toolbar = (Toolbar) findViewById(R.id.step_detail_toolbar);
            toolbar.setTitle(currentRecipe);
            AppCompatActivity appCompatActivity = (AppCompatActivity) this;
            appCompatActivity.setSupportActionBar(toolbar);
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            Bundle stepsBundle = new Bundle();
            stepsBundle.putString("Recipe", currentRecipe);
            stepsBundle.putInt("Step", currentStep);
            stepsBundle.putParcelableArrayList("Steps", steps);
            StepDetailFragment stepDetailFragment = new StepDetailFragment();
            stepDetailFragment.setArguments(stepsBundle);
            FragmentManager fragmentManager = getSupportFragmentManager();

            if (savedInstanceState == null) {
                playbackPosition = 0;
                fragmentManager.beginTransaction()
                        .add(R.id.step_detail_container, stepDetailFragment)
                        .commit();
            } else {
                playbackPosition = savedInstanceState.getLong("playbackPosition");
                System.out.println("Here I am: " + playbackPosition);
                fragmentManager.beginTransaction()
                        .replace(R.id.step_detail_container, stepDetailFragment)
                        .commit();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onButtonSelected(View v, int position, ArrayList<Step> steps) {
        int newPosition = 0;
        switch (v.getId()) {
            case R.id.previous_step_button:
                if (position == 0) {
                    newPosition = steps.size() - 1;
                } else {
                    newPosition = position - 1;
                }
                break;
            case R.id.next_step_button:
                if (position == steps.size() - 1) {
                    newPosition = 0;
                } else {
                    newPosition = position + 1;
                }
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle stepsBundle = new Bundle();
        stepsBundle.putInt("Step", newPosition);
        stepsBundle.putParcelableArrayList("Steps", steps);
        StepDetailFragment newStepDetailFragment = new StepDetailFragment();
        newStepDetailFragment.setArguments(stepsBundle);
        fragmentManager.beginTransaction()
                .replace(R.id.step_detail_container, newStepDetailFragment)
                .commit();
    }

    private void initializePlayer(String videoURL) {
        playerView.setVisibility(VISIBLE);

        if (player == null) {
            TrackSelection.Factory adaptiveTrackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);

            player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this),
                    new DefaultTrackSelector(adaptiveTrackSelectionFactory), new DefaultLoadControl());

            playerView.setPlayer(player);
            player.setPlayWhenReady(false);
            player.seekTo(currentWindow, playbackPosition);

        }

        Uri uri = Uri.parse(videoURL);
        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource, true, false);

    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory("ua"),
                new DefaultExtractorsFactory(), null, null);
    }

    private void hideSystemUi() {
        if (player != null) {
            playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            if (currentVideoURL.equals("")) {
                if (player != null) {
                    playerView.setVisibility(GONE);
                    releasePlayer();
                }
            } else {
                initializePlayer(currentVideoURL);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT <= 23 || player == null)) {
            if (currentVideoURL.equals("")) {
                if (player != null) {
                    playerView.setVisibility(GONE);
                    releasePlayer();
                }
            } else {
                initializePlayer(currentVideoURL);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if (player != null) {
            outState.putLong("playbackPosition", player.getCurrentPosition());
        } else {
            outState.putLong("playbackPosition", 0);
        }
    }
}
