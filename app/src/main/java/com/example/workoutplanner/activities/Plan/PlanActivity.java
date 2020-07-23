package com.example.workoutplanner.activities.Plan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutplanner.R;
import com.example.workoutplanner.activities.AddExercise.AddExerciseActivity;
import com.example.workoutplanner.database.ExercisePlan;
import com.example.workoutplanner.database.WorkoutPlan.WorkoutPlan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class PlanActivity extends AppCompatActivity {

    public static final int EXERCISE_REQUEST = 2;
    private EditText etWorkoutName;
    private ExercisesAdapter mExercisesAdapter;
    private ArrayList<ExercisePlan> exercisePlans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        etWorkoutName = findViewById(R.id.et_workout_name);

        // Button used to save created workout session
        Button btnSaveWorkout = findViewById(R.id.btn_save);
        btnSaveWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String workoutName = etWorkoutName.getText().toString();
                if (!workoutName.isEmpty()) {
                    WorkoutPlan workoutPlan = new WorkoutPlan(etWorkoutName.getText().toString(),
                            exercisePlans);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("workoutPlan", workoutPlan);
                    setResult(AppCompatActivity.RESULT_OK, resultIntent);
                    Toast.makeText(getApplicationContext(), "WorkoutPlan " + workoutName
                            + " saved.", Toast.LENGTH_SHORT).show();
                    finish();
                }//@TODO Handle a case where a workout of same name exists
                else {
                    Toast.makeText(getApplicationContext(), "Please enter the name.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Abandon creation of new workout
        Button btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button btnAddExercise = findViewById(R.id.btn_add_exercise);
        btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlanActivity.this,
                        AddExerciseActivity.class);
                startActivityForResult(intent, EXERCISE_REQUEST);
            }
        });

        // List of planned Exercises for a workout
        RecyclerView rvExercises = findViewById(R.id.rv_exercises);
        exercisePlans = new ArrayList<>();//testing sample
//        exercisePlans.add(new ExercisePlan.TempoExercisePlan("Bench Press", 5, 5, 4, 0, 1, 0));
//        exercisePlans.add(new ExercisePlan.IsometricExercisePlan("Plank", 5, 60));
        mExercisesAdapter = new ExercisesAdapter(this, exercisePlans);
        rvExercises.setAdapter(mExercisesAdapter);
        rvExercises.setLayoutManager(new LinearLayoutManager(this));
        rvExercises.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        // Drag RV items to reorder
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START
                        | ItemTouchHelper.END, 0) {
            @Override
            // Swap positions of the items moved
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(exercisePlans, fromPosition, toPosition);
                if(recyclerView.getAdapter() == null){
                    throw new NullPointerException("Unexpected null adapter");
                }
                else{
                    recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
                }
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // do nothing;
            }
        });
        itemTouchHelper.attachToRecyclerView(rvExercises);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EXERCISE_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    exercisePlans.add((ExercisePlan) data.getSerializableExtra("exercisePlan"));
                    mExercisesAdapter.notifyItemInserted(exercisePlans.size() - 1);
                }
            }
        }
    }

    public static class ExercisesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TEMPO_EXERCISE = 1;
        private static final int ISOMETRIC_EXERCISE = 2;
        private final Context mContext;
        private final ArrayList<ExercisePlan> exercisePlans;

        public ExercisesAdapter(Context mContext, ArrayList<ExercisePlan> exercisePlans) {
            this.mContext = mContext;
            this.exercisePlans = exercisePlans;
        }

        @Override
        public int getItemViewType(int position) {
            if (exercisePlans.get(position) instanceof ExercisePlan.TempoExercisePlan) {
                return TEMPO_EXERCISE;
            } else {
                return ISOMETRIC_EXERCISE;
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            switch (viewType) {
                case 1: {
                    View view = inflater.inflate(R.layout.item_tempo_exercise, parent, false);
                    return new TempoViewHolder(view);
                }
                case 2: {
                    View view = inflater.inflate(R.layout.item_isometric_exercise, parent, false);
                    return new IsometricViewHolder(view);
                }
                default:{
                    throw new IllegalStateException("Unexpected viewType " + viewType);
                }
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 1: {
                    ExercisePlan.TempoExercisePlan exercise
                            = (ExercisePlan.TempoExercisePlan) exercisePlans.get(position);
                    TempoViewHolder tempo_holder = (TempoViewHolder) holder;
                    Log.i("XD", exercise.getExercise().toString());
                    tempo_holder.tvExerciseName.setText(exercise.getExercise().getName());
                    tempo_holder.etNoSets.setText(String.format(Locale.getDefault(), "%d",
                            exercise.getNoSets()));
                    tempo_holder.etNoReps.setText(String.format(Locale.getDefault(), "%d",
                            exercise.getNoReps()));
                    String sb = String.valueOf(exercise.getEccentric()) + exercise.getEccentricPause() +
                            exercise.getConcentric() + exercise.getConcentricPause();
                    tempo_holder.etTempo.setText(sb);
                    break;
                }
                case 2: {
                    ExercisePlan.IsometricExercisePlan exercise
                            = (ExercisePlan.IsometricExercisePlan) exercisePlans.get(position);
                    IsometricViewHolder isometric_holder = (IsometricViewHolder) holder;
                    Log.i("XD", exercise.getExercise().toString());
                    isometric_holder.tvExerciseName.setText(exercise.getExercise().getName());
                    isometric_holder.etNoSets.setText(String.format(Locale.getDefault(), "%d",
                            exercise.getNoSets()));
                    isometric_holder.etTime.setText(String.format(Locale.getDefault(),"%d",
                            exercise.getTime()));
                    break;
                }
            }
        }

        @Override
        public int getItemCount() {
            return exercisePlans.size();
        }

        public static class TempoViewHolder extends RecyclerView.ViewHolder {

            private TextView tvExerciseName;
            private EditText etNoSets;
            private EditText etNoReps;
            private EditText etTempo;

            public TempoViewHolder(@NonNull View itemView) {
                super(itemView);
                tvExerciseName = itemView.findViewById(R.id.tv_exercise_name);
                etNoSets = itemView.findViewById(R.id.et_no_sets);
                etNoReps = itemView.findViewById(R.id.et_no_reps);
                etTempo = itemView.findViewById(R.id.et_tempo);
            }
        }

        public static class IsometricViewHolder extends RecyclerView.ViewHolder {

            private TextView tvExerciseName;
            private EditText etNoSets;
            private EditText etTime;

            public IsometricViewHolder(@NonNull View itemView) {
                super(itemView);
                tvExerciseName = itemView.findViewById(R.id.tv_iso_exercise_name);
                etNoSets = itemView.findViewById(R.id.et_iso_no_sets);
                etTime = itemView.findViewById(R.id.et_iso_time);
            }
        }
    }
}