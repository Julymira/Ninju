package com.ninju.dao;

import com.ninju.model.Food;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class FoodDao {

    @PersistenceContext
    EntityManager em;

    // Salvar um novo alimento no banco
    @Transactional
    public void save(Food food) {
        em.persist(food);
    }

    // Buscar um alimento específico pelo ID
    public Food findById(Long id) {
        return em.find(Food.class, id);
    }

    // Listar todos os alimentos cadastrados para mostrar no front-end
    public List<Food> findAll() {
        return em.createQuery("SELECT f FROM Food f", Food.class).getResultList();
    }
}