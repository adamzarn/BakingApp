package com.example.android.bakingapp.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.android.bakingapp.BakingApplication;
import com.example.android.bakingapp.DeviceUtils;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.VISIBLE;
import static com.example.android.bakingapp.ConnectivityReceiver.isConnected;

/**
 * Created by adamzarn on 8/18/17.
 */

public class StepDetailFragment extends Fragment {

    @BindView(R.id.step_detail_title)
    TextView stepDetailTitle;

    @BindView(R.id.thumbnail_view)
    ImageView thumbnailView;

    @BindView(R.id.simple_video_view)
    SimpleExoPlayerView playerView;

    @BindView(R.id.no_media_available)
    TextView noMediaAvailableTextView;

    @BindView(R.id.description_text_view)
    TextView descriptionTextView;

    @BindView(R.id.next_step_button)
    Button nextStepButton;

    @BindView(R.id.previous_step_button)
    Button previousStepButton;

    @OnClick({R.id.next_step_button, R.id.previous_step_button})
    public void onNextStepButtonClick(View v) {
        mCallback.onButtonSelected(v, position, steps);
    }

    public StepDetailFragment() {
    }

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    private SimpleExoPlayer player;

    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;
    private String currentVideoURL;
    private String currentThumbnailURL;

    int position;
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
            throw new ClassCastException(context.toString() + " " + getActivity().getResources().getString(R.string.on_button_exception));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.step_detail_fragment, container, false);
        ButterKnife.bind(this, rootView);

        position = this.getArguments().getInt(getActivity().getResources().getString(R.string.step_key));
        steps = this.getArguments().getParcelableArrayList(getActivity().getResources().getString(R.string.steps_key));
        Step currentStep = steps.get(position);

        stepDetailTitle.setText(currentStep.getShortDescription());
        descriptionTextView.setText(currentStep.getDescription());
        currentVideoURL = currentStep.getVideoURL();
        currentThumbnailURL = currentStep.getThumbnailURL();

        if (savedInstanceState != null) {
            playbackPosition = savedInstanceState.getLong(getActivity().getResources().getString(R.string.playback_position_key));
        } else {
            playbackPosition = 0;
        }

        Context context = getActivity().getApplicationContext();
        if (DeviceUtils.isLandscape(context) && !DeviceUtils.isTablet(context)) {
            stepDetailTitle.setVisibility(View.GONE);
            descriptionTextView.setVisibility(View.GONE);
            nextStepButton.setVisibility(View.GONE);
            previousStepButton.setVisibility(View.GONE);
        }

        noMediaAvailableTextView.setVisibility(View.VISIBLE);
        thumbnailView.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(currentVideoURL)) {
            if (TextUtils.isEmpty(currentThumbnailURL)) {
                thumbnailView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.no_media_available));
                noMediaAvailableTextView.setVisibility(View.GONE);
            } else {
                if (isConnected()) {
                    ImageRequest imageRequest = new ImageRequest(currentThumbnailURL, new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            try {
                                thumbnailView.setImageBitmap(response);
                                noMediaAvailableTextView.setVisibility(View.GONE);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 0, 0, ImageView.ScaleType.CENTER_CROP, null, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            try {
                                thumbnailView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.no_media_available));
                                noMediaAvailableTextView.setVisibility(View.GONE);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    BakingApplication.getInstance().addToRequestQueue(imageRequest);
                } else {
                    thumbnailView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.no_media_available));
                    noMediaAvailableTextView.setVisibility(View.GONE);
                }
            }
        } else {
            noMediaAvailableTextView.setVisibility(View.GONE);
            thumbnailView.setVisibility(View.GONE);
        }

        return rootView;

    }

    private void initializePlayer(String videoURL) {
        playerView.setVisibility(VISIBLE);

        if (player == null) {
            TrackSelection.Factory adaptiveTrackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);

            player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getActivity()),
                    new DefaultTrackSelector(adaptiveTrackSelectionFactory), new DefaultLoadControl());

        }

        Uri uri = Uri.parse(videoURL);
        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource, true, false);
        playerView.setPlayer(player);
        if (playbackPosition > 0) {
            player.setPlayWhenReady(true);
        } else {
            player.setPlayWhenReady(false);
        }
        player.seekTo(currentWindow, playbackPosition);

    }

    private void releasePlayer() {
        if (player != null) {
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
            if (TextUtils.isEmpty(currentVideoURL)) {
                playerView.setVisibility(View.GONE);
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
            if (TextUtils.isEmpty(currentVideoURL)) {
                playerView.setVisibility(View.GONE);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (player != null) {
            outState.putLong(getActivity().getResources().getString(R.string.playback_position_key), player.getCurrentPosition());
        } else {
            outState.putLong(getActivity().getResources().getString(R.string.playback_position_key), 0);
        }
    }

}
