package com.ninju.dao;

import com.ninju.model.AuditLog;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
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
