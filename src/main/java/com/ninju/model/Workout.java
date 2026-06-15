package com.ninju.model;

import jakarta.persistence.*;

@Entity
@Table(name = "workouts")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 80)
    private String category;

    // "MUSCULACAO" = séries × reps × carga × fator | "TEMPO" = duração(min) × fator
    @Column(nullable = false, length = 20)
    private String exerciseType;

    // MUSCULACAO: kcal por rep por kg | TEMPO: kcal por minuto
    @Column(nullable = false)
    private Double calorieFactor;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner; // null = global; preenchido = pessoal do usuário

    public Workout() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getExerciseType() { return exerciseType; }
    public void setExerciseType(String exerciseType) { this.exerciseType = exerciseType; }
    public Double getCalorieFactor() { return calorieFactor; }
    public void setCalorieFactor(Double calorieFactor) { this.calorieFactor = calorieFactor; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
}
