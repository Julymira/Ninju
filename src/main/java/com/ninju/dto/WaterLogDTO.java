package com.ninju.dto;

import java.time.LocalDate;

public class WaterLogDTO {
    public Long id;
    public LocalDate date;
    public Integer amountMl;
    public int goalMl;
    public double percentageAchieved;

    public WaterLogDTO(Long id, LocalDate date, Integer amountMl, int goalMl, double percentageAchieved) {
        this.id = id;
        this.date = date;
        this.amountMl = amountMl;
        this.goalMl = goalMl;
        this.percentageAchieved = percentageAchieved;
    }
    
}
