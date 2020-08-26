package com.eorionsolution.microservices.employeemonitor.monitor.repository;

import com.eorionsolution.microservices.employeemonitor.monitor.domain.RefreshDuration;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface RefreshDurationRepository extends ReactiveMongoRepository<RefreshDuration, String> {
}
