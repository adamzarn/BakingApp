package com.example.android.bakingapp.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.fragments.StepDetailFragment;
import com.example.android.bakingapp.objects.Step;

import java.util.ArrayList;

/**
 * Created by adamzarn on 8/24/17.
 */

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setHomeButtonEnabled(true);

        int currentStep = getIntent().getExtras().getInt("Step");
        ArrayList<Step> steps = getIntent().getExtras().getParcelableArrayList("Steps");

        getSupportActionBar().setTitle(steps.get(currentStep).getShortDescription());

        Bundle stepsBundle = new Bundle();
        stepsBundle.putInt("Step", currentStep);
        stepsBundle.putParcelableArrayList("Steps", steps);
        StepDetailFragment stepDetailFragment = new StepDetailFragment();
        stepDetailFragment.setArguments(stepsBundle);
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.step_detail_container, stepDetailFragment)
                .commit();

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



}
