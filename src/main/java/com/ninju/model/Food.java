package com.ninju.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "foods")
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name; // Nome do alimento

    @Column(nullable = false)
    private Integer calories; // Calorias

    @Column(nullable = false)
    private Double protein; // Proteínas em gramas

    @Column(nullable = false)
    private Double carbohydrates; // Carboidratos em gramas

    @Column(nullable = false)
    private Double fat; // Gorduras em gramas

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner; // null = alimento global (admin); preenchido = alimento pessoal do usuário

    public Food() {}

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
    public Integer getCalories() {
        return calories;
    }
    public void setCalories(Integer calories) {
        this.calories = calories;
    }
    public Double getProtein() {
        return protein;
    }
    public void setProtein(Double protein) {
        this.protein = protein;
    }
    public Double getCarbohydrates() {
        return carbohydrates;
    }
    public void setCarbohydrates(Double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }
    public Double getFat() {
        return fat;
    }
    public void setFat(Double fat) {
        this.fat = fat;
    }
    public User getOwner() {
        return owner;
    }
    public void setOwner(User owner) {
        this.owner = owner;
    }

}