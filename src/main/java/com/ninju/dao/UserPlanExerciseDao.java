package com.ninju.dao;

import com.ninju.model.UserPlanExercise;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserPlanExerciseDao {

    @PersistenceContext
    EntityManager em;

    @Transactional
    public void save(UserPlanExercise exercise) {
        em.persist(exercise);
    }

    @Transactional
    public void delete(UserPlanExercise exercise) {
        em.remove(em.contains(exercise) ? exercise : em.merge(exercise));
    }

    public UserPlanExercise findById(Long id) {
        return em.find(UserPlanExercise.class, id);
    }
}
