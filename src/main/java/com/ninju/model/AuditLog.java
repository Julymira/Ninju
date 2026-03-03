package com.ninju.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String actionExecuted; // O que foi feito (ex: "Criou novo alimento")

    @Column(nullable = false, length = 100)
    private String executedBy; // Quem fez (Email ou Nome do Usuário)

    @Column(nullable = false)
    private LocalDateTime executionTime; // Data e hora da ação

    public AuditLog() {}

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActionExecuted() {
        return actionExecuted;
    }

    public void setActionExecuted(String actionExecuted) {
        this.actionExecuted = actionExecuted;
    }

    public String getExecutedBy() {
        return executedBy;
    }

    public void setExecutedBy(String executedBy) {
        this.executedBy = executedBy;
    }

    public LocalDateTime getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }
}