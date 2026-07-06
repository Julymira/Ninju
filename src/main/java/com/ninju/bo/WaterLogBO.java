package com.ninju.bo;

import com.ninju.dao.WaterLogDao;
import com.ninju.dao.AuditLogDao;
import com.ninju.dao.UserDao;
import com.ninju.dto.WaterLogDTO;
import com.ninju.model.WaterLog;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;

@ApplicationScoped
public class WaterLogBO {
    
    @Inject WaterLogDao waterLogDao;
    @Inject UserDao userDao;
    @Inject AuditLogDao auditLogDao;

    public WaterLogDTO getTodayLog(Long userId, LocalDate date, String executedBy) {
        auditLogDao.save("HIDRATACAO_DIA: " + date, executedBy);

        WaterLog log = waterLogDao.findByUserAndDate(userId, date);

        if (log == null) {
            return null;
        }

        return new WaterLogDTO(log.getId(), log.getDate(), log.getAmountMl(), log.getGoalMl(),
        ((double) log.getAmountMl() / log.getGoalMl() * 100));
    }

    public WaterLogDTO addWater(Long userId, LocalDate date, int amountMl, String executedBy) {
        WaterLog log = waterLogDao.findByUserAndDate(userId, date);

        if(log == null){
            log = new WaterLog();
            log.setUser(userDao.findById(userId));
            log.setDate(date);
            log.setAmountMl(amountMl);
            log.setGoalMl(2000); // Default goal
            waterLogDao.save(log);

        }else{
            log.setAmountMl((log.getAmountMl() + amountMl));
            waterLogDao.save(log);
        }

        auditLogDao.save("ADICIONAR_AGUA: " + amountMl + "ml em " + date, executedBy);
        WaterLog updated = waterLogDao.findByUserAndDate(userId, date);
        return new WaterLogDTO(updated.getId(), updated.getDate(), updated.getAmountMl(), updated.getGoalMl(),
                ((double) updated.getAmountMl() / updated.getGoalMl() * 100));
    }

    public WaterLogDTO updateGoal(Long userId, LocalDate date, int goalMl, String executedBy) {
       WaterLog log = waterLogDao.findByUserAndDate(userId, date);

        if (log == null) {
            log = new WaterLog();
            log.setUser(userDao.findById(userId));
            log.setDate(date);
            log.setAmountMl(0);
        }

        log.setGoalMl(goalMl);
        waterLogDao.save(log);

        auditLogDao.save("ATUALIZAR_META_AGUA: " + goalMl + "ml em " + date, executedBy);
        WaterLog updated = waterLogDao.findByUserAndDate(userId, date);
        return new WaterLogDTO(updated.getId(), updated.getDate(), updated.getAmountMl(), updated.getGoalMl(),
                ((double) updated.getAmountMl() / updated.getGoalMl() * 100));
    }
}
