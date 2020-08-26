package com.eorionsolution.microservices.employeemonitor.bleservlet.repository;

import com.eorionsolution.microservices.employeemonitor.domain.DeviceMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeviceMessageRepository extends MongoRepository<DeviceMessage, String> {
}
