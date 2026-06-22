package com.ninju.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Table(name = "users") // Usar plural no banco é uma boa convenção
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(length = 100)
    private String avatar;

    @Column
    private Double weight;

    @Column
    private Integer calorieMeta;

    @Column
    private Integer carbsMetaPct;

    @Column
    private Integer proteinMetaPct;

    @Column
    private Integer fatMetaPct;

    public User() {
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Integer getCalorieMeta() { return calorieMeta; }
    public void setCalorieMeta(Integer calorieMeta) { this.calorieMeta = calorieMeta; }

    public Integer getCarbsMetaPct() { return carbsMetaPct; }
    public void setCarbsMetaPct(Integer carbsMetaPct) { this.carbsMetaPct = carbsMetaPct; }

    public Integer getProteinMetaPct() { return proteinMetaPct; }
    public void setProteinMetaPct(Integer proteinMetaPct) { this.proteinMetaPct = proteinMetaPct; }

    public Integer getFatMetaPct() { return fatMetaPct; }
    public void setFatMetaPct(Integer fatMetaPct) { this.fatMetaPct = fatMetaPct; }
}