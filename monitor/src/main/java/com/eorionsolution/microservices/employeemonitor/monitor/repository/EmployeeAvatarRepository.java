package com.eorionsolution.microservices.employeemonitor.monitor.repository;

import com.eorionsolution.microservices.employeemonitor.monitor.domain.EmployeeAvatar;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface EmployeeAvatarRepository extends ReactiveMongoRepository<EmployeeAvatar, String> {
}
