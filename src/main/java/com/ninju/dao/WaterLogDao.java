package com.ninju.dao;


import com.ninju.model.WaterLog;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDate;

@ApplicationScoped
public class WaterLogDao {
    
    @PersistenceContext
    EntityManager em;

    @Transactional
    public void save(WaterLog waterLog) {
        em.merge(waterLog);
    }

    @Transactional
    public WaterLog findByUserAndDate(Long userId, LocalDate date) {
    return em.createQuery(
        "SELECT w FROM WaterLog w WHERE w.user.id = :uid AND w.date = :date", WaterLog.class)
        .setParameter("uid", userId)
        .setParameter("date", date)
        .getResultStream()
        .findFirst()
        .orElse(null);
    }
}
