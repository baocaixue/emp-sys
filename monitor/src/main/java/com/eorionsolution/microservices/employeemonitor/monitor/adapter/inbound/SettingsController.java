package com.eorionsolution.microservices.employeemonitor.monitor.adapter.inbound;

import com.eorionsolution.microservices.employeemonitor.monitor.domain.*;
import com.eorionsolution.microservices.employeemonitor.monitor.domain.exception.InvalidPasswordException;
import com.eorionsolution.microservices.employeemonitor.monitor.repository.EmployeeScheduleRepository;
import com.eorionsolution.microservices.employeemonitor.monitor.service.AdminService;
import com.eorionsolution.microservices.employeemonitor.monitor.service.DeviceMessageService;
import com.eorionsolution.microservices.employeemonitor.monitor.service.RefreshDurationService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;

import java.time.ZoneId;

@Controller
@AllArgsConstructor
@Log4j2
public class SettingsController {
    private final AdminService adminService;
    private final RefreshDurationService refreshDurationService;
    private final DeviceMessageService deviceMessageService;
    private final EmployeeScheduleRepository employeeScheduleRepository;

    @GetMapping("/settings")
    public Mono<String> showSettingsPage(Model model) {

        return refreshDurationService.findOne()
                .map(refreshDuration -> {
                    model.addAttribute("password", new ChangePasswordDTO());
                    model.addAttribute("cleanDate", new CleanDateDTO());
                    model.addAttribute("cleanShiftDate", new CleanDateDTO());
                    model.addAttribute("refreshDuration", refreshDuration);
                    return "setting";
                });
    }

    @PostMapping("/change-password")
    public Mono<String> changePassword(ChangePasswordDTO dto) {
        return adminService.changePassword(dto)
                .flatMap(x -> Mono.just("redirect:/settings?changePasswordSuccess"))
                .onErrorResume(InvalidPasswordException.class, ex -> Mono.just("redirect:/settings?oldPasswordError"));
    }

    @PostMapping("/clean-data")
    public Mono<String> cleanData(CleanDateDTO dto) {
        return deviceMessageService.deleteBeforeDate(dto.getCleanDate())
                .then(Mono.just("redirect:/settings?cleanDataSuccess"));
    }

    @PostMapping("/clean-shift")
    public Mono<String> cleanShiftData(CleanDateDTO dto) {
        return employeeScheduleRepository.deleteAllByDateBefore(dto.getCleanDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .then(Mono.just("redirect:/settings?cleanShiftDataSuccess"));
    }

    @PostMapping("/change-refresh")
    public Mono<String> changeRefresh(RefreshDuration refreshDuration) {
        return refreshDurationService.saveChange(refreshDuration)
                .then(Mono.just("redirect:/settings?changeRefreshSuccess"));
    }
}
