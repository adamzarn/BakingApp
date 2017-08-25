package com.example.android.bakingapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.objects.Recipe;

/**
 * Created by adamzarn on 8/15/17.
 */

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    private Recipe[] myRecipes = null;

    public void setData(Recipe[] recipes) {
        myRecipes = recipes;
        notifyDataSetChanged();
    }

    @Override
    public RecipesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecipesAdapter.ViewHolder viewHolder, int i) {
        viewHolder.recipeTitle.setText(myRecipes[i].getName());
    }

    @Override
    public int getItemCount() {
        if (myRecipes != null) {
            return myRecipes.length;
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView recipeTitle;

        public ViewHolder(View view) {
            super(view);
            recipeTitle = (TextView) view.findViewById(R.id.recipe_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Recipe selectedRecipe = myRecipes[position];
            mClickHandler.onClick(selectedRecipe);
        }
    }

    private final RecipesAdapterOnClickHandler mClickHandler;

    public interface RecipesAdapterOnClickHandler {
        void onClick(Recipe selectedRecipe);
    }

    public RecipesAdapter(RecipesAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

}
