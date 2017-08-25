package com.example.android.bakingapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.objects.Step;

import java.util.ArrayList;

/**
 * Created by adamzarn on 8/18/17.
 */

public class StepDetailFragment extends Fragment {

    public StepDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        int currentStep = this.getArguments().getInt("Step");
        ArrayList<Step> steps = this.getArguments().getParcelableArrayList("Steps");

        View rootView = inflater.inflate(R.layout.step_detail_fragment, container, false);

        TextView description = (TextView) rootView.findViewById(R.id.description_text_view);
        description.setText(steps.get(currentStep).getDescription());
        Button nextStepButton = (Button) rootView.findViewById(R.id.next_step_button);

        return rootView;

    }
}
