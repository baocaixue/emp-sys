package com.eorionsolution.microservices.employeemonitor.monitor.adapter.inbound;

import com.eorionsolution.microservices.employeemonitor.monitor.domain.Admin;
import com.eorionsolution.microservices.employeemonitor.monitor.domain.ChangePasswordDTO;
import com.eorionsolution.microservices.employeemonitor.monitor.domain.exception.InvalidPasswordException;
import com.eorionsolution.microservices.employeemonitor.monitor.service.AdminService;
import com.eorionsolution.microservices.employeemonitor.monitor.service.RefreshDurationService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Mono;

@Controller
@AllArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final RefreshDurationService refreshDurationService;

    @ResponseBody
    @PostMapping(value = "/admin/init", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Admin> initAdmin() {
        return refreshDurationService.init().flatMap(adminService::init);
    }

    @GetMapping(value = "/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping(value = "/strategyPassword/page")
    public String strategyPasswordPage(Model model) {
        model.addAttribute("password", new ChangePasswordDTO());
        return "change-password";
    }

    @GetMapping(value = "/strategyPassword/expirePassword")
    public Mono<String> expirePassword() {
        return Mono.just("redirect:/strategyPassword/page?expirepassword");
    }

    @GetMapping(value = "/strategyPassword/systemPassword")
    public Mono<String> systemPassword() {
        return Mono.just("redirect:/strategyPassword/page?resetpassword");
    }

    @GetMapping(value = "/strategyPassword/errorPassword")
    public Mono<String> errorPassword() {
        return Mono.just("redirect:/login?error");
    }

    @PostMapping(value = "/strategyPassword/changePassword")
    public Mono<String> changePassword(ChangePasswordDTO dto) {
        return adminService.changePassword(dto)
                .flatMap(x -> Mono.just("redirect:/login?changePasswordSuccess"))
                .onErrorResume(InvalidPasswordException.class, ex -> Mono.just("redirect:/strategyPassword/page?oldPasswordError"));
    }

    @GetMapping(value = "/open/busyday")
    public String busyDay() {
        return "busyday";
    }

    @GetMapping(value = "/open/normalday")
    public String normalDay() {
        return "normalday";
    }

    @GetMapping(value = "/open/peakday")
    public String peakDay() {
        return "peakday";
    }
}
