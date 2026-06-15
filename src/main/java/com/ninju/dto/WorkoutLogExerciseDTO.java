package com.ninju.dto;

public class WorkoutLogExerciseDTO {
    public Long id;
    public String workoutName;
    public String exerciseType;
    public Integer sets;
    public Integer reps;
    public Double weightKg;
    public Double durationMinutes;
    public double estimatedCalories;

    public WorkoutLogExerciseDTO(Long id, String workoutName, String exerciseType,
                                  Integer sets, Integer reps, Double weightKg,
                                  Double durationMinutes, double estimatedCalories) {
        this.id = id;
        this.workoutName = workoutName;
        this.exerciseType = exerciseType;
        this.sets = sets;
        this.reps = reps;
        this.weightKg = weightKg;
        this.durationMinutes = durationMinutes;
        this.estimatedCalories = estimatedCalories;
    }
}
