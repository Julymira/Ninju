package com.ninju.dao;

import com.ninju.model.Workout;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
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
}
