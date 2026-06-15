package com.ninju.dto;

import java.time.LocalDate;
import java.util.List;

public class DailyReportDTO {
    public LocalDate date;
    public Double totalCalories;
    public Double totalProtein;
    public Double totalCarbs;
    public Double totalFat;
    public Integer calorieMeta;
    public List<DailyLogEntryResponseDTO> cafe;
    public List<DailyLogEntryResponseDTO> almoco;
    public List<DailyLogEntryResponseDTO> jantar;
    public List<DailyLogEntryResponseDTO> lanche;

    public DailyReportDTO(LocalDate date, Double totalCalories, Double totalProtein,
                          Double totalCarbs, Double totalFat, Integer calorieMeta,
                          List<DailyLogEntryResponseDTO> cafe,
                          List<DailyLogEntryResponseDTO> almoco,
                          List<DailyLogEntryResponseDTO> jantar,
                          List<DailyLogEntryResponseDTO> lanche) {
        this.date = date;
        this.totalCalories = totalCalories;
        this.totalProtein = totalProtein;
        this.totalCarbs = totalCarbs;
        this.totalFat = totalFat;
        this.calorieMeta = calorieMeta;
        this.cafe = cafe;
        this.almoco = almoco;
        this.jantar = jantar;
        this.lanche = lanche;
    }
}
