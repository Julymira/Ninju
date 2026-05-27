package com.ninju.dto;

public class FoodDTO {
    public Long id;
    public String name;
    public Integer calories;
    public Double protein;
    public Double carbohydrates;
    public Double fat;

    public FoodDTO(Long id, String name, Integer calories, Double protein, Double carbohydrates, Double fat) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbohydrates = carbohydrates;
        this.fat = fat;
    }
}
