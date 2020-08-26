package com.eorionsolution.microservices.employeemonitor.monitor.repository;

import com.eorionsolution.microservices.employeemonitor.monitor.domain.EmployeeSchedule;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface EmployeeScheduleRepository extends ReactiveMongoRepository<EmployeeSchedule, LocalDate> {
    Flux<EmployeeSchedule> findAllByDateBetween(LocalDate startDate, LocalDate endDate);
    Flux<EmployeeSchedule> deleteAllByDateBefore(LocalDate date);
}
