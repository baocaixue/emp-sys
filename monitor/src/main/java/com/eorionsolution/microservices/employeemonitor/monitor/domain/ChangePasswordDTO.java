package com.eorionsolution.microservices.employeemonitor.monitor.domain;

import lombok.Data;

@Data
public class ChangePasswordDTO {
    private String oldPassword;
    private String newPassword;
}
