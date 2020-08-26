package com.eorionsolution.microservices.employeemonitor.monitor.repository;

import com.eorionsolution.microservices.employeemonitor.monitor.domain.Admin;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

public interface AdminRepository extends ReactiveMongoRepository<Admin, String> {
    Mono<UserDetails> findByUsername(String username);
}
