package com.ninju.model;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Table;

@Entity
@Table(name = "workouts")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name; // Nome do exercício (ex: Supino, Corrida)

    @Column(nullable = false, length = 50)
    private String category; // Categoria (ex: Musculação, Cardio)

    @Column(nullable = false)
    private Integer estimatedCaloriesBurned; // Gasto calórico médio

    public Workout() {}

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getEstimatedCaloriesBurned() {
        return estimatedCaloriesBurned;
    }

    public void setEstimatedCaloriesBurned(Integer estimatedCaloriesBurned) {
        this.estimatedCaloriesBurned = estimatedCaloriesBurned;
    }

}