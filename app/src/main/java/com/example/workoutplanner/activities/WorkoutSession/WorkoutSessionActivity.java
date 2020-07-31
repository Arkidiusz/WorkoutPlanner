package com.example.workoutplanner.activities.WorkoutSession;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;

import com.example.workoutplanner.R;
import com.example.workoutplanner.database.Exercise.Exercise;
import com.example.workoutplanner.database.ExerciseLog.ExerciseLog;
import com.example.workoutplanner.database.ExerciseLog.ExerciseLogDao;
import com.example.workoutplanner.database.WorkoutPlan.WorkoutPlan;
import com.example.workoutplanner.database.WorkoutPlannerDatabase;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class WorkoutSessionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_session);
        Intent intent = getIntent();
        final WorkoutPlan workoutPlan = (WorkoutPlan) intent.getSerializableExtra(
                "workoutPlan");
        final ViewPager2 viewpager2 = findViewById(R.id.vp_exercise_plans);
        final ExercisePagerAdapter exercisePagerAdapter = new ExercisePagerAdapter(this, workoutPlan);
        viewpager2.setAdapter(exercisePagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tl_exercise_plans);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewpager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if(position < workoutPlan.getExercisePlans().size()){
                    tab.setText(workoutPlan.getExercisePlans().get(position).getExercise().getName());
                }
            }
        });
        tabLayoutMediator.attach();

        // Finish workout session, and log all data from user input
        Button btnFinishSession = findViewById(R.id.btn_finish_session);
        btnFinishSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WorkoutPlannerDatabase db = WorkoutPlannerDatabase.getDataBase(getApplication());
                final ExerciseLogDao exerciseLogDao = db.exerciseLogDao();
                SparseArray<ExerciseFragment> exerciseFragments =
                        exercisePagerAdapter.getRegisteredFragments();
                for(int i = 0; i < exerciseFragments.size(); i++){
                    int key = exerciseFragments.keyAt(i);
                    ExerciseFragment exerciseFragment = exerciseFragments.get(key);
                    ArrayList<ExerciseLog> exerciseLogs = exerciseFragment.getExerciseLogs();
                    for(final ExerciseLog exerciseLog : exerciseLogs){
                        //TODO insert all exercises at once instead
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                exerciseLogDao.insert(exerciseLog);
                            }
                        });
                        thread.start();
                    }
                }
                finish();
            }
        });
    }
}