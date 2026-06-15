package com.ninju.dto;

import java.util.List;

public class WorkoutLogDTO {
    public Long id;
    public String planName;
    public double totalCalories;
    public List<WorkoutLogExerciseDTO> exercises;

    public WorkoutLogDTO(Long id, String planName, double totalCalories, List<WorkoutLogExerciseDTO> exercises) {
        this.id = id;
        this.planName = planName;
        this.totalCalories = totalCalories;
        this.exercises = exercises;
    }
}
