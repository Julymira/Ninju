package com.ninju.bo;

import com.ninju.dao.AuditLogDao;
import com.ninju.dao.FoodDao;
import com.ninju.dao.UserDao;
import com.ninju.dto.FoodDTO;
import com.ninju.model.Food;
import com.ninju.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class FoodBO {

    @Inject FoodDao foodDao;
    @Inject UserDao userDao;
    @Inject AuditLogDao auditLogDao;

    public List<FoodDTO> listVisible(Long userId, String executedBy) {
        auditLogDao.save("LISTAR_ALIMENTOS", executedBy);
        return foodDao.findVisibleToUser(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public FoodDTO findById(Long id, String executedBy) {
        Food food = foodDao.findById(id);
        if (food == null) throw new IllegalArgumentException("Alimento não encontrado.");
        auditLogDao.save("BUSCAR_ALIMENTO: " + id, executedBy);
        return toDTO(food);
    }

    public FoodDTO create(Map<String, Object> body, Long requesterId, boolean isAdmin, String executedBy) {
        String name = (String) body.get("name");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Nome obrigatório.");

        Food food = new Food();
        food.setName(name.trim());
        food.setCalories(((Number) body.get("calories")).intValue());
        food.setProtein(((Number) body.get("protein")).doubleValue());
        food.setCarbohydrates(((Number) body.get("carbohydrates")).doubleValue());
        food.setFat(((Number) body.get("fat")).doubleValue());

        boolean makeGlobal = isAdmin && Boolean.TRUE.equals(body.get("global"));
        if (makeGlobal) {
            food.setOwner(null); // global para todos
        } else {
            User owner = userDao.findById(requesterId);
            if (owner == null) throw new IllegalStateException("Usuário não encontrado.");
            food.setOwner(owner);
        }

        foodDao.save(food);
        auditLogDao.save("CRIAR_ALIMENTO: " + name, executedBy);
        return toDTO(food);
    }

    public void delete(Long id, Long requesterId, boolean isAdmin, String executedBy) {
        Food food = foodDao.findById(id);
        if (food == null) throw new IllegalArgumentException("Alimento não encontrado.");

        boolean isGlobal = food.getOwner() == null;
        boolean isOwner  = !isGlobal && food.getOwner().getId().equals(requesterId);

        if (!isAdmin && !isOwner) {
            throw new SecurityException("Sem permissão para excluir este alimento.");
        }

        foodDao.delete(food);
        auditLogDao.save("EXCLUIR_ALIMENTO: " + id, executedBy);
    }

    private FoodDTO toDTO(Food f) {
        Long ownerId = f.getOwner() != null ? f.getOwner().getId() : null;
        return new FoodDTO(f.getId(), f.getName(), f.getCalories(), f.getProtein(), f.getCarbohydrates(), f.getFat(), ownerId);
    }
}
