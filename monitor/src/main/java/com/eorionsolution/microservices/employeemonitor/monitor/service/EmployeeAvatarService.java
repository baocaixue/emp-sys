package com.eorionsolution.microservices.employeemonitor.monitor.service;

import com.eorionsolution.microservices.employeemonitor.monitor.domain.EmployeeAvatar;
import com.eorionsolution.microservices.employeemonitor.monitor.repository.EmployeeAvatarRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class EmployeeAvatarService {
    private final EmployeeAvatarRepository employeeAvatarRepository;

    public Mono<EmployeeAvatar> save(EmployeeAvatar employeeAvatar) {
        // for change existed Employee without new avatar upload case
        if (!StringUtils.isEmpty(employeeAvatar.getId()) && employeeAvatar.getInputAvatar().filename().isEmpty()) {
            return employeeAvatarRepository.findById(employeeAvatar.getId());
        } else {
            return employeeAvatar.parseInputAvatar().flatMap(employeeAvatarRepository::save);
        }
    }
}
