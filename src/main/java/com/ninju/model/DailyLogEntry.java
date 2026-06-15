package com.ninju.model;

import jakarta.persistence.*;

@Entity
@Table(name = "daily_log_entries")
public class DailyLogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "daily_log_id", nullable = false)
    private DailyLog dailyLog;

    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @Column(nullable = false)
    private Double quantityGrams;

    // CAFE | ALMOCO | JANTAR | LANCHE
    @Column(nullable = false, length = 20)
    private String mealType;

    public DailyLogEntry() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DailyLog getDailyLog() { return dailyLog; }
    public void setDailyLog(DailyLog dailyLog) { this.dailyLog = dailyLog; }

    public Food getFood() { return food; }
    public void setFood(Food food) { this.food = food; }

    public Double getQuantityGrams() { return quantityGrams; }
    public void setQuantityGrams(Double quantityGrams) { this.quantityGrams = quantityGrams; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }
}
