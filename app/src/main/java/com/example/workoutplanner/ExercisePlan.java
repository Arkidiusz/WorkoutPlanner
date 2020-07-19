package com.example.workoutplanner;

import java.io.Serializable;

/**
 * Class representing an ExercisePlan belonging to a WorkoutPlan
 */
public class ExercisePlan implements Serializable {
    private Exercise exercise;
    private int noSets;

    private ExercisePlan(Exercise exercise, int noSets) {
        this.exercise = exercise;
        this.noSets = noSets;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public int getNoSets() {
        return noSets;
    }

    public void setNoSets(int noSets) {
        this.noSets = noSets;
    }

    /**
     * Tempo, e.g. 4020 where 4 is 4s of eccentric phase and so on
     */
    public static class TempoExercisePlan extends ExercisePlan {
        private int noReps;
        private int eccentric;
        private int eccentricPause;
        private int concentric;
        private int concentricPause;

        public TempoExercisePlan(Exercise exercise, int noSets, int noReps, int eccentric,
                                 int eccentricPause, int concentric, int concentricPause) {
            super(exercise, noSets);
            this.noReps = noReps;
            this.eccentric = eccentric;
            this.eccentricPause = eccentricPause;
            this.concentric = concentric;
            this.concentricPause = concentricPause;
        }

        public int getNoReps() {
            return noReps;
        }

        public void setNoReps(int noReps) {
            this.noReps = noReps;
        }

        public int getEccentric() {
            return eccentric;
        }

        public void setEccentric(int eccentric) {
            this.eccentric = eccentric;
        }

        public int getEccentricPause() {
            return eccentricPause;
        }

        public void setEccentricPause(int eccentricPause) {
            this.eccentricPause = eccentricPause;
        }

        public int getConcentric() {
            return concentric;
        }

        public void setConcentric(int concentric) {
            this.concentric = concentric;
        }

        public int getConcentricPause() {
            return concentricPause;
        }

        public void setConcentricPause(int concentricPause) {
            this.concentricPause = concentricPause;
        }
    }

    public static class IsometricExercisePlan extends ExercisePlan {
        private int time;

        public IsometricExercisePlan(Exercise exercise, int noSets, int time) {
            super(exercise, noSets);
            this.time = time;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }
    }
}