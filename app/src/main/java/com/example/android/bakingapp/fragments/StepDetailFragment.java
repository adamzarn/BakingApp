package com.example.android.bakingapp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.bakingapp.R;
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
 * Created by adamzarn on 8/18/17.
 */

public class StepDetailFragment extends Fragment {

    public StepDetailFragment() {
    }

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final String TAG = "PlayerActivity";

    private SimpleExoPlayer player;
    private SimpleExoPlayerView playerView;

    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;
    private String currentVideoURL;

    int currentStep;
    ArrayList<Step> steps;

    OnButtonClickListener mCallback;

    public interface OnButtonClickListener {
        void onButtonSelected(View v, int position, ArrayList<Step> steps);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnButtonClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        currentStep = this.getArguments().getInt("Step");
        steps = this.getArguments().getParcelableArrayList("Steps");
        Step step = steps.get(currentStep);

        View rootView = inflater.inflate(R.layout.step_detail_fragment, container, false);

        TextView shortDescription = (TextView) rootView.findViewById(R.id.step_detail_title);
        shortDescription.setText(step.getShortDescription());

        TextView description = (TextView) rootView.findViewById(R.id.description_text_view);
        description.setText(step.getDescription());

        Button nextStepButton = (Button) rootView.findViewById(R.id.next_step_button);
        Button previousStepButton = (Button) rootView.findViewById(R.id.previous_step_button);

        nextStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onButtonSelected(v, currentStep, steps);
            }
        });

        previousStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onButtonSelected(v, currentStep, steps);
            }
        });

        playerView = (SimpleExoPlayerView) rootView.findViewById(R.id.video_view);
        currentVideoURL = step.getVideoURL();

        return rootView;

    }

    private void initializePlayer(String videoURL) {
        playerView.setVisibility(VISIBLE);

        if (player == null) {
            TrackSelection.Factory adaptiveTrackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);

            player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getActivity()),
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
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            if (currentVideoURL.equals("")) {
                playerView.setVisibility(GONE);
                releasePlayer();
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
                playerView.setVisibility(GONE);
                releasePlayer();
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

}
