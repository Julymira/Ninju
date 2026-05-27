package com.ninju.dao;

import com.ninju.model.DailyLog;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class DailyLogDao {

    @PersistenceContext
    EntityManager em;

    @Transactional
    public void save(DailyLog dailyLog) {
        em.persist(dailyLog);
    }

    @Transactional
    public void update(DailyLog dailyLog) {
        em.merge(dailyLog);
    }

    public DailyLog findById(Long id) {
        return em.find(DailyLog.class, id);
    }

    public List<DailyLog> findByUserId(Long userId) {
        TypedQuery<DailyLog> query = em.createQuery(
            "SELECT d FROM DailyLog d WHERE d.user.id = :userId ORDER BY d.logDate DESC", DailyLog.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    public DailyLog findByUserIdAndDate(Long userId, LocalDate date) {
        try {
            TypedQuery<DailyLog> query = em.createQuery(
                "SELECT d FROM DailyLog d WHERE d.user.id = :userId AND d.logDate = :date", DailyLog.class);
            query.setParameter("userId", userId);
            query.setParameter("date", date);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
