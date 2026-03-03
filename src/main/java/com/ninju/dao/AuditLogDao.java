package com.ninju.dao;

import com.ninju.model.AuditLog;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class AuditLogDao {

    @PersistenceContext
    EntityManager em;

    @Transactional
    public void save(AuditLog auditLog) {
        em.persist(auditLog);
    }

    // Listar as ações do sistema
    public List<AuditLog> findAll() {
        return em.createQuery("SELECT a FROM AuditLog a ORDER BY a.executionTime DESC", AuditLog.class).getResultList();
    }
}
