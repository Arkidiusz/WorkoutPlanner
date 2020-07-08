package com.example.workoutplanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;

/**
 * Display a list of workouts that can be added, modified and deleted by the user.
 */
public class MainActivity extends AppCompatActivity {

    private ArrayList<Workout> workouts;
    private WorkoutsAdapter workoutsAdapter;

    public static final int WORKOUT_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup RecyclerView of Workouts
        workouts = new ArrayList<>() ;
        String[] sampleWorkouts = getResources().getStringArray(R.array.sample_workouts);
        for(String s : sampleWorkouts){
            ArrayList<Exercise> sampleExercises = new ArrayList<>();
            sampleExercises.add(new Exercise("Bench Press", 5));
            sampleExercises.add(new Exercise("Squat", 4));
            workouts.add(new Workout(s, sampleExercises));
        }
        RecyclerView workoutsRecyclerView = findViewById(R.id.workoutsRecyclerView);
        workoutsAdapter = new WorkoutsAdapter(this, workouts);
        workoutsRecyclerView.setAdapter(workoutsAdapter);
        workoutsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Setup FAB for adding workouts
        FloatingActionButton addWorkoutButton = findViewById(R.id.addWorkoutButton);
        addWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PlanActivity.class);
                startActivityForResult(intent, WORKOUT_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == WORKOUT_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    workouts.add((Workout) data.getSerializableExtra("workout"));
                    workoutsAdapter.notifyItemInserted(workouts.size() - 1);
                }
            }
        }
    }

    public class WorkoutsAdapter extends RecyclerView.Adapter<WorkoutsAdapter.WorkoutsViewHolder>{

        private final Context context;
        private final ArrayList<Workout> workoutDays;

        public WorkoutsAdapter(Context context, ArrayList<Workout> workoutDays){
            this.context = context;
            this.workoutDays = workoutDays;
        }

        @NonNull
        @Override
        public WorkoutsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.workout_row, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(),"Workout clicked",Toast.LENGTH_SHORT).show();
                }
            });
            return new WorkoutsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WorkoutsViewHolder holder, int position) {
            Workout workout = workoutDays.get(position);

            holder.workoutName.setText(workout.getName());

            // Build a String of all exercises of a Workout to be displayed
            StringBuilder exercises = new StringBuilder();
            boolean firstExercise = true;
            for(Exercise exercise : workout.getExercises()){
                if(!firstExercise){
                    exercises.append(", ");
                }
                exercises.append(exercise.getName());
                firstExercise = false;
            }
            exercises.lastIndexOf(exercises.toString());
            holder.exercises.setText(exercises);
        }

        @Override
        public int getItemCount() {
            return workoutDays.size();
        }

        public class WorkoutsViewHolder extends RecyclerView.ViewHolder{
            TextView workoutName;
            TextView exercises;

            public WorkoutsViewHolder(@NonNull View itemView) {
                super(itemView);
                workoutName = itemView.findViewById(R.id.workout_name);
                exercises = itemView.findViewById(R.id.exercises);
            }
        }
    }
}