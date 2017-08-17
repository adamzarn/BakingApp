package com.example.android.bakingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.AdapterOnClickHandler {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerAdapter recyclerAdapter;

    private static final String url = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new RecyclerAdapter(this);
        recyclerView.setAdapter(recyclerAdapter);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                JSONArray jsonArray = NetworkUtils.getJsonArray(response);
                JSONObject[] jsonObjectArray = NetworkUtils.getJsonObjectArray(jsonArray);
                Recipe[] recipes = NetworkUtils.convertToRecipes(jsonObjectArray);
                recyclerAdapter.setData(recipes);

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
        System.out.println(selectedRecipe.getName());
        System.out.println(selectedRecipe.getID());
        for (Ingredient ingredient: selectedRecipe.getIngredients()) {
            System.out.println(ingredient.getItem());
        }
        for (Step step: selectedRecipe.getSteps()) {
            System.out.println(step.getShortDescription());
        }
        System.out.println(selectedRecipe.getServings());
        System.out.println(selectedRecipe.getImage());
        System.out.println("");
    }
}
