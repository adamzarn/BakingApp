package com.example.android.bakingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.android.bakingapp.BakingApplication;
import com.example.android.bakingapp.ConnectivityReceiver;
import com.example.android.bakingapp.DeviceUtils;
import com.example.android.bakingapp.NetworkUtils;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.adapters.RecipesAdapter;
import com.example.android.bakingapp.objects.Recipe;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.bakingapp.BakingApplication.getContext;

public class MainActivity extends AppCompatActivity implements RecipesAdapter.RecipesAdapterOnClickHandler, ConnectivityReceiver.ConnectivityReceiverListener {

    @BindView(R.id.recipes_recycler_view)
    RecyclerView recipesRecyclerView;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    CountingIdlingResource mIdlingResource = new CountingIdlingResource(getContext().getResources().getString(R.string.resource));

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
        toolbar.setTitle(getResources().getString(R.string.main_activity_title));
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

        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable("recycler_layout");
            recipesRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }

        recipesAdapter = new RecipesAdapter(this);
        recipesRecyclerView.setAdapter(recipesAdapter);

        getRecipes(isConnected());

    }

    private void getRecipes(boolean isConnected) {
        mIdlingResource.increment();
        progressBar.setVisibility(View.VISIBLE);
        if (isConnected) {
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
                    mIdlingResource.decrement();
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(context, "Could not get recipes.", Toast.LENGTH_LONG);
                    error.printStackTrace();
                }
            });
            BakingApplication.getInstance().addToRequestQueue(stringRequest);
        } else {
            mIdlingResource.decrement();
            progressBar.setVisibility(View.INVISIBLE);
            Snackbar snackbar = Snackbar
                    .make(findViewById(android.R.id.content), "You are not connected to the internet. Please establish an internet connection.", Snackbar.LENGTH_INDEFINITE);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            snackbar.show();
        }
    }

    @Override
    public void onClick(Recipe selectedRecipe) {
        Class recipeActivity = RecipeActivity.class;
        context = getApplicationContext();
        Intent intent = new Intent(context, recipeActivity);

        intent.putExtra(getResources().getString(R.string.recipe_key), selectedRecipe);

        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("recycler_layout", recipesRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onResume() {
        super.onResume();
        BakingApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BakingApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        getRecipes(isConnected);
    }

    private boolean isConnected() {
        return ConnectivityReceiver.isConnected();
    }

}
