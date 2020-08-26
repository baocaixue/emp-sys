package com.eorionsolution.microservices.employeemonitor.monitor.repository;

import com.eorionsolution.microservices.employeemonitor.monitor.domain.Employee;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EmployeeRepository extends ReactiveMongoRepository<Employee, String> {
    Mono<Employee> deleteBySn(String sn);

    Flux<Employee> findAllByMacInAndEnabledIsTrue(Flux<String> macFlux);
    Flux<Employee> findAllByMacNotInAndEnabledIsTrue(Flux<String> macFlux);

    Flux<Employee> findAllBySnLikeOrNameLike(String snLike, String nameLike);

    Flux<Employee> findAllByIsEmergencyMemberAndEnabled(Boolean isEmergencyMember, Boolean enabled);
    Flux<Employee> findAllByIsEmergencyMember(Boolean isEmergencyMember);
}
