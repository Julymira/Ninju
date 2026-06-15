package com.ninju.dao;

import com.ninju.model.UserPlan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class UserPlanDao {

    @PersistenceContext
    EntityManager em;

    @Transactional
    public void save(UserPlan plan) {
        em.persist(plan);
    }

    @Transactional
    public UserPlan update(UserPlan plan) {
        return em.merge(plan);
    }

    @Transactional
    public void delete(UserPlan plan) {
        em.remove(em.contains(plan) ? plan : em.merge(plan));
    }

    public UserPlan findById(Long id) {
        return em.find(UserPlan.class, id);
    }

    public List<UserPlan> findByUserId(Long userId) {
        return em.createQuery("SELECT p FROM UserPlan p WHERE p.user.id = :uid", UserPlan.class)
                .setParameter("uid", userId)
                .getResultList();
    }

    public long countByUserId(Long userId) {
        return em.createQuery("SELECT COUNT(p) FROM UserPlan p WHERE p.user.id = :uid", Long.class)
                .setParameter("uid", userId)
                .getSingleResult();
    }
}
