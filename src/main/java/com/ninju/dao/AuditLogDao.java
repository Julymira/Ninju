package com.ninju.dao;

import com.ninju.model.AuditLog;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class AuditLogDao {

    @PersistenceContext
    EntityManager em;

    @Transactional
    public void save(AuditLog auditLog) {
        em.persist(auditLog);
    }

    @Transactional
    public void save(String action, String executedBy) {
        AuditLog log = new AuditLog();
        log.setActionExecuted(action);
        log.setExecutedBy(executedBy);
        log.setExecutionTime(LocalDateTime.now());
        em.persist(log);
    }

    // Listar as ações do sistema
    public List<AuditLog> findAll() {
        return em.createQuery("SELECT a FROM AuditLog a ORDER BY a.executionTime DESC", AuditLog.class).getResultList();
    }
}
