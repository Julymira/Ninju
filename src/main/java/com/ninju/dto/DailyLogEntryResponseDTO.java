package com.ninju.dto;

public class DailyLogEntryResponseDTO {
    public Long id;
    public Long foodId;
    public String foodName;
    public String mealType;
    public Double quantityGrams;
    public Double calories;
    public Double protein;
    public Double carbs;
    public Double fat;

    public DailyLogEntryResponseDTO(Long id, Long foodId, String foodName, String mealType,
                                    Double quantityGrams, Double calories,
                                    Double protein, Double carbs, Double fat) {
        this.id = id;
        this.foodId = foodId;
        this.foodName = foodName;
        this.mealType = mealType;
        this.quantityGrams = quantityGrams;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
    }
}
