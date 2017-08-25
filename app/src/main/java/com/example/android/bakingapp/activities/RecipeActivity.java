package com.example.android.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.fragments.IngredientsAndStepsFragment;
import com.example.android.bakingapp.objects.Recipe;
import com.example.android.bakingapp.objects.Step;

import java.util.ArrayList;

public class RecipeActivity extends AppCompatActivity implements IngredientsAndStepsFragment.OnStepClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        Recipe selectedRecipe = getIntent().getExtras().getParcelable("recipe");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(selectedRecipe.getName());

        Bundle recipeBundle = new Bundle();
        recipeBundle.putParcelable("recipe", selectedRecipe);
        IngredientsAndStepsFragment recipeStepsFragment = new IngredientsAndStepsFragment();
        recipeStepsFragment.setArguments(recipeBundle);
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.ingredients_and_steps_container, recipeStepsFragment)
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

    @Override
    public void onStepSelected(int position, ArrayList<Step> steps) {
        Class stepDetailActivity = DetailActivity.class;
        Intent intent = new Intent(getApplicationContext(), stepDetailActivity);

        intent.putExtra("Step", position);
        intent.putParcelableArrayListExtra("Steps", steps);

        startActivity(intent);
    }
}
