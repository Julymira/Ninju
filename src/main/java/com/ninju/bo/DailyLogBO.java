package com.ninju.bo;

import com.ninju.dao.AuditLogDao;
import com.ninju.dao.DailyLogDao;
import com.ninju.dao.DailyLogEntryDao;
import com.ninju.dao.FoodDao;
import com.ninju.dao.UserDao;
import com.ninju.dto.DailyLogEntryRequestDTO;
import com.ninju.dto.DailyLogEntryResponseDTO;
import com.ninju.dto.DailyLogRequestDTO;
import com.ninju.dto.DailyLogResponseDTO;
import com.ninju.dto.DailyReportDTO;
import com.ninju.model.DailyLog;
import com.ninju.model.DailyLogEntry;
import com.ninju.model.Food;
import com.ninju.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class DailyLogBO {

    @Inject DailyLogDao dailyLogDao;
    @Inject DailyLogEntryDao entryDao;
    @Inject UserDao userDao;
    @Inject FoodDao foodDao;
    @Inject AuditLogDao auditLogDao;

    public List<DailyLogResponseDTO> listByUser(Long userId, String executedBy) {
        auditLogDao.save("LISTAR_LOGS_USUARIO: " + userId, executedBy);
        return dailyLogDao.findByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── Caso de uso 1: Registrar notas de refeição (texto livre) ──
    @Transactional
    public DailyLogResponseDTO registrarRefeicao(Long userId, DailyLogRequestDTO dto, String executedBy) {
        if (dto.mealsNotes == null || dto.mealsNotes.isBlank())
            throw new IllegalArgumentException("Notas da refeição são obrigatórias.");
        LocalDate date = dto.logDate != null ? dto.logDate : LocalDate.now();
        DailyLog log = obterOuCriarLog(userId, date);
        log.setMealsNotes(dto.mealsNotes);
        if (log.getId() == null) dailyLogDao.save(log);
        else dailyLogDao.update(log);
        auditLogDao.save("REGISTRAR_REFEICAO: " + date, executedBy);
        return toDTO(log);
    }

    // ── Caso de uso 2: Registrar treino ──
    @Transactional
    public DailyLogResponseDTO registrarTreino(Long userId, DailyLogRequestDTO dto, String executedBy) {
        if (dto.workoutNotes == null || dto.workoutNotes.isBlank())
            throw new IllegalArgumentException("Notas do treino são obrigatórias.");
        LocalDate date = dto.logDate != null ? dto.logDate : LocalDate.now();
        DailyLog log = obterOuCriarLog(userId, date);
        log.setWorkoutNotes(dto.workoutNotes);
        if (log.getId() == null) dailyLogDao.save(log);
        else dailyLogDao.update(log);
        auditLogDao.save("REGISTRAR_TREINO: " + date, executedBy);
        return toDTO(log);
    }

    // ── Caso de uso 3: Adicionar alimento ao diário ──
    @Transactional
    public DailyLogEntryResponseDTO addEntry(Long userId, DailyLogEntryRequestDTO dto, String executedBy) {
        if (dto.foodId == null)
            throw new IllegalArgumentException("Alimento é obrigatório.");
        if (dto.quantityGrams == null || dto.quantityGrams <= 0)
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        if (dto.mealType == null || !List.of("CAFE","ALMOCO","JANTAR","LANCHE").contains(dto.mealType))
            throw new IllegalArgumentException("Tipo de refeição inválido.");

        Food food = foodDao.findById(dto.foodId);
        if (food == null) throw new IllegalArgumentException("Alimento não encontrado.");

        LocalDate date = dto.logDate != null ? dto.logDate : LocalDate.now();
        DailyLog log = obterOuCriarLog(userId, date);
        if (log.getId() == null) dailyLogDao.save(log);

        DailyLogEntry entry = new DailyLogEntry();
        entry.setDailyLog(log);
        entry.setFood(food);
        entry.setQuantityGrams(dto.quantityGrams);
        entry.setMealType(dto.mealType);
        entryDao.save(entry);

        auditLogDao.save("ADICIONAR_ALIMENTO: " + food.getName() + " (" + dto.mealType + ")", executedBy);
        return toEntryDTO(entry);
    }

    // ── Caso de uso 4: Remover alimento do diário ──
    @Transactional
    public void removeEntry(Long userId, Long entryId, String executedBy) {
        DailyLogEntry entry = entryDao.findById(entryId);
        if (entry == null) throw new IllegalArgumentException("Item não encontrado.");
        if (!entry.getDailyLog().getUser().getId().equals(userId))
            throw new SecurityException("Sem permissão para remover este item.");
        entryDao.delete(entryId);
        auditLogDao.save("REMOVER_ALIMENTO: " + entryId, executedBy);
    }

    // ── Relatório nutricional diário ──
    public DailyReportDTO getReport(Long userId, LocalDate date, String executedBy) {
        User user = userDao.findById(userId);
        List<DailyLogEntry> entries = entryDao.findByUserAndDate(userId, date);

        List<DailyLogEntryResponseDTO> cafe   = filter(entries, "CAFE");
        List<DailyLogEntryResponseDTO> almoco = filter(entries, "ALMOCO");
        List<DailyLogEntryResponseDTO> jantar = filter(entries, "JANTAR");
        List<DailyLogEntryResponseDTO> lanche = filter(entries, "LANCHE");

        auditLogDao.save("VISUALIZAR_RELATORIO: " + date, executedBy);
        return new DailyReportDTO(date,
                round(sumCal(entries)), round(sumNutrient(entries, "prot")),
                round(sumNutrient(entries, "carb")), round(sumNutrient(entries, "fat")),
                user != null ? user.getCalorieMeta() : null,
                cafe, almoco, jantar, lanche);
    }

    // ── Helpers ──
    private DailyLog obterOuCriarLog(Long userId, LocalDate date) {
        DailyLog log = dailyLogDao.findByUserIdAndDate(userId, date);
        if (log == null) {
            User user = userDao.findById(userId);
            if (user == null) throw new IllegalArgumentException("Usuário não encontrado.");
            log = new DailyLog();
            log.setUser(user);
            log.setLogDate(date);
        }
        return log;
    }

    private List<DailyLogEntryResponseDTO> filter(List<DailyLogEntry> entries, String type) {
        return entries.stream()
                .filter(e -> type.equals(e.getMealType()))
                .map(this::toEntryDTO)
                .collect(Collectors.toList());
    }

    private double sumCal(List<DailyLogEntry> entries) {
        return entries.stream()
                .mapToDouble(e -> e.getFood().getCalories() * e.getQuantityGrams() / 100.0)
                .sum();
    }

    private double sumNutrient(List<DailyLogEntry> entries, String type) {
        return entries.stream().mapToDouble(e -> {
            double qty = e.getQuantityGrams() / 100.0;
            return switch (type) {
                case "prot" -> e.getFood().getProtein() * qty;
                case "carb" -> e.getFood().getCarbohydrates() * qty;
                case "fat"  -> e.getFood().getFat() * qty;
                default -> 0;
            };
        }).sum();
    }

    private double round(double v) { return Math.round(v * 10.0) / 10.0; }

    private DailyLogResponseDTO toDTO(DailyLog d) {
        return new DailyLogResponseDTO(d.getId(), d.getLogDate(),
                d.getMealsNotes(), d.getWorkoutNotes(), d.getUser().getName());
    }

    private DailyLogEntryResponseDTO toEntryDTO(DailyLogEntry e) {
        double qty = e.getQuantityGrams() / 100.0;
        Food f = e.getFood();
        return new DailyLogEntryResponseDTO(
                e.getId(), f.getId(), f.getName(), e.getMealType(),
                e.getQuantityGrams(),
                round(f.getCalories() * qty),
                round(f.getProtein() * qty),
                round(f.getCarbohydrates() * qty),
                round(f.getFat() * qty));
    }
}
