package com.ninju.startup;

import com.ninju.dao.WorkoutDao;
import com.ninju.model.Workout;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;


@ApplicationScoped
public class ExerciseCatalogInitializer {

    @Inject
    WorkoutDao workoutDao;

    @Transactional
    public void onStart(@Observes StartupEvent ev) {
        if (!workoutDao.findAll().isEmpty()) return;

        // Musculação: Gasto = séries × reps × carga(kg) × fator
        insert("Agachamento Livre",               "Musculação",   "MUSCULACAO", 0.02110);
        insert("Leg Press 45°",                   "Musculação",   "MUSCULACAO", 0.01313);
        insert("Puxada Alta",                     "Musculação",   "MUSCULACAO", 0.01219);
        insert("Supino Reto",                     "Musculação",   "MUSCULACAO", 0.01125);
        insert("Desenvolvimento",                 "Musculação",   "MUSCULACAO", 0.01125);
        insert("Cadeira Extensora",               "Musculação",   "MUSCULACAO", 0.00703);
        insert("Mesa Flexora",                    "Musculação",   "MUSCULACAO", 0.00703);
        insert("Rosca Direta",                    "Musculação",   "MUSCULACAO", 0.00633);
        insert("Tríceps Pulley",                  "Musculação",   "MUSCULACAO", 0.00633);

        // Cardio / Tempo: Gasto = duração(min) × kcal/min
        insert("Corrida Moderada (8 km/h)",        "Cardio",        "TEMPO", 7.00);
        insert("Corrida Intensa (12 km/h)",         "Cardio",        "TEMPO", 11.40);
        insert("Ciclismo Moderado (16-19 km/h)",    "Cardio",        "TEMPO", 5.80);
        insert("Ciclismo Intenso (>22 km/h)",       "Cardio",        "TEMPO", 10.00);
        insert("Corda (Pular)",                     "Cardio",        "TEMPO", 11.00);
        insert("Natação - Nado Crawl",              "Cardio",        "TEMPO", 7.00);
        insert("Caminhada Acelerada (6 km/h)",      "Cardio",        "TEMPO", 4.50);
        insert("Yoga - Hatha / Suave",              "Flexibilidade", "TEMPO", 2.50);
        insert("Yoga - Vinyasa / Ashtanga",         "Flexibilidade", "TEMPO", 5.80);
        insert("Pilates Solo",                      "Flexibilidade", "TEMPO", 3.30);
        insert("Pilates com Aparelhos",             "Flexibilidade", "TEMPO", 4.10);
        insert("Dança - Zumba",                     "Dança",         "TEMPO", 7.50);
        insert("Dança - Ballet Clássico",           "Dança",         "TEMPO", 4.60);
        insert("Alongamento / Mobilidade",          "Flexibilidade", "TEMPO", 2.00);
        insert("Artes Marciais (Muay Thai / Jiu-Jitsu)", "Luta",    "TEMPO", 10.00);
    }

    private void insert(String name, String category, String type, double factor) {
        Workout w = new Workout();
        w.setName(name);
        w.setCategory(category);
        w.setExerciseType(type);
        w.setCalorieFactor(factor);
        w.setOwner(null); // global
        workoutDao.save(w);
    }
}
