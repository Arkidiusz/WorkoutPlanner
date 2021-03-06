package com.example.workout_planner.activities.chart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.workout_planner.R;
import com.example.workout_planner.activities.logs.LogsActivity;
import com.example.workout_planner.database.WorkoutPlannerDatabase;
import com.example.workout_planner.database.exercise_log.ExerciseLog;
import com.example.workout_planner.database.exercise_log.ExerciseLogDao;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.threeten.bp.LocalDate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Chart displays 1RM (1 rep max) over time for an exercise passed in an intent
 */
public class ChartActivity extends AppCompatActivity {
    private List<LocalDate> dates;
    private List<ExerciseLog> exerciseLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        Intent intent = getIntent();
        final String exerciseName = intent.getStringExtra("exerciseName");

        // Fetch exerciseLogs and dates corresponding to given exerciseName
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                WorkoutPlannerDatabase db = WorkoutPlannerDatabase.getDataBase(getApplication());
                ExerciseLogDao dao = db.exerciseLogDao();
                dates = dao.getDatesOfExercise(exerciseName);
                exerciseLogs = dao.getExerciseLogsOfName(exerciseName);
            }
        });
        thread.start();
        while (dates == null || exerciseLogs == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Collections.reverse(dates);// results in ascending order

        // Compute data points for LineChart
        ArrayList<Entry> dataValues = new ArrayList<>();
        for (LocalDate date : dates) {
            double current1RM = 0;
            for (ExerciseLog exerciseLog : exerciseLogs) {
                if (exerciseLog.getDate().isEqual(date)) {
                    double _1RM = LogsActivity.estimate1RM(exerciseLog.getWeight(),
                            exerciseLog.getReps());
                    if (_1RM > current1RM) current1RM = _1RM;
                }
            }
            dataValues.add(new Entry(date.toEpochDay(), (float) current1RM));
        }

        LineChart lineChart = findViewById(R.id.line_chart);
        LineDataSet lineDataSet = new LineDataSet(dataValues, "Bench Press 1RM\"");

        // Chart aesthetics, formatting etc.
        lineDataSet.setLineWidth(3f);
        lineDataSet.setCircleRadius(6f);
        lineDataSet.setCircleHoleRadius(3f);
        lineDataSet.setCircleColor(ContextCompat.getColor(getApplicationContext(),
                R.color.primaryColor));
        lineDataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.primaryColor));
        lineDataSet.setValueTextSize(12f);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        LineData data = new LineData(dataSets);

        lineChart.setScaleEnabled(true);
        lineChart.setData(data);
        lineChart.getXAxis().setValueFormatter(new DateFormatter());
        lineChart.animateXY(1000, 1000);
        lineChart.getDescription().setEnabled(false);
        lineChart.setExtraTopOffset(4f);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(4, true);

        YAxis axisLeft = lineChart.getAxisLeft();
        axisLeft.setValueFormatter(new KgFormatter());
        axisLeft.setTextSize(12f);

        YAxis axisRight = lineChart.getAxisRight();
        axisRight.setTextSize(12f);
        axisRight.setValueFormatter(new KgFormatter());

        lineChart.invalidate();
    }

    // Converts floats to LocalDates
    public static class DateFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return LocalDate.ofEpochDay((long) value).toString();
        }
    }

    // Adds kg to weight values
    public static class KgFormatter extends ValueFormatter{
        @Override
        public String getFormattedValue(float value) {
            return new DecimalFormat("0.00").format(value) + "kg";
        }
    }
}