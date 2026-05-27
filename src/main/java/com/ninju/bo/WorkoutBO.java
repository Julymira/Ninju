package com.ninju.bo;

import com.ninju.dao.AuditLogDao;
import com.ninju.dao.WorkoutDao;
import com.ninju.dto.WorkoutDTO;
import com.ninju.model.Workout;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class WorkoutBO {

    @Inject
    WorkoutDao workoutDao;

    @Inject
    AuditLogDao auditLogDao;

    public List<WorkoutDTO> listAll(String executedBy) {
        auditLogDao.save("LISTAR_TREINOS", executedBy);
        return workoutDao.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public WorkoutDTO findById(Long id, String executedBy) {
        Workout workout = workoutDao.findById(id);
        if (workout == null) throw new IllegalArgumentException("Treino não encontrado.");
        auditLogDao.save("BUSCAR_TREINO: " + id, executedBy);
        return toDTO(workout);
    }

    private WorkoutDTO toDTO(Workout w) {
        return new WorkoutDTO(w.getId(), w.getName(), w.getCategory(), w.getEstimatedCaloriesBurned());
    }
}
