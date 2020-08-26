package com.eorionsolution.microservices.employeemonitor.monitor.adapter.inbound;

import com.eorionsolution.microservices.employeemonitor.monitor.domain.EmployeeSchedule;
import com.eorionsolution.microservices.employeemonitor.monitor.domain.LocalDatePeriodDTO;
import com.eorionsolution.microservices.employeemonitor.monitor.domain.Select2ItemDTO;
import com.eorionsolution.microservices.employeemonitor.monitor.domain.exception.DataNotExistException;
import com.eorionsolution.microservices.employeemonitor.monitor.repository.EmployeeRepository;
import com.eorionsolution.microservices.employeemonitor.monitor.repository.EmployeeScheduleRepository;
import com.eorionsolution.microservices.employeemonitor.monitor.service.ScheduleChangeSseService;
import com.eorionsolution.microservices.employeemonitor.monitor.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor()
@Slf4j
public class SchedulerController {
    private final ScheduleService scheduleService;
    private final EmployeeScheduleRepository scheduleRepository;
    private final EmployeeRepository employeeRepository;
    private final ScheduleChangeSseService sseService;

    private LocalDatePeriodDTO localDatePeriodDTO = LocalDatePeriodDTO.getInitialInstance();

    @GetMapping(value = "/schedulerList")
    public String showSchedulerListPage(Model model) {
        model.addAttribute("period", localDatePeriodDTO);
        model.addAttribute("scheduleList",
                new ReactiveDataDriverContextVariable(
                        scheduleRepository.findAllByDateBetween(localDatePeriodDTO.getStartDate(),
                                localDatePeriodDTO.getEndDate()),
                        10));
        return "scheduler";
    }

    @GetMapping(value = "/scheduler/employee", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Flux<Select2ItemDTO> findEmployeeListForSelect(@RequestParam(value = "search", required = false) String term) {
        if (term != null) {
            return employeeRepository.findAllBySnLikeOrNameLike(term, term).map(Select2ItemDTO::fromEmployee);
        } else {
            return employeeRepository.findAll().limitRequest(10).map(Select2ItemDTO::fromEmployee);
        }
    }

    @GetMapping(value = "/scheduler")
    public Mono<String> showSchedulerModifyPage(Model model, @RequestParam(value = "date", required = false) String date) {
        if (date != null) {
            return scheduleRepository.findById(LocalDate.parse(date))
                    .switchIfEmpty(Mono.error(new DataNotExistException("没有该日期的排班计划")))
                    .doOnNext(s -> {
                        model.addAttribute("schedule", s);
                        model.addAttribute("empSchedule", new EmployeeSchedule());
                    })
                    .then(Mono.just("scheduler-edit"));
        } else {
            model.addAttribute("empSchedule", new EmployeeSchedule());
            model.addAttribute("schedule", new EmployeeSchedule());
            return Mono.just("scheduler-edit");
        }
    }

    @PostMapping(value = "/scheduler")
    public Mono<String> saveSingleScheduler(EmployeeSchedule employeeSchedule) {
        return scheduleRepository.save(employeeSchedule)
                .then(sseService.sendEvent("update"))
                .then(Mono.just("redirect:/schedulerList"));
    }

    @PostMapping(value = "/scheduler/delete")
    public Mono<String> deleteSingleScheduler(EmployeeSchedule employeeSchedule) {
        return scheduleRepository.deleteById(employeeSchedule.getDate())
                .then(sseService.sendEvent("upload"))
                .then(Mono.just("redirect:/schedulerList"));
    }

    @PostMapping(value = "/schedulerList")
    public String searchSchedulerList(LocalDatePeriodDTO period) {
        this.localDatePeriodDTO = period;
        return "redirect:/schedulerList";
    }

    @PostMapping(value = "/scheduler/upload")
    public Mono<String> uploadScheduler(FilePart file) {
        return scheduleService.parseUploadedSchedulerFile(file)
                .doOnComplete(() -> sseService.sendEvent("upload"))
                .then(Mono.just("redirect:/schedulerList"));
    }

    @GetMapping(value = "/shift")
    public Mono<String> showShiftPage(Model model) {
        model.addAttribute("emergencyMembers", new ReactiveDataDriverContextVariable(employeeRepository.findAllByIsEmergencyMember(true).sort((o1, o2) -> o2.getPriority() - o1.getPriority()), 10));
        return scheduleRepository.findById(LocalDate.now())
                .doOnNext(s -> {
                    model.addAttribute("scheduler", s);
                })
                .then(Mono.just("shift"));
    }

    @GetMapping(value = "/shift/change", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<ServerSentEvent<String>> sentChangeEvent() {
        return sseService.getEvent();
    }
}
