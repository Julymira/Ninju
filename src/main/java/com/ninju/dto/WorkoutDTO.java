package com.ninju.dto;

public class WorkoutDTO {
    public Long id;
    public String name;
    public String category;
    public Integer estimatedCaloriesBurned;

    public WorkoutDTO(Long id, String name, String category, Integer estimatedCaloriesBurned) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.estimatedCaloriesBurned = estimatedCaloriesBurned;
    }
}
