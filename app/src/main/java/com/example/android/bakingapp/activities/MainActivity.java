package com.example.android.bakingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.android.bakingapp.BakingApplication;
import com.example.android.bakingapp.DeviceUtils;
import com.example.android.bakingapp.NetworkUtils;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.adapters.RecipesAdapter;
import com.example.android.bakingapp.objects.Recipe;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RecipesAdapter.RecipesAdapterOnClickHandler {

    @BindView(R.id.recipes_recycler_view)
    RecyclerView recipesRecyclerView;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    CountingIdlingResource mIdlingResource = new CountingIdlingResource("DATA_LOADER");

    public IdlingResource getIdlingResource() {
        return mIdlingResource;
    }

    Context context;
    RecyclerView.LayoutManager portraitLayoutManager;
    RecyclerView.LayoutManager landLayoutManager;
    RecipesAdapter recipesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        toolbar.setTitle("Recipes");
        AppCompatActivity appCompatActivity = (AppCompatActivity) this;
        appCompatActivity.setSupportActionBar(toolbar);

        portraitLayoutManager = new LinearLayoutManager(this);
        landLayoutManager = new GridLayoutManager(this, 2);

        context = getApplicationContext();
        if (!DeviceUtils.isLandscape(context) && !DeviceUtils.isTablet(context)) {
            recipesRecyclerView.setLayoutManager(portraitLayoutManager);
        } else {
            recipesRecyclerView.setLayoutManager(landLayoutManager);
        }

        recipesAdapter = new RecipesAdapter(this);
        recipesRecyclerView.setAdapter(recipesAdapter);

        mIdlingResource.increment();
        progressBar.setVisibility(View.VISIBLE);
        String url = getApplicationContext().getResources().getString(R.string.baking_data_url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                JSONArray jsonArray = NetworkUtils.getJsonArray(response);
                JSONObject[] jsonObjectArray = NetworkUtils.getJsonObjectArray(jsonArray);
                Recipe[] recipes = NetworkUtils.convertToRecipes(jsonObjectArray);
                recipesAdapter.setData(recipes);
                mIdlingResource.decrement();
                progressBar.setVisibility(View.INVISIBLE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.INVISIBLE);
                System.out.println("Something went wrong.");
                error.printStackTrace();
            }
        });

        BakingApplication.getInstance().addToRequestQueue(stringRequest);

    }

    @Override
    public void onClick(Recipe selectedRecipe) {
        Class recipeActivity = RecipeActivity.class;
        context = getApplicationContext();
        Intent intent = new Intent(context, recipeActivity);

        intent.putExtra("recipe", selectedRecipe);

        startActivity(intent);
    }

}
