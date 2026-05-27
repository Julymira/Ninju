package com.ninju.util;

import com.ninju.model.DailyLog;
import com.ninju.model.User;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.time.LocalDate;

@ApplicationScoped
public class DataSeeder {

    @Inject
    EntityManager em;

    @Transactional
    void onStart(@Observes StartupEvent event) {
        if (em.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult() > 0) {
            return;
        }

        createUser("Admin Ninju", "admin@ninju.com", "password123", "ADMIN");
        User joao = createUser("João Silva", "joao@ninju.com", "password123", "USER");
        createUser("Maria Oliveira", "maria@ninju.com", "password123", "USER");

        createDailyLog(joao, LocalDate.now().minusDays(2),
                "Café: aveia + banana. Almoço: arroz, feijão, frango. Jantar: ovo mexido.",
                "Corrida 30 min + alongamento.");
        createDailyLog(joao, LocalDate.now().minusDays(1),
                "Café: ovo + batata doce. Almoço: salmão + arroz. Jantar: frango grelhado.",
                "Musculação - Peito.");
        createDailyLog(joao, LocalDate.now(),
                "Café: aveia + banana. Almoço: feijão + arroz + ovo.",
                null);
    }

    private User createUser(String name, String email, String plainPassword, String role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(PasswordUtil.hash(plainPassword));
        user.setRole(role);
        em.persist(user);
        return user;
    }

    private void createDailyLog(User user, LocalDate date, String meals, String workout) {
        DailyLog log = new DailyLog();
        log.setUser(user);
        log.setLogDate(date);
        log.setMealsNotes(meals);
        log.setWorkoutNotes(workout);
        em.persist(log);
    }
}
