package com.example.android.bakingapp;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.android.bakingapp.activities.RecipeActivity;
import com.example.android.bakingapp.objects.Ingredient;
import com.example.android.bakingapp.objects.Recipe;
import com.example.android.bakingapp.objects.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.android.bakingapp.BakingApplication.getContext;

/**
 * Created by adamzarn on 8/16/17.
 */

public class NetworkUtils {

    public static JSONArray getJsonArray(String response) {
        JSONArray array = null;
        try {
            array = new JSONArray(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }

    public static JSONObject[] getJsonObjectArray(JSONArray array) {
        JSONObject[] jsonObjectArray = new JSONObject[array.length()];
        for (int i = 0; i < (array != null ? array.length() : 0); i++) {
            try {
                JSONObject jsonObject = array.getJSONObject(i);
                jsonObjectArray[i] = jsonObject;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObjectArray;
    }

    public static Recipe[] convertToRecipes(JSONObject[] jsonObjectArray) {
        Recipe[] recipes = new Recipe[jsonObjectArray.length];
        int i = 0;
        for (JSONObject recipe: jsonObjectArray) {
            try {
                String id = recipe.getString(getContext().getString(R.string.id));

                String name = recipe.getString(getContext().getString(R.string.name));

                JSONArray ingredientsData = recipe.getJSONArray(getContext().getString(R.string.ingredients));
                JSONObject[] ingredientObjects = getJsonObjectArray(ingredientsData);
                Ingredient[] ingredients = convertToIngredients(ingredientObjects);

                JSONArray stepsData = recipe.getJSONArray(getContext().getString(R.string.steps));
                JSONObject[] stepObjects = getJsonObjectArray(stepsData);
                Step[] steps = convertToSteps(stepObjects);

                String servings = recipe.getString(getContext().getString(R.string.servings));

                String image = recipe.getString(getContext().getString(R.string.image));

                Recipe newRecipe = new Recipe(id, name, ingredients, steps, servings, image);
                recipes[i] = newRecipe;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            i = i + 1;
        };
        return recipes;
    }

    public static Ingredient[] convertToIngredients(JSONObject[] jsonObjectArray) {
        Ingredient[] ingredients = new Ingredient[jsonObjectArray.length];
        int i = 0;
        for (JSONObject ingredient: jsonObjectArray) {
            try {
                String quantity = ingredient.getString(getContext().getString(R.string.quantity));
                String measure = ingredient.getString(getContext().getString(R.string.measure));
                String item = ingredient.getString(getContext().getString(R.string.ingredient));
                Ingredient newIngredient = new Ingredient(quantity, measure, item);
                ingredients[i] = newIngredient;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            i = i + 1;
        };
        return ingredients;
    }

    public static Step[] convertToSteps(JSONObject[] jsonObjectArray) {
        Step[] steps = new Step[jsonObjectArray.length];
        int i = 0;
        for (JSONObject step: jsonObjectArray) {
            try {
                String id = step.getString(getContext().getString(R.string.step_id));
                String shortDescription = step.getString(getContext().getString(R.string.shortDescription));
                String description = step.getString(getContext().getString(R.string.description));
                String videoURL = step.getString(getContext().getString(R.string.videoURL));
                String thumbnailURL = step.getString(getContext().getString(R.string.thumbnailURL));
                Step newStep = new Step(id, shortDescription, description, videoURL, thumbnailURL);
                steps[i] = newStep;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            i = i + 1;
        };
        return steps;
    }

    public static void getFavoriteRecipeData(final String recipe, final RecipeActivity.VolleyCallback callback) {
        String url = getContext().getResources().getString(R.string.baking_data_url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = NetworkUtils.getJsonArray(response);
                JSONObject[] jsonObjectArray = NetworkUtils.getJsonObjectArray(jsonArray);
                Recipe[] recipes = NetworkUtils.convertToRecipes(jsonObjectArray);
                if (TextUtils.isEmpty(recipe)) {
                    callback.onSuccess(recipes[0]);
                } else {
                    for (Recipe currentRecipe : recipes) {
                        if (currentRecipe.getName().equals(recipe)) {
                            callback.onSuccess(currentRecipe);
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        BakingApplication.getInstance().addToRequestQueue(stringRequest);
    }

    public static String getIngredientsString(Ingredient[] ingredients) {
        String ingredientsText = "";
        int i = 0;
        for (Ingredient ingredient : ingredients) {
            ingredientsText = ingredientsText + ingredient.getQuantity() + " " + ingredient.getMeasure() + " " + ingredient.getItem();
            if (i != ingredients.length - 1) {
                ingredientsText = ingredientsText + "\n";
            }
            i = i + 1;
        }
        return ingredientsText;
    }

}
