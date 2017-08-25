package com.example.android.bakingapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.objects.Ingredient;
import com.example.android.bakingapp.R;

/**
 * Created by adamzarn on 8/18/17.
 */

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

    private Ingredient[] mIngredients = null;

    public void setData(Ingredient[] ingredients) {
        mIngredients = ingredients;
        notifyDataSetChanged();
    }

    @Override
    public IngredientsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ingredient_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(IngredientsAdapter.ViewHolder viewHolder, int i) {
        viewHolder.ingredientItem.setText(mIngredients[i].getQuantity() + " " + mIngredients[i].getMeasure() + " " + mIngredients[i].getItem());
    }

    @Override
    public int getItemCount() {
        if (mIngredients != null) {
            return mIngredients.length;
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView ingredientItem;

        public ViewHolder(View view) {
            super(view);
            ingredientItem = (TextView) view.findViewById(R.id.ingredient_item);
        }

    }

}
