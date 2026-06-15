package com.ninju.dao;

import com.ninju.model.WorkoutLog;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class WorkoutLogDao {

    @PersistenceContext
    EntityManager em;

    @Transactional
    public void save(WorkoutLog log) {
        em.persist(log);
    }

    public WorkoutLog findById(Long id) {
        return em.find(WorkoutLog.class, id);
    }

    public List<WorkoutLog> findByUserAndDate(Long userId, LocalDate date) {
        return em.createQuery(
            "SELECT l FROM WorkoutLog l WHERE l.user.id = :uid AND l.logDate = :date", WorkoutLog.class)
            .setParameter("uid", userId)
            .setParameter("date", date)
            .getResultList();
    }

    @Transactional
    public void delete(WorkoutLog log) {
        em.remove(em.contains(log) ? log : em.merge(log));
    }
}
