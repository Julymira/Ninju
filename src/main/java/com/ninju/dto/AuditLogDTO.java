package com.ninju.dto;

import java.time.LocalDateTime;

public class AuditLogDTO {
    public Long id;
    public String actionExecuted;
    public String executedBy;
    public LocalDateTime executionTime;

    public AuditLogDTO(Long id, String actionExecuted, String executedBy, LocalDateTime executionTime) {
        this.id = id;
        this.actionExecuted = actionExecuted;
        this.executedBy = executedBy;
        this.executionTime = executionTime;
    }
}
