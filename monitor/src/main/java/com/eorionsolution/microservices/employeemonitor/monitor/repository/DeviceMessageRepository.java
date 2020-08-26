package com.eorionsolution.microservices.employeemonitor.monitor.repository;

import com.eorionsolution.microservices.employeemonitor.domain.DeviceMessage;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface DeviceMessageRepository extends ReactiveMongoRepository<DeviceMessage, String> {
    Flux<DeviceMessage> findDistinctMacByComingTimeBetween(LocalDateTime start, LocalDateTime end);
    Mono<Void> deleteAllByComingTimeIsBefore(LocalDateTime time);

    Flux<DeviceMessage> findByMacAndBleMacAndComingTimeBetween(String mac, String bleMac, LocalDateTime start, LocalDateTime end);
    Flux<DeviceMessage> findByBleMacAndComingTimeBetween(String bleMac, LocalDateTime start, LocalDateTime end);
}
