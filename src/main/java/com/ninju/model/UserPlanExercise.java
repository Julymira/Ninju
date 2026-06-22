package com.ninju.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_plan_exercises")
public class UserPlanExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plan_id")
    private UserPlan plan;

    @ManyToOne(optional = false)
    @JoinColumn(name = "workout_id")
    private Workout workout;

    // MUSCULACAO
    @Column
    private Integer sets;

    @Column
    private Integer reps;

    @Column
    private Double weightKg;

    // TEMPO (aeróbico / fluxo contínuo)
    @Column
    private Double durationMinutes;

    public UserPlanExercise() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserPlan getPlan() { return plan; }
    public void setPlan(UserPlan plan) { this.plan = plan; }

    public Workout getWorkout() { return workout; }
    public void setWorkout(Workout workout) { this.workout = workout; }

    public Integer getSets() { return sets; }
    public void setSets(Integer sets) { this.sets = sets; }

    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }

    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }
    
    public Double getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Double durationMinutes) { this.durationMinutes = durationMinutes; }
}
