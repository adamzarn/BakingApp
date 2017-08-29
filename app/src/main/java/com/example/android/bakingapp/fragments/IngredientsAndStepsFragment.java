package com.example.android.bakingapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.adapters.StepsAdapter;
import com.example.android.bakingapp.objects.Ingredient;
import com.example.android.bakingapp.objects.Recipe;
import com.example.android.bakingapp.objects.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by adamzarn on 8/17/17.
 */

public class IngredientsAndStepsFragment extends Fragment {

    @BindView(R.id.scroll_view)
    ScrollView scrollView;

    @BindView(R.id.ingredients_header)
    TextView ingredientsHeader;

    @BindView(R.id.steps_header)
    TextView stepsHeader;

    @BindView(R.id.ingredients_text_view)
    TextView ingredientsTextView;

    @BindView(R.id.steps_recycler_view)
    RecyclerView stepsRecyclerView;

    public IngredientsAndStepsFragment() {
    }

    private Recipe selectedRecipe;

    OnStepClickListener mCallback;

    public interface OnStepClickListener {
        void onStepSelected(int position, ArrayList<Step> steps);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnStepClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.ingredients_and_steps_fragment, container, false);
        ButterKnife.bind(this, rootView);

        if (savedInstanceState == null) {
            selectedRecipe = this.getArguments().getParcelable("recipe");
        } else {
            selectedRecipe = savedInstanceState.getParcelable("recipe");
        }

        ingredientsHeader.setText(getResources().getString(R.string.ingredients_header));
        stepsHeader.setText(getResources().getString(R.string.steps_header));

        String ingredientsText = "";
        Ingredient[] ingredientsList = selectedRecipe.getIngredients();

        int i = 0;
        for (Ingredient ingredient : ingredientsList) {
            ingredientsText = ingredientsText + ingredient.getQuantity() + " " + ingredient.getMeasure() + " " + ingredient.getItem();
            if (i != ingredientsList.length - 1) {
                ingredientsText = ingredientsText + "\n";
            }
            i = i + 1;
        }
        ingredientsTextView.setText(ingredientsText);

        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        StepsAdapter stepsAdapter = new StepsAdapter();
        stepsRecyclerView.setAdapter(stepsAdapter);

        stepsAdapter.setOnItemClickListener(new StepsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ArrayList<Step> steps) {
                mCallback.onStepSelected(position, steps);
            }
        });

        stepsAdapter.setData(selectedRecipe.getSteps());

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                scrollView.scrollTo(0, 0);
            }
        });

        return rootView;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("recipe", selectedRecipe);
    }
}

