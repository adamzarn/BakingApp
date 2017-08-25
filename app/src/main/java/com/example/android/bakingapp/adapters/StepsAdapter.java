package com.example.android.bakingapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.objects.Step;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by adamzarn on 8/18/17.
 */

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.ViewHolder> {

    private Step[] mSteps = null;
    private OnItemClickListener clickListener;
    private Context context;

    public void setData(Step[] steps) {
        mSteps = steps;
        notifyDataSetChanged();
    }

    public ArrayList<Step> getStepsArrayList() {
        return new ArrayList<>(Arrays.asList(mSteps));
    }

    @Override
    public StepsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.step_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StepsAdapter.ViewHolder viewHolder, int i) {
        viewHolder.stepShortDescription.setText(mSteps[i].getShortDescription());
    }

    @Override
    public int getItemCount() {
        if (mSteps != null) {
            return mSteps.length;
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView stepShortDescription;

        public ViewHolder(View view) {
            super(view);
            stepShortDescription = (TextView) view.findViewById(R.id.step_short_description);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), getStepsArrayList());
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(int position, ArrayList<Step> steps);
    }

    public void setOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

}
