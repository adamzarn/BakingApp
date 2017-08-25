package com.example.android.bakingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.android.bakingapp.BakingApplication;
import com.example.android.bakingapp.NetworkUtils;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.adapters.RecipesAdapter;
import com.example.android.bakingapp.objects.Recipe;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements RecipesAdapter.RecipesAdapterOnClickHandler {

    Context context;
    RecyclerView recipesRecyclerView;
    RecyclerView.LayoutManager portraitLayoutManager;
    RecyclerView.LayoutManager landLayoutManager;
    RecipesAdapter recipesAdapter;

    private static final String url = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        portraitLayoutManager = new LinearLayoutManager(this);
        landLayoutManager = new GridLayoutManager(this,3);

        if (findViewById(R.id.recipes_recycler_view) != null) {
            recipesRecyclerView = (RecyclerView) findViewById(R.id.recipes_recycler_view);
            recipesRecyclerView.setLayoutManager(portraitLayoutManager);
        } else if (findViewById(R.id.recipes_recycler_view_land) != null) {
            recipesRecyclerView = (RecyclerView) findViewById(R.id.recipes_recycler_view_land);
            recipesRecyclerView.setLayoutManager(landLayoutManager);
        } else {
            recipesRecyclerView = (RecyclerView) findViewById(R.id.recipes_recycler_view_tablet);
            recipesRecyclerView.setLayoutManager(landLayoutManager);
        }

        recipesAdapter = new RecipesAdapter(this);
        recipesRecyclerView.setAdapter(recipesAdapter);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                JSONArray jsonArray = NetworkUtils.getJsonArray(response);
                JSONObject[] jsonObjectArray = NetworkUtils.getJsonObjectArray(jsonArray);
                Recipe[] recipes = NetworkUtils.convertToRecipes(jsonObjectArray);
                recipesAdapter.setData(recipes);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
