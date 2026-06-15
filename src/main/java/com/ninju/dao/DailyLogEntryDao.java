package com.ninju.dao;

import com.ninju.model.DailyLogEntry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class DailyLogEntryDao {

    @PersistenceContext
    EntityManager em;

    @Transactional
    public void save(DailyLogEntry entry) {
        em.persist(entry);
    }

    @Transactional
    public void delete(Long id) {
        DailyLogEntry e = em.find(DailyLogEntry.class, id);
        if (e != null) em.remove(e);
    }

    public DailyLogEntry findById(Long id) {
        return em.find(DailyLogEntry.class, id);
    }

    public List<DailyLogEntry> findByDailyLogId(Long dailyLogId) {
        return em.createQuery(
            "SELECT e FROM DailyLogEntry e WHERE e.dailyLog.id = :logId ORDER BY e.mealType, e.id",
            DailyLogEntry.class)
            .setParameter("logId", dailyLogId)
            .getResultList();
    }

    public List<DailyLogEntry> findByUserAndDate(Long userId, LocalDate date) {
        return em.createQuery(
            "SELECT e FROM DailyLogEntry e " +
            "WHERE e.dailyLog.user.id = :userId AND e.dailyLog.logDate = :date " +
            "ORDER BY e.mealType, e.id",
            DailyLogEntry.class)
            .setParameter("userId", userId)
            .setParameter("date", date)
            .getResultList();
    }
}
