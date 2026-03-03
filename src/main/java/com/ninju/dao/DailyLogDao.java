package com.ninju.dao;

import com.ninju.model.DailyLog;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class DailyLogDao {

    @PersistenceContext
    EntityManager em;

    @Transactional
    public void save(DailyLog dailyLog) {
        em.persist(dailyLog);
    }

    // Buscar o histórico de um usuário específico
    public List<DailyLog> findByUserId(Long userId) {
        TypedQuery<DailyLog> query = em.createQuery(
            "SELECT d FROM DailyLog d WHERE d.user.id = :userId ORDER BY d.logDate DESC", DailyLog.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
}
