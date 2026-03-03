package com.ninju.dao;

import com.ninju.model.User;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@ApplicationScoped
public class UserDao {

    @PersistenceContext
    EntityManager em;

    @Transactional
    public void save(User user) {
        em.persist(user);
    }

    public User authenticate(String email, String password) {
        try {
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.email = :email AND u.password = :password", User.class);
            query.setParameter("email", email);
            query.setParameter("password", password);
            
            return query.getSingleResult();
            
        } catch (Exception e) {
            
            return null; 
        }
    }
}
