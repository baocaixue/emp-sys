package com.eorionsolution.microservices.employeemonitor.monitor.service;

import com.eorionsolution.microservices.employeemonitor.domain.DeviceMessage;
import com.eorionsolution.microservices.employeemonitor.monitor.domain.RefreshDuration;
import com.eorionsolution.microservices.employeemonitor.monitor.repository.DeviceMessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@AllArgsConstructor
public class DeviceMessageService {
    private final DeviceMessageRepository deviceMessageRepository;

    public Flux<DeviceMessage> getMessagesByDuration(Mono<RefreshDuration> refreshDurationMono) {
        return refreshDurationMono.flatMapMany(duration -> {
                    var start = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0));
                    var now = LocalDateTime.now();
                    var device2 = deviceMessageRepository.findByBleMacAndComingTimeBetween(duration.getMacAddr2(), start, now);
                    return device2.groupBy(DeviceMessage::getMac)
                            .flatMap(Flux::collectList)
                            .flatMap(list -> Flux.fromStream(list.stream().sorted((a, b) -> b.getComingTime().compareTo(a.getComingTime())).limit(1)))
                            .flatMap(msg2 ->
                                    deviceMessageRepository.findByMacAndBleMacAndComingTimeBetween(msg2.getMac(), duration.getMacAddr1(), start, now)
                                            .all(msg1 -> msg1.getComingTime().isBefore(msg2.getComingTime()))
                                            .flatMap(flag -> flag ? Mono.just(msg2) : Mono.empty()));
                }
        );
    }

    public Mono<Void> deleteBeforeDate(Date date) {
        return deviceMessageRepository.deleteAllByComingTimeIsBefore(
                LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
    }
}
