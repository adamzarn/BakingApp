package com.example.android.bakingapp.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.android.bakingapp.DeviceUtils;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.fragments.StepDetailFragment;
import com.example.android.bakingapp.objects.Step;

import java.util.ArrayList;

/**
 * Created by adamzarn on 8/24/17.
 */

public class DetailActivity extends AppCompatActivity implements StepDetailFragment.OnButtonClickListener {

    String currentRecipe;
    int position;
    ArrayList<Step> steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            currentRecipe = getIntent().getExtras().getString(getResources().getString(R.string.recipe_key));
            position = getIntent().getExtras().getInt(getResources().getString(R.string.step_key));
            steps = getIntent().getExtras().getParcelableArrayList(getResources().getString(R.string.steps_key));
        } else {
            currentRecipe = savedInstanceState.getString(getResources().getString(R.string.recipe_key));
            position = savedInstanceState.getInt(getResources().getString(R.string.step_key));
            steps = savedInstanceState.getParcelableArrayList(getResources().getString(R.string.steps_key));
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.step_detail_toolbar);
        if (!DeviceUtils.isLandscape(getApplicationContext())) {
            toolbar.setTitle(currentRecipe);
            AppCompatActivity appCompatActivity = (AppCompatActivity) this;
            appCompatActivity.setSupportActionBar(toolbar);
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            toolbar.setVisibility(View.GONE);
        }

        Bundle stepsBundle = new Bundle();
        stepsBundle.putString(getResources().getString(R.string.recipe_key), currentRecipe);
        stepsBundle.putInt(getResources().getString(R.string.step_key), position);
        stepsBundle.putParcelableArrayList(getResources().getString(R.string.steps_key), steps);
        StepDetailFragment stepDetailFragment = new StepDetailFragment();
        stepDetailFragment.setArguments(stepsBundle);
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.step_detail_container, stepDetailFragment)
                    .commit();
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
        stepsBundle.putInt(getResources().getString(R.string.step_key), newPosition);
        this.position = newPosition;
        stepsBundle.putParcelableArrayList(getResources().getString(R.string.steps_key), steps);
        StepDetailFragment newStepDetailFragment = new StepDetailFragment();
        newStepDetailFragment.setArguments(stepsBundle);
        fragmentManager.beginTransaction()
                .replace(R.id.step_detail_container, newStepDetailFragment)
                .commit();
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getResources().getString(R.string.recipe_key), currentRecipe);
        outState.putInt(getResources().getString(R.string.step_key), position);
        outState.putParcelableArrayList(getResources().getString(R.string.steps_key), steps);
    }
}
