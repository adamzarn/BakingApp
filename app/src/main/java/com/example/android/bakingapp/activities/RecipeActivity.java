package com.example.android.bakingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.android.bakingapp.DeviceUtils;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.fragments.IngredientsAndStepsFragment;
import com.example.android.bakingapp.fragments.StepDetailFragment;
import com.example.android.bakingapp.objects.Recipe;
import com.example.android.bakingapp.objects.Step;

import java.util.ArrayList;
import java.util.Arrays;

public class RecipeActivity extends AppCompatActivity implements IngredientsAndStepsFragment.OnStepClickListener, StepDetailFragment.OnButtonClickListener {

    private boolean mTwoPane;
    private String recipe;
    private Recipe selectedRecipe;
    private int currentStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        if (savedInstanceState == null) {
            selectedRecipe = getIntent().getExtras().getParcelable("recipe");
            currentStep = 0;
        } else {
            selectedRecipe = savedInstanceState.getParcelable("recipe");
            currentStep = savedInstanceState.getInt("currentStep");
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.ingredients_and_steps_toolbar);
        recipe = selectedRecipe.getName();
        toolbar.setTitle(recipe);
        AppCompatActivity appCompatActivity = (AppCompatActivity) this;
        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle recipeBundle = new Bundle();
        recipeBundle.putParcelable("recipe", selectedRecipe);
        IngredientsAndStepsFragment recipeStepsFragment = new IngredientsAndStepsFragment();
        recipeStepsFragment.setArguments(recipeBundle);

        FragmentManager fragmentManager = getSupportFragmentManager();

        Context context = getApplicationContext();
        if (savedInstanceState == null) {
            if (DeviceUtils.isTablet(context)) {
                mTwoPane = true;
                Bundle stepsBundle = new Bundle();
                stepsBundle.putInt("Step", currentStep);
                stepsBundle.putParcelableArrayList("Steps", new ArrayList<>(Arrays.asList(selectedRecipe.getSteps())));
                StepDetailFragment stepDetailFragment = new StepDetailFragment();
                stepDetailFragment.setArguments(stepsBundle);
                fragmentManager.beginTransaction()
                        .add(R.id.ingredients_and_steps_container, recipeStepsFragment)
                        .commit();
                fragmentManager.beginTransaction()
                        .add(R.id.step_detail_container, stepDetailFragment)
                        .commit();
            } else {
                mTwoPane = false;
                fragmentManager.beginTransaction()
                        .add(R.id.ingredients_and_steps_container, recipeStepsFragment)
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
    public void onStepSelected(int position, ArrayList<Step> steps) {
        if (mTwoPane) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Bundle stepsBundle = new Bundle();
            stepsBundle.putInt("Step", position);
            currentStep = position;
            stepsBundle.putParcelableArrayList("Steps", steps);
            StepDetailFragment newStepDetailFragment = new StepDetailFragment();
            newStepDetailFragment.setArguments(stepsBundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.step_detail_container, newStepDetailFragment)
                    .commit();
        } else {
            Class stepDetailActivity = DetailActivity.class;
            Intent intent = new Intent(getApplicationContext(), stepDetailActivity);

            intent.putExtra("Recipe", recipe);
            intent.putExtra("Step", position);
            intent.putParcelableArrayListExtra("Steps", steps);

            startActivity(intent);
        }
    }

    @Override
    public void onButtonSelected(View v, int position, ArrayList<Step> steps) {

        switch (v.getId()) {
            case R.id.previous_step_button:
                if (position == 0) {
                    currentStep = steps.size() - 1;
                } else {
                    currentStep = position - 1;
                }
                break;
            case R.id.next_step_button:
                if (position == steps.size() - 1) {
                    currentStep = 0;
                } else {
                    currentStep = position + 1;
                }
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        Bundle recipeBundle = new Bundle();
        recipeBundle.putParcelable("recipe", selectedRecipe);
        IngredientsAndStepsFragment recipeStepsFragment = new IngredientsAndStepsFragment();
        recipeStepsFragment.setArguments(recipeBundle);

        fragmentManager.beginTransaction()
                .replace(R.id.ingredients_and_steps_container, recipeStepsFragment)
                .commit();

        Bundle stepsBundle = new Bundle();
        stepsBundle.putInt("Step", currentStep);
        stepsBundle.putParcelableArrayList("Steps", steps);
        StepDetailFragment newStepDetailFragment = new StepDetailFragment();
        newStepDetailFragment.setArguments(stepsBundle);

        fragmentManager.beginTransaction()
                .replace(R.id.step_detail_container, newStepDetailFragment)
                .commit();

    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("recipe", selectedRecipe);
        outState.putInt("currentStep", currentStep);
    }

}

