package com.ninju.bo;

import com.ninju.dao.AuditLogDao;
import com.ninju.dao.UserDao;
import com.ninju.dao.UserPlanDao;
import com.ninju.dao.UserPlanExerciseDao;
import com.ninju.dao.WorkoutDao;
import com.ninju.dto.UserPlanDTO;
import com.ninju.dto.UserPlanExerciseDTO;
import com.ninju.model.User;
import com.ninju.model.UserPlan;
import com.ninju.model.UserPlanExercise;
import com.ninju.model.Workout;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserPlanBO {

    static final int MAX_PLANS = 7;

    @Inject UserPlanDao planDao;
    @Inject UserPlanExerciseDao exerciseDao;
    @Inject WorkoutDao workoutDao;
    @Inject UserDao userDao;
    @Inject AuditLogDao auditLogDao;

    public List<UserPlanDTO> listPlans(Long userId, String executedBy) {
        auditLogDao.save("LISTAR_PLANOS", executedBy);
        return planDao.findByUserId(userId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public UserPlanDTO createPlan(Long userId, String name, String executedBy) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Nome do treino obrigatório.");
        long count = planDao.countByUserId(userId);
        if (count >= MAX_PLANS) throw new IllegalStateException("Limite de " + MAX_PLANS + " treinos atingido.");

        User user = userDao.findById(userId);
        if (user == null) throw new IllegalStateException("Usuário não encontrado.");

        UserPlan plan = new UserPlan();
        plan.setName(name.trim());
        plan.setUser(user);
        planDao.save(plan);

        auditLogDao.save("CRIAR_PLANO: " + name, executedBy);
        return toDTO(plan);
    }

    public UserPlanDTO renamePlan(Long planId, Long userId, String name, String executedBy) {
        UserPlan plan = getPlanOwned(planId, userId);
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Nome obrigatório.");
        plan.setName(name.trim());
        planDao.update(plan);
        auditLogDao.save("RENOMEAR_PLANO: " + planId, executedBy);
        return toDTO(plan);
    }

    public void deletePlan(Long planId, Long userId, String executedBy) {
        UserPlan plan = getPlanOwned(planId, userId);
        planDao.delete(plan);
        auditLogDao.save("EXCLUIR_PLANO: " + planId, executedBy);
    }

    public UserPlanDTO addExercise(Long planId, Long userId, Map<String, Object> body, String executedBy) {
        UserPlan plan = getPlanOwned(planId, userId);

        Long workoutId = ((Number) body.get("workoutId")).longValue();
        Workout workout = workoutDao.findById(workoutId);
        if (workout == null) throw new IllegalArgumentException("Exercício não encontrado.");

        UserPlanExercise ex = new UserPlanExercise();
        ex.setPlan(plan);
        ex.setWorkout(workout);

        if ("MUSCULACAO".equals(workout.getExerciseType())) {
            ex.setSets(((Number) body.get("sets")).intValue());
            ex.setReps(((Number) body.get("reps")).intValue());
            Object w = body.get("weightKg");
            if (w == null) throw new IllegalArgumentException("Carga (kg) obrigatória para exercícios de musculação.");
            ex.setWeightKg(((Number) w).doubleValue());
        } else {
            Object d = body.get("durationMinutes");
            if (d == null) throw new IllegalArgumentException("Duração obrigatória para exercícios por tempo.");
            ex.setDurationMinutes(((Number) d).doubleValue());
        }

        exerciseDao.save(ex);
        auditLogDao.save("ADICIONAR_EXERCICIO_PLANO: " + planId, executedBy);
        return toDTO(planDao.findById(planId));
    }

    public UserPlanDTO removeExercise(Long planId, Long userId, Long exerciseEntryId, String executedBy) {
        getPlanOwned(planId, userId);
        UserPlanExercise ex = exerciseDao.findById(exerciseEntryId);
        if (ex == null || !ex.getPlan().getId().equals(planId))
            throw new IllegalArgumentException("Exercício não encontrado neste plano.");
        exerciseDao.delete(ex);
        auditLogDao.save("REMOVER_EXERCICIO_PLANO: " + planId, executedBy);
        return toDTO(planDao.findById(planId));
    }

    private UserPlan getPlanOwned(Long planId, Long userId) {
        UserPlan plan = planDao.findById(planId);
        if (plan == null) throw new IllegalArgumentException("Plano não encontrado.");
        if (!plan.getUser().getId().equals(userId)) throw new SecurityException("Sem permissão.");
        return plan;
    }

    private UserPlanExerciseDTO toExerciseDTO(UserPlanExercise ex) {
        Workout w = ex.getWorkout();
        double calories = 0;

        if ("MUSCULACAO".equals(w.getExerciseType())) {
            // Gasto = séries × reps × carga(kg) × fator
            int s = ex.getSets() != null ? ex.getSets() : 0;
            int r = ex.getReps() != null ? ex.getReps() : 0;
            double kg = ex.getWeightKg() != null ? ex.getWeightKg() : 0;
            calories = s * r * kg * (w.getCalorieFactor() != null ? w.getCalorieFactor() : 0);
        } else {
            // Gasto = duração(min) × kcal/min
            double min = ex.getDurationMinutes() != null ? ex.getDurationMinutes() : 0;
            calories = min * (w.getCalorieFactor() != null ? w.getCalorieFactor() : 0);
        }

        return new UserPlanExerciseDTO(
                ex.getId(), w.getId(), w.getName(), w.getCategory(), w.getExerciseType(),
                ex.getSets(), ex.getReps(), ex.getWeightKg(), ex.getDurationMinutes(),
                Math.round(calories * 10.0) / 10.0);
    }

    private UserPlanDTO toDTO(UserPlan plan) {
        List<UserPlanExerciseDTO> exercises = plan.getExercises().stream()
                .map(this::toExerciseDTO)
                .collect(Collectors.toList());

        double total = exercises.stream().mapToDouble(e -> e.estimatedCalories != null ? e.estimatedCalories : 0).sum();
        return new UserPlanDTO(plan.getId(), plan.getName(), Math.round(total * 10.0) / 10.0, exercises);
    }
}
