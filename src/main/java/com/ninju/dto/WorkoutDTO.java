package com.ninju.dto;

public class WorkoutDTO {
    public Long id;
    public String name;
    public String category;
    public String exerciseType;   // MUSCULACAO | TEMPO
    public Double calorieFactor;  // MUSCULACAO: kcal/rep/kg | TEMPO: kcal/min
    public Long ownerId;

    public WorkoutDTO(Long id, String name, String category, String exerciseType, Double calorieFactor, Long ownerId) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.exerciseType = exerciseType;
        this.calorieFactor = calorieFactor;
        this.ownerId = ownerId;
    }
}
