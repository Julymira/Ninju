package com.ninju.dto;

import java.util.List;

public class UserPlanDTO {
    public Long id;
    public String name;
    public double totalCalories;
    public List<UserPlanExerciseDTO> exercises;

    public UserPlanDTO(Long id, String name, double totalCalories, List<UserPlanExerciseDTO> exercises) {
        this.id = id;
        this.name = name;
        this.totalCalories = totalCalories;
        this.exercises = exercises;
    }
}
