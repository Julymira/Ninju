package com.ninju.bo;

import com.ninju.dao.AuditLogDao;
import com.ninju.dao.DailyLogDao;
import com.ninju.dao.UserDao;
import com.ninju.dto.DailyLogRequestDTO;
import com.ninju.dto.DailyLogResponseDTO;
import com.ninju.model.DailyLog;
import com.ninju.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class DailyLogBO {

    @Inject
    DailyLogDao dailyLogDao;

    @Inject
    UserDao userDao;

    @Inject
    AuditLogDao auditLogDao;

    public List<DailyLogResponseDTO> listByUser(Long userId, String executedBy) {
        auditLogDao.save("LISTAR_LOGS_USUARIO: " + userId, executedBy);
        return dailyLogDao.findByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Caso de uso 1: Registrar refeição do dia
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

    // Caso de uso 2: Registrar treino do dia
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

    private DailyLogResponseDTO toDTO(DailyLog d) {
        return new DailyLogResponseDTO(
                d.getId(),
                d.getLogDate(),
                d.getMealsNotes(),
                d.getWorkoutNotes(),
                d.getUser().getName()
        );
    }
}
