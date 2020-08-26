package com.eorionsolution.microservices.employeemonitor.monitor.service;

import com.eorionsolution.microservices.employeemonitor.monitor.domain.Admin;
import com.eorionsolution.microservices.employeemonitor.monitor.domain.ChangePasswordDTO;
import com.eorionsolution.microservices.employeemonitor.monitor.domain.RefreshDuration;
import com.eorionsolution.microservices.employeemonitor.monitor.domain.exception.InvalidPasswordException;
import com.eorionsolution.microservices.employeemonitor.monitor.repository.AdminRepository;
import com.eorionsolution.microservices.employeemonitor.monitor.repository.RefreshDurationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Log4j2
public class AdminService {
    private final AdminRepository adminRepository;
    private final RefreshDurationRepository refreshDurationRepository;
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public Mono<Admin> init(RefreshDuration duration) {
        return Mono.just(LocalDateTime.now())
                .map(now ->
                        new Admin("admin", passwordEncoder.encode(randomPwd()))
                        .setPwdLastSetTime(now)
                        .setPwdExpireDateTime(now.plusDays(duration.getPwdDuration()))
                )
                .flatMap(adminRepository::save);
    }

    private static CharSequence randomPwd() {
        var generator = new PasswordGenerator();
        var lowerCase = EnglishCharacterData.LowerCase;
        var characterRule = new CharacterRule(lowerCase);
        characterRule.setNumberOfCharacters(3);

        var digit = EnglishCharacterData.Digit;
        var digitRule = new CharacterRule(digit);
        digitRule.setNumberOfCharacters(5);

        String password = generator.generatePassword(8, characterRule, digitRule);
        log.info("Current init password is {}", password);
        return password;
    }

    public Mono<Admin> changePassword(ChangePasswordDTO dto) {
        return refreshDurationRepository.findById("1")
                .flatMap(duration ->
                        adminRepository.findById("admin")
                                .map(it ->
                                        it.setPwdLastSetTime(LocalDateTime.now())
                                                .setPwdExpireDateTime(LocalDateTime.now().plusDays(duration.getPwdDuration()))
                                )
                )
                .filter(admin -> passwordEncoder.matches(dto.getOldPassword(), admin.getPassword()))
                .map(admin -> admin.setPassword(passwordEncoder.encode(dto.getNewPassword())).setSysPwd(false))
                .switchIfEmpty(Mono.error(InvalidPasswordException::new))
                .flatMap(adminRepository::save);

    }
}
