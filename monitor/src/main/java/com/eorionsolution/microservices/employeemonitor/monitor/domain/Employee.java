package com.eorionsolution.microservices.employeemonitor.monitor.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(value = "Employee")
@Data
@Accessors(chain = true)
public class Employee {
    @Id
    private String sn;

    private String name;
    private String position;
    @DBRef(lazy = true)
    private EmployeeAvatar employeeAvatar;
    private String mac;
    private Boolean isEmergencyMember = false;
    private Boolean enabled = true;
    private int priority;
}
