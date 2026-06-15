package com.ninju.dto;

public class UserPlanExerciseDTO {
    public Long id;
    public Long workoutId;
    public String workoutName;
    public String category;
    public String exerciseType;

    // MUSCULACAO
    public Integer sets;
    public Integer reps;
    public Double weightKg;

    // TEMPO
    public Double durationMinutes;

    public Double estimatedCalories;

    public UserPlanExerciseDTO(Long id, Long workoutId, String workoutName, String category,
                                String exerciseType, Integer sets, Integer reps, Double weightKg,
                                Double durationMinutes, Double estimatedCalories) {
        this.id = id;
        this.workoutId = workoutId;
        this.workoutName = workoutName;
        this.category = category;
        this.exerciseType = exerciseType;
        this.sets = sets;
        this.reps = reps;
        this.weightKg = weightKg;
        this.durationMinutes = durationMinutes;
        this.estimatedCalories = estimatedCalories;
    }
}
