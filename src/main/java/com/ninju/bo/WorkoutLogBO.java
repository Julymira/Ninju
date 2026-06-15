package com.ninju.bo;

import com.ninju.dao.AuditLogDao;
import com.ninju.dao.UserDao;
import com.ninju.dao.WorkoutDao;
import com.ninju.dao.WorkoutLogDao;
import com.ninju.dto.WorkoutLogDTO;
import com.ninju.dto.WorkoutLogExerciseDTO;
import com.ninju.model.Workout;
import com.ninju.model.WorkoutLog;
import com.ninju.model.WorkoutLogExercise;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class WorkoutLogBO {

    @Inject WorkoutLogDao workoutLogDao;
    @Inject WorkoutDao workoutDao;
    @Inject UserDao userDao;
    @Inject AuditLogDao auditLogDao;

    public List<WorkoutLogDTO> listByDate(Long userId, LocalDate date, String executedBy) {
        auditLogDao.save("LISTAR_TREINOS_DIA: " + date, executedBy);
        return workoutLogDao.findByUserAndDate(userId, date)
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public WorkoutLogDTO create(Map<String, Object> body, Long userId) {
        String planName = (String) body.get("planName");
        String dateStr  = (String) body.get("date");
        List<Map<String, Object>> exList = (List<Map<String, Object>>) body.get("exercises");

        if (planName == null || dateStr == null || exList == null) {
            throw new IllegalArgumentException("Campos obrigatórios ausentes.");
        }

        WorkoutLog log = new WorkoutLog();
        log.setUser(userDao.findById(userId));
        log.setLogDate(LocalDate.parse(dateStr));
        log.setPlanName(planName);

        for (Map<String, Object> ex : exList) {
            Long workoutId = ((Number) ex.get("workoutId")).longValue();
            Workout w = workoutDao.findById(workoutId);
            if (w == null) continue;

            WorkoutLogExercise wle = new WorkoutLogExercise();
            wle.setLog(log);
            wle.setWorkoutName(w.getName());
            wle.setExerciseType(w.getExerciseType());

            double cal;
            if ("MUSCULACAO".equals(w.getExerciseType())) {
                int sets    = ((Number) ex.get("sets")).intValue();
                int reps    = ((Number) ex.get("reps")).intValue();
                double kg   = ((Number) ex.get("weightKg")).doubleValue();
                wle.setSets(sets);
                wle.setReps(reps);
                wle.setWeightKg(kg);
                cal = sets * reps * kg * w.getCalorieFactor();
            } else {
                double min = ((Number) ex.get("durationMinutes")).doubleValue();
                wle.setDurationMinutes(min);
                cal = min * w.getCalorieFactor();
            }
            wle.setEstimatedCalories(cal);
            log.getExercises().add(wle);
        }

        workoutLogDao.save(log);
        auditLogDao.save("REGISTRAR_TREINO_LOG: " + planName, log.getUser().getEmail());
        return toDTO(log);
    }

    @Transactional
    public void delete(Long logId, Long userId, String executedBy) {
        WorkoutLog log = workoutLogDao.findById(logId);
        if (log == null) throw new IllegalArgumentException("Registro não encontrado.");
        if (!log.getUser().getId().equals(userId)) throw new SecurityException("Acesso negado.");
        auditLogDao.save("REMOVER_TREINO_LOG: " + logId, executedBy);
        workoutLogDao.delete(log);
    }

    private WorkoutLogDTO toDTO(WorkoutLog log) {
        List<WorkoutLogExerciseDTO> exercises = log.getExercises().stream().map(e ->
            new WorkoutLogExerciseDTO(
                e.getId(), e.getWorkoutName(), e.getExerciseType(),
                e.getSets(), e.getReps(), e.getWeightKg(),
                e.getDurationMinutes(), e.getEstimatedCalories()
            )
        ).collect(Collectors.toList());

        double total = exercises.stream().mapToDouble(e -> e.estimatedCalories).sum();
        return new WorkoutLogDTO(log.getId(), log.getPlanName(), total, exercises);
    }
}
