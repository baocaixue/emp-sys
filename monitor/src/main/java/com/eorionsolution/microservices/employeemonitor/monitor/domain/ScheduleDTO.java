package com.eorionsolution.microservices.employeemonitor.monitor.domain;

import lombok.Data;

import java.util.Date;

@Data
public class ScheduleDTO {
    private Date date;
    private String departmentMorning;
    private String departmentAfternoon;
    private String salesMorning;
    private String salesAfternoon;
    private String serviceMorning;
    private String serviceAfternoon;
    private String redCrossMorning;
    private String redCrossAfternoon;
}
