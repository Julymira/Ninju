package com.ninju.dto;

import java.time.LocalDate;

public class DailyLogResponseDTO {
    public Long id;
    public LocalDate logDate;
    public String mealsNotes;
    public String workoutNotes;
    public String userName;

    public DailyLogResponseDTO(Long id, LocalDate logDate, String mealsNotes, String workoutNotes, String userName) {
        this.id = id;
        this.logDate = logDate;
        this.mealsNotes = mealsNotes;
        this.workoutNotes = workoutNotes;
        this.userName = userName;
    }
}
