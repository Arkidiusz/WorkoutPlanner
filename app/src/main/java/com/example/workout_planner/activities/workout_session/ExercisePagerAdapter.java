package com.example.workout_planner.activities.workout_session;

import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.workout_planner.database.workout_plan.WorkoutPlan;

public class ExercisePagerAdapter extends FragmentStateAdapter {
    private SparseArray<ExerciseFragment> registeredFragments = new SparseArray<>();

    private WorkoutPlan workoutPlan;

    public ExercisePagerAdapter(@NonNull FragmentActivity fragmentActivity, WorkoutPlan workoutPlan) {
        super(fragmentActivity);
        this.workoutPlan = workoutPlan;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        ExerciseFragment exerciseFragment =
                new ExerciseFragment(workoutPlan.getExercisePlans().get(position));
        registeredFragments.put(position, exerciseFragment);
        return exerciseFragment;
    }

    @Override
    public int getItemCount() {
        return workoutPlan.getExercisePlans().size();
    }

    public SparseArray<ExerciseFragment> getRegisteredFragments() {
        return registeredFragments;
    }
}
