package com.ninju.dto;

import java.time.LocalDate;

public class DailyLogEntryRequestDTO {
    public LocalDate logDate;
    public Long foodId;
    public Double quantityGrams;
    public String mealType; // CAFE | ALMOCO | JANTAR | LANCHE
}
