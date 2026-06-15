package com.ninju.model;

import jakarta.persistence.*;

@Entity
@Table(name = "workout_log_exercises")
public class WorkoutLogExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "log_id")
    private WorkoutLog log;

    @Column(nullable = false, length = 150)
    private String workoutName;

    @Column(nullable = false, length = 20)
    private String exerciseType;

    private Integer sets;
    private Integer reps;
    private Double weightKg;
    private Double durationMinutes;

    @Column(nullable = false)
    private Double estimatedCalories;

    public WorkoutLogExercise() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public WorkoutLog getLog() { return log; }
    public void setLog(WorkoutLog log) { this.log = log; }
    public String getWorkoutName() { return workoutName; }
    public void setWorkoutName(String workoutName) { this.workoutName = workoutName; }
    public String getExerciseType() { return exerciseType; }
    public void setExerciseType(String exerciseType) { this.exerciseType = exerciseType; }
    public Integer getSets() { return sets; }
    public void setSets(Integer sets) { this.sets = sets; }
    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }
    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }
    public Double getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Double durationMinutes) { this.durationMinutes = durationMinutes; }
    public Double getEstimatedCalories() { return estimatedCalories; }
    public void setEstimatedCalories(Double estimatedCalories) { this.estimatedCalories = estimatedCalories; }
}
