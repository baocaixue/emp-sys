package com.eorionsolution.microservices.employeemonitor.monitor.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Select2ItemDTO {
    private String id;
    private String text;

    public static Select2ItemDTO fromEmployee(Employee employee) {
        return new Select2ItemDTO(employee.getSn(), employee.getName());
    }
}
