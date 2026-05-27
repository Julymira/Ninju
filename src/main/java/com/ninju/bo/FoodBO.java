package com.ninju.bo;

import com.ninju.dao.AuditLogDao;
import com.ninju.dao.FoodDao;
import com.ninju.dto.FoodDTO;
import com.ninju.model.Food;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class FoodBO {

    @Inject
    FoodDao foodDao;

    @Inject
    AuditLogDao auditLogDao;

    public List<FoodDTO> listAll(String executedBy) {
        auditLogDao.save("LISTAR_ALIMENTOS", executedBy);
        return foodDao.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public FoodDTO findById(Long id, String executedBy) {
        Food food = foodDao.findById(id);
        if (food == null) throw new IllegalArgumentException("Alimento não encontrado.");
        auditLogDao.save("BUSCAR_ALIMENTO: " + id, executedBy);
        return toDTO(food);
    }

    private FoodDTO toDTO(Food f) {
        return new FoodDTO(f.getId(), f.getName(), f.getCalories(), f.getProtein(), f.getCarbohydrates(), f.getFat());
    }
}
