package com.example.android.bakingapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.android.bakingapp.BakingApplication;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.objects.Recipe;

/**
 * Created by adamzarn on 8/15/17.
 */

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    private Recipe[] myRecipes = null;
    private Context context;

    public void setData(Recipe[] recipes) {
        myRecipes = recipes;
        notifyDataSetChanged();
    }

    @Override
    public RecipesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        context = viewGroup.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecipesAdapter.ViewHolder viewHolder, int i) {
        viewHolder.recipeTitle.setText(myRecipes[i].getName());
        String servingsText = context.getResources().getString(R.string.serves) + myRecipes[i].getServings();
        viewHolder.servings.setText(servingsText);
        String url = myRecipes[i].getImage();

        if (!url.equals("")) {
            ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    viewHolder.imageView.setImageBitmap(response);
                }
            }, 0, 0, ImageView.ScaleType.CENTER_CROP, null, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    viewHolder.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.cupcake));
                }
            });
            BakingApplication.getInstance().addToRequestQueue(imageRequest);
        } else {
            viewHolder.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.cupcake));
        }
    }

    @Override
    public int getItemCount() {
        if (myRecipes != null) {
            return myRecipes.length;
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageView;
        public TextView recipeTitle;
        public TextView servings;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.image_view);
            recipeTitle = (TextView) view.findViewById(R.id.recipe_title);
            servings = (TextView) view.findViewById(R.id.servings);
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
