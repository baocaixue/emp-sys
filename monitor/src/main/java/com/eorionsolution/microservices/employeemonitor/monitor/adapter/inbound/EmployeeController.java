package com.eorionsolution.microservices.employeemonitor.monitor.adapter.inbound;

import com.eorionsolution.microservices.employeemonitor.monitor.domain.Employee;
import com.eorionsolution.microservices.employeemonitor.monitor.domain.EmployeeAvatar;
import com.eorionsolution.microservices.employeemonitor.monitor.domain.exception.DataNotExistException;
import com.eorionsolution.microservices.employeemonitor.monitor.repository.EmployeeRepository;
import com.eorionsolution.microservices.employeemonitor.monitor.service.EmployeeService;
import com.eorionsolution.microservices.employeemonitor.monitor.service.RefreshDurationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Mono;

@Controller
@AllArgsConstructor
@Slf4j
public class EmployeeController {
    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final RefreshDurationService refreshDurationService;

    @PostMapping(value = "/employee/add")
    public Mono<String> addEmployee(Mono<Employee> employee) {
        return employeeService.checkExist(employee)
                .flatMap(__ -> employeeService.save(employee))
                .flatMap(__ -> Mono.just("redirect:/"))
                .onErrorReturn(DuplicateKeyException.class, "redirect:/employee?duplicatekey");
    }

    @GetMapping(value = "/employee/checkSn/{sn}")
    @ResponseBody
    public Mono<String> checkExist(@PathVariable String sn) {
        return employeeService.checkExist(Mono.just(new Employee().setSn(sn)))
        .flatMap(__ -> Mono.just("ok"))
        .onErrorReturn(DuplicateKeyException.class, "duplicatekey");
    }

    @GetMapping(value = {"/", "/employeeList"})
    public String showEmployeeListPage(Model model) {
        model.addAttribute("employeeList",  new ReactiveDataDriverContextVariable(employeeService.findAll(), 10));
        model.addAttribute("emp", new Employee());
        return "employee-list";
    }

    @PostMapping(value = "/employee")
    public Mono<String> modifyEmployee(Mono<Employee> employee) {
        return employeeService.save(employee)
                .then(Mono.just("redirect:/"));
    }

    @PostMapping(value = "/employee/delete")
    public Mono<String> deleteEmployee(@ModelAttribute(value = "employee") Mono<Employee> employee) {
        return employeeService.deleteEmployeeCascadingWithAvatar(employee)
                .then(Mono.just("redirect:/"));
    }

    @GetMapping(value = "/employee")
    public String showCreateEmployeePage(Model model) {
        var emp = model.getAttribute("employee");
        if (emp == null)
            model.addAttribute("employee", new Employee().setEmployeeAvatar(new EmployeeAvatar()));
        else
            model.addAttribute("employee", ((Employee)emp).setSn(null));

        return "employee-edit";
    }

    @GetMapping(value = "/employee/{sn}")
    public Mono<String> showModifyEmployeePage(@PathVariable("sn") String sn,  Model model) {
        return employeeRepository.findById(sn)
                .switchIfEmpty(Mono.error(new DataNotExistException("没有该员工号信息")))
        .map(employee -> {
            model.addAttribute("employee", employee);
            return "employee-edit";
        });
    }

    @GetMapping(value = "/status")
    public Mono<String> showStatus(Model model) {
        return refreshDurationService.findOne().
                map(refreshDuration -> {
                    model.addAttribute("refreshDuration", refreshDuration);
                    return "status";
                });
    }

    @GetMapping(value = "/status/outList")
    public String showOutStatus(Model model) {
        model.addAttribute("outList", new ReactiveDataDriverContextVariable(employeeService.findEmployeesOutByDuration(), 10));
        return "status :: employee-out-list";
    }

    @GetMapping(value = "/status/inList")
    public String showInStatus(Model model) {
        model.addAttribute("inList", new ReactiveDataDriverContextVariable(employeeService.findEmployeesInByDuration(), 10));
        return "status :: employee-in-list";
    }
}
