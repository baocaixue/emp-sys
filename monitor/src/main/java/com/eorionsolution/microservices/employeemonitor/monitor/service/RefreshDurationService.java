package com.eorionsolution.microservices.employeemonitor.monitor.service;

import com.eorionsolution.microservices.employeemonitor.monitor.domain.RefreshDuration;
import com.eorionsolution.microservices.employeemonitor.monitor.repository.AdminRepository;
import com.eorionsolution.microservices.employeemonitor.monitor.repository.RefreshDurationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class RefreshDurationService {
    private final RefreshDurationRepository refreshDurationRepository;
    private final AdminRepository adminRepository;

    public Mono<RefreshDuration> init() {
        return refreshDurationRepository.save(new RefreshDuration("1", 1, "", "", 30));
    }

    public Mono<RefreshDuration> findOne() {
        return refreshDurationRepository.findById("1");
    }

    @Transactional
    public Mono<RefreshDuration> saveChange(RefreshDuration refreshDuration) {
        return adminRepository.findById("admin")
                .map(admin -> admin.setPwdExpireDateTime(admin.getPwdLastSetTime().plusDays(refreshDuration.getPwdDuration())))
                .flatMap(adminRepository::save)
                .flatMap(__ -> refreshDurationRepository.save(refreshDuration));
    }
}
