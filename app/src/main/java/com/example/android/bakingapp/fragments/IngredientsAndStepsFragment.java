package com.example.android.bakingapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.adapters.IngredientsAdapter;
import com.example.android.bakingapp.adapters.StepsAdapter;
import com.example.android.bakingapp.objects.Recipe;
import com.example.android.bakingapp.objects.Step;

import java.util.ArrayList;

/**
 * Created by adamzarn on 8/17/17.
 */

public class IngredientsAndStepsFragment extends Fragment {

    public IngredientsAndStepsFragment() {
    }

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

        Recipe selectedRecipe = this.getArguments().getParcelable("recipe");

        View rootView = inflater.inflate(R.layout.ingredients_and_steps_fragment, container, false);
        TextView ingredients = (TextView) rootView.findViewById(R.id.ingredients_header);
        ingredients.setText(getResources().getString(R.string.ingredients_header));
        TextView steps = (TextView) rootView.findViewById(R.id.steps_header);
        steps.setText(getResources().getString(R.string.steps_header));

        RecyclerView ingredientsRecyclerView = (RecyclerView) rootView.findViewById(R.id.ingredients_recycler_view);
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        IngredientsAdapter ingredientsAdapter = new IngredientsAdapter();
        ingredientsRecyclerView.setAdapter(ingredientsAdapter);
        ingredientsAdapter.setData(selectedRecipe.getIngredients());
        ingredientsRecyclerView.setNestedScrollingEnabled(false);

        RecyclerView stepsRecyclerView = (RecyclerView) rootView.findViewById(R.id.steps_recycler_view);
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
        ingredientsRecyclerView.setNestedScrollingEnabled(false);

        return rootView;

    }

}

