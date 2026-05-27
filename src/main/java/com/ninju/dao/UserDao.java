package com.ninju.dao;

import com.ninju.model.User;
import com.ninju.util.PasswordUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class UserDao {

    @PersistenceContext
    EntityManager em;

    @Transactional
    public void save(User user) {
        em.persist(user);
    }

    public User findById(Long id) {
        return em.find(User.class, id);
    }

    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u ORDER BY u.name", User.class).getResultList();
    }

    @Transactional
    public void update(User user) {
        em.merge(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = em.find(User.class, id);
        if (user != null) em.remove(user);
    }

    public User authenticate(String email, String plainPassword) {
        try {
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", email);

            User user = query.getSingleResult();

            if (!PasswordUtil.verify(plainPassword, user.getPassword())) {
                return null;
            }

            return user;

        } catch (NoResultException e) {
            return null;
        }
    }
}
