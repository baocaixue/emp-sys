package com.eorionsolution.microservices.employeemonitor.monitor.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.ZoneId;

@Document
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeSchedule {
    @Id
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    @DBRef
    private Employee departmentMorning;
    @DBRef
    private Employee departmentAfternoon;
    @DBRef
    private Employee salesMorning;
    @DBRef
    private Employee salesAfternoon;
    @DBRef
    private Employee serviceMorning;
    @DBRef
    private Employee serviceAfternoon;
    @DBRef
    private Employee redCrossMorning;
    @DBRef
    private Employee redCrossAfternoon;

    public static EmployeeSchedule fromScheduleDTO(ScheduleDTO dto) {
        return new EmployeeSchedule(dto.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                new Employee().setSn(dto.getDepartmentMorning()),
                new Employee().setSn(dto.getDepartmentAfternoon()),
                new Employee().setSn(dto.getSalesMorning()),
                new Employee().setSn(dto.getSalesAfternoon()),
                new Employee().setSn(dto.getServiceMorning()),
                new Employee().setSn(dto.getServiceAfternoon()),
                new Employee().setSn(dto.getRedCrossMorning()),
                new Employee().setSn(dto.getRedCrossAfternoon()));
    }
}
