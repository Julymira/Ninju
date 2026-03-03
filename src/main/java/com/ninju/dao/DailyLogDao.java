package com.ninju.dao;

import com.ninju.model.DailyLog;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
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
