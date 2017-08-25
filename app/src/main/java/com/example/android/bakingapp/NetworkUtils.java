package com.example.android.bakingapp;

import com.example.android.bakingapp.objects.Ingredient;
import com.example.android.bakingapp.objects.Recipe;
import com.example.android.bakingapp.objects.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                String id = recipe.getString("id");

                String name = recipe.getString("name");

                JSONArray ingredientsData = recipe.getJSONArray("ingredients");
                JSONObject[] ingredientObjects = getJsonObjectArray(ingredientsData);
                Ingredient[] ingredients = convertToIngredients(ingredientObjects);

                JSONArray stepsData = recipe.getJSONArray("steps");
                JSONObject[] stepObjects = getJsonObjectArray(stepsData);
                Step[] steps = convertToSteps(stepObjects);

                String servings = recipe.getString("servings");

                String image = recipe.getString("image");

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
                String quantity = ingredient.getString("quantity");
                String measure = ingredient.getString("measure");
                String item = ingredient.getString("ingredient");
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
                String id = step.getString("id");
                String shortDescription = step.getString("shortDescription");
                String description = step.getString("description");
                String videoURL = step.getString("videoURL");
                String thumbnailURL = step.getString("thumbnailURL");
                Step newStep = new Step(id, shortDescription, description, videoURL, thumbnailURL);
                steps[i] = newStep;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            i = i + 1;
        };
        return steps;
    }

}
