package com.example.android.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.example.android.bakingapp.activities.RecipeActivity;
import com.example.android.bakingapp.objects.Recipe;

import static com.example.android.bakingapp.BakingApplication.getContext;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget_provider);

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getResources().getString(R.string.my_preferences), Context.MODE_PRIVATE);
        String recipe = preferences.getString(getContext().getResources().getString(R.string.widget_recipe_key), "");
        NetworkUtils.getFavoriteRecipeData(recipe, new RecipeActivity.VolleyCallback() {
            @Override
            public void onSuccess(Recipe favoriteRecipe) {
                views.setTextViewText(R.id.widget_recipe_title, favoriteRecipe.getName());
                views.setTextViewText(R.id.widget_ingredients_list, NetworkUtils.getIngredientsString(favoriteRecipe.getIngredients()));
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        });

        Intent intent = new Intent(context, RecipeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        views.setOnClickPendingIntent(R.id.widget_view, pendingIntent);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

