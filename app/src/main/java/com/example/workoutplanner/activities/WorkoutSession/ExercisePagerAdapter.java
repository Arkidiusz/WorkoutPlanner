package com.example.workoutplanner.activities.WorkoutSession;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ExercisePagerAdapter extends FragmentStateAdapter {

    public ExercisePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new ExerciseFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    } //@TODO change this to use set size
}