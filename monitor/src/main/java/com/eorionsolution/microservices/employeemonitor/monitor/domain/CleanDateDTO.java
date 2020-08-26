package com.eorionsolution.microservices.employeemonitor.monitor.domain;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class CleanDateDTO {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date cleanDate = new Date();
}
