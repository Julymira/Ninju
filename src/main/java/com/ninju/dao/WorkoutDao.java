package com.ninju.dao;

import com.ninju.model.Workout;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class WorkoutDao {

    @PersistenceContext
    EntityManager em;

    @Transactional
    public void save(Workout workout) {
        em.persist(workout);
    }

    public Workout findById(Long id) {
        return em.find(Workout.class, id);
    }

    public List<Workout> findAll() {
        return em.createQuery("SELECT w FROM Workout w", Workout.class).getResultList();
    }

    public List<Workout> findVisibleToUser(Long userId) {
        return em.createQuery(
            "SELECT w FROM Workout w WHERE w.owner IS NULL OR w.owner.id = :uid", Workout.class)
            .setParameter("uid", userId)
            .getResultList();
    }

    @Transactional
    public void delete(Workout workout) {
        em.remove(em.contains(workout) ? workout : em.merge(workout));
    }
}
