package com.example.android.bakingapp.activities;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bakingapp.ConnectivityReceiver;
import com.example.android.bakingapp.DeviceUtils;
import com.example.android.bakingapp.NetworkUtils;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeWidgetProvider;
import com.example.android.bakingapp.fragments.IngredientsAndStepsFragment;
import com.example.android.bakingapp.fragments.StepDetailFragment;
import com.example.android.bakingapp.objects.Recipe;
import com.example.android.bakingapp.objects.Step;

import java.util.ArrayList;
import java.util.Arrays;

public class RecipeActivity extends AppCompatActivity implements IngredientsAndStepsFragment.OnStepClickListener, StepDetailFragment.OnButtonClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private boolean mTwoPane;
    private String recipe;
    private Recipe selectedRecipe;
    private int currentStep;
    private Bundle savedInstanceStateCopy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        savedInstanceStateCopy = savedInstanceState;

        if (savedInstanceState == null) {
            try {
                selectedRecipe = getIntent().getExtras().getParcelable(getResources().getString(R.string.recipe_key));
                recipe = selectedRecipe.getName();
                currentStep = 0;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            selectedRecipe = savedInstanceState.getParcelable(getResources().getString(R.string.recipe_key));
            recipe = selectedRecipe.getName();
            currentStep = savedInstanceState.getInt(getResources().getString(R.string.step_key));
        }

        if (selectedRecipe == null) {
            SharedPreferences preferences = getApplicationContext().getSharedPreferences(getString(R.string.my_preferences), Context.MODE_PRIVATE);
            recipe = preferences.getString(getResources().getString(R.string.widget_recipe_key), "");

            if (isConnected()) {
                NetworkUtils.getFavoriteRecipeData(recipe, new VolleyCallback() {
                    @Override
                    public void onSuccess(Recipe favoriteRecipe) {
                        selectedRecipe = favoriteRecipe;
                        setUpView(favoriteRecipe.getName());
                    }
                });
            } else {
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), "You are not connected to the internet. Please establish an internet connection.", Snackbar.LENGTH_INDEFINITE);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                snackbar.show();
            }

        } else {
            setUpView(recipe);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
    }

    private boolean isConnected() {
        return ConnectivityReceiver.isConnected();
    }

    public interface VolleyCallback {
        void onSuccess(Recipe favoriteRecipe);
    }

    public void setUpView(String recipe) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.ingredients_and_steps_toolbar);
        toolbar.setTitle(recipe);
        AppCompatActivity appCompatActivity = (AppCompatActivity) this;
        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle recipeBundle = new Bundle();
        recipeBundle.putParcelable(getResources().getString(R.string.recipe_key), selectedRecipe);
        IngredientsAndStepsFragment recipeStepsFragment = new IngredientsAndStepsFragment();
        recipeStepsFragment.setArguments(recipeBundle);

        FragmentManager fragmentManager = getSupportFragmentManager();

        Context context = getApplicationContext();
        if (savedInstanceStateCopy == null) {
            if (DeviceUtils.isTablet(context)) {
                mTwoPane = true;
                Bundle stepsBundle = new Bundle();
                stepsBundle.putInt(getResources().getString(R.string.step_key), currentStep);
                stepsBundle.putParcelableArrayList(getResources().getString(R.string.steps_key), new ArrayList<>(Arrays.asList(selectedRecipe.getSteps())));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recipe_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.set_widget:
                SharedPreferences preferences = getApplicationContext().getSharedPreferences(getString(R.string.my_preferences), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(getResources().getString(R.string.widget_recipe_key), recipe);
                editor.apply();
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_message) + " " + recipe, Toast.LENGTH_LONG);
                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                if (v != null) v.setGravity(Gravity.CENTER);
                toast.show();
                updateWidget();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateWidget() {
        Intent intent = new Intent(this, RecipeWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), RecipeWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }

    @Override
    public void onStepSelected(int position, ArrayList<Step> steps) {
        if (mTwoPane) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Bundle stepsBundle = new Bundle();
            stepsBundle.putInt(getResources().getString(R.string.step_key), position);
            currentStep = position;
            stepsBundle.putParcelableArrayList(getResources().getString(R.string.steps_key), steps);
            StepDetailFragment newStepDetailFragment = new StepDetailFragment();
            newStepDetailFragment.setArguments(stepsBundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.step_detail_container, newStepDetailFragment)
                    .commit();
        } else {
            Class stepDetailActivity = DetailActivity.class;
            Intent intent = new Intent(getApplicationContext(), stepDetailActivity);

            intent.putExtra(getResources().getString(R.string.recipe_key), recipe);
            intent.putExtra(getResources().getString(R.string.step_key), position);
            intent.putParcelableArrayListExtra(getResources().getString(R.string.steps_key), steps);

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
        recipeBundle.putParcelable(getResources().getString(R.string.recipe_key), selectedRecipe);
        IngredientsAndStepsFragment recipeStepsFragment = new IngredientsAndStepsFragment();
        recipeStepsFragment.setArguments(recipeBundle);

        fragmentManager.beginTransaction()
                .replace(R.id.ingredients_and_steps_container, recipeStepsFragment)
                .commit();

        Bundle stepsBundle = new Bundle();
        stepsBundle.putInt(getResources().getString(R.string.step_key), currentStep);
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
        outState.putParcelable(getResources().getString(R.string.recipe_key), selectedRecipe);
        outState.putInt(getResources().getString(R.string.step_key), currentStep);
    }

}

