package com.eorionsolution.microservices.employeemonitor.monitor.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalDatePeriodDTO {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    public static LocalDatePeriodDTO getInitialInstance() {
        var now = LocalDate.now();
        return new LocalDatePeriodDTO(now.with(firstDayOfMonth()), now.with(lastDayOfMonth()));
    }
}
