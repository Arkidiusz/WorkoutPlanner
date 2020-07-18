package com.example.workoutplanner;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

@Entity
public class Exercise {
    @PrimaryKey
    @NonNull
    private String name;
    @NonNull
    private ExerciseType exerciseType;

    public Exercise(@NonNull String name, @NonNull ExerciseType exerciseType){
        this.name = name;
        this.exerciseType = exerciseType;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public ExerciseType getExerciseType() {
        return exerciseType;
    }

    @TypeConverter
    public static String fromExerciseType(ExerciseType exerciseType){
        return exerciseType.toString();
    }

    @TypeConverter
    public static ExerciseType stringToExerciseType(String exerciseTypeName){
        return ExerciseType.valueOf(exerciseTypeName);
    }

    @NonNull
    @Override
    public String toString(){
        return name;
    }

    public enum ExerciseType{
        ISOMETRIC,
        TEMPO
    }
}
