package com.example.android.bakingapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by adamzarn on 8/15/17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private Context context;
    private Recipe[] myRecipes = null;

    public RecyclerAdapter(Context c, AdapterOnClickHandler myClickHandler) {
        context = c;
        this.myClickHandler = myClickHandler;
    }

    void setData(Recipe[] recipes) {
        myRecipes = recipes;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder viewHolder, int i) {
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
            myClickHandler.onClick(selectedRecipe);
        }
    }

    private final AdapterOnClickHandler myClickHandler;

    public interface AdapterOnClickHandler {
        void onClick(Recipe selectedRecipe);
    }

    public RecyclerAdapter(AdapterOnClickHandler clickHandler) {
        myClickHandler = clickHandler;
    }

}
