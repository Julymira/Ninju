package com.ninju.bo;

import com.ninju.dao.AuditLogDao;
import com.ninju.dao.UserDao;
import com.ninju.dao.WorkoutDao;
import com.ninju.dto.WorkoutDTO;
import com.ninju.model.User;
import com.ninju.model.Workout;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class WorkoutBO {

    @Inject WorkoutDao workoutDao;
    @Inject UserDao userDao;
    @Inject AuditLogDao auditLogDao;

    public List<WorkoutDTO> listVisible(Long userId, String executedBy) {
        auditLogDao.save("LISTAR_EXERCICIOS", executedBy);
        return workoutDao.findVisibleToUser(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public WorkoutDTO findById(Long id, String executedBy) {
        Workout w = workoutDao.findById(id);
        if (w == null) throw new IllegalArgumentException("Exercício não encontrado.");
        auditLogDao.save("BUSCAR_EXERCICIO: " + id, executedBy);
        return toDTO(w);
    }

    public WorkoutDTO create(Map<String, Object> body, Long requesterId, boolean isAdmin, String executedBy) {
        String name = (String) body.get("name");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Nome obrigatório.");
        String category = (String) body.get("category");
        if (category == null || category.isBlank()) throw new IllegalArgumentException("Categoria obrigatória.");
        String type = (String) body.get("exerciseType");
        if (!"MUSCULACAO".equals(type) && !"TEMPO".equals(type))
            throw new IllegalArgumentException("Tipo inválido. Use MUSCULACAO ou TEMPO.");

        Workout w = new Workout();
        w.setName(name.trim());
        w.setCategory(category.trim());
        w.setExerciseType(type);
        w.setCalorieFactor(((Number) body.get("calorieFactor")).doubleValue());

        boolean makeGlobal = isAdmin && Boolean.TRUE.equals(body.get("global"));
        if (makeGlobal) {
            w.setOwner(null);
        } else {
            User owner = userDao.findById(requesterId);
            if (owner == null) throw new IllegalStateException("Usuário não encontrado.");
            w.setOwner(owner);
        }

        workoutDao.save(w);
        auditLogDao.save("CRIAR_EXERCICIO: " + name, executedBy);
        return toDTO(w);
    }

    public void delete(Long id, Long requesterId, boolean isAdmin, String executedBy) {
        Workout w = workoutDao.findById(id);
        if (w == null) throw new IllegalArgumentException("Exercício não encontrado.");

        boolean isGlobal = w.getOwner() == null;
        boolean isOwner  = !isGlobal && w.getOwner().getId().equals(requesterId);

        if (!isAdmin && !isOwner) throw new SecurityException("Sem permissão para excluir este exercício.");

        workoutDao.delete(w);
        auditLogDao.save("EXCLUIR_EXERCICIO: " + id, executedBy);
    }

    public WorkoutDTO toDTO(Workout w) {
        Long ownerId = w.getOwner() != null ? w.getOwner().getId() : null;
        return new WorkoutDTO(w.getId(), w.getName(), w.getCategory(), w.getExerciseType(), w.getCalorieFactor(), ownerId);
    }
}
